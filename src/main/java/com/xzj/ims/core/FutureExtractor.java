package com.xzj.ims.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;


/**
 * Class to extract Object from Future
 * @author xuzhijun.online 
 * @date 2019年4月13日
 *
 * @param <T>
 */
public class FutureExtractor<T> implements Runnable{
	
	private static final Logger logger = Logger.getLogger(FutureExtractor.class);

	private BlockingQueue<Future<T>> futures;
	private BlockingQueue<T> queues;
	private Future<T> future;
	
	public FutureExtractor(BlockingQueue<Future<T>> futures, BlockingQueue<T> queues) {
		this.futures = futures;
		this.queues = queues;
	}
	
	public FutureExtractor(Future<T> future, BlockingQueue<T> queues) {
		this.future = future;
		this.queues = queues;
	}
	
	@Override
	public void run() {
		logger.info("Extract Object from Future...");
		try {
			extract();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	private void extract() throws Exception {
		if(futures != null) {
			//futures队列
			while(!futures.isEmpty()) {
				Future<T> future = futures.take();
				if(future.isDone() && future.get() != null) {
					queues.put(future.get());
				}else {
					futures.put(future);
				}
			}
			logger.info("Extract Object from Futures is done!");
		}else {
			//future
			while(future != null) {
				if(future.isDone()) {
					queues.put(future.get());
					logger.info("Extract Object from Future is done!");
					break;
				}
			}
		}
	}
}
