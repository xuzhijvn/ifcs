package com.xzj.ims.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
/**
 * 
 * @author xuzhijun.online 
 * @date 2019年4月13日
 *
 */
public class ProducerThreadPool extends ThreadPoolExecutor {

	private static final ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("producer-pool-%d").build();
	
	private static ProducerThreadPool PRODUCER_THREAD_POOL;

	private ProducerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	private ProducerThreadPool(int corePoolSize) {
		
		this(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(1), NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());
	}
	
	private ProducerThreadPool() throws Exception{
		//一个生产者
		this(1);
	}

	public static ProducerThreadPool getInstance() throws Exception {
		if(PRODUCER_THREAD_POOL == null) {
			PRODUCER_THREAD_POOL = new ProducerThreadPool();
		}
		return PRODUCER_THREAD_POOL;
	}

	public void execute(Runnable command, long delay) throws InterruptedException {
		// TODO Auto-generated method stub
		Thread.sleep(delay);
		super.execute(command);
	}
}
