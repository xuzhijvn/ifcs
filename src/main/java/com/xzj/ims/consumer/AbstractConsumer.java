package com.xzj.ims.consumer;

import org.apache.log4j.Logger;

import com.xzj.ims.cache.Cache;
import com.xzj.ims.cache.Face;
import com.xzj.ims.cache.FaceDetector;
import com.xzj.ims.core.ConsumerThreadPool;
import com.xzj.ims.core.Partition;
import com.xzj.ims.core.ProducerThreadPool;
import com.xzj.ims.core.Record;

/**
 * @author xuzhijun.online  
 * @date 2019年4月16日
 */
abstract public class AbstractConsumer<T extends Face<K, V>, K, V> implements Consumer<T>{
	
	private static final Logger logger = Logger.getLogger(AbstractConsumer.class);
	protected Cache<T> cache;
	protected Partition<? extends Record<K,V>> partition;
	FaceDetector faceDetetor;
	
	public AbstractConsumer() {
		// TODO Auto-generated constructor stub
	}
	public AbstractConsumer(Partition<? extends Record<K,V>> partition, FaceDetector faceDetetor) {
		this.partition = partition;
		this.cache = new Cache<T>();
		this.faceDetetor = faceDetetor;
	}
	
	abstract public void beforeConsume() throws Exception;
	
	abstract public void consume() throws Exception;
	
	@Override
	public void run() {
		try {
			beforeConsume();
			int count = 0;
			//生产者线程关闭，消费者线程也关闭
			while (ProducerThreadPool.getInstance().isTerminated() == false) {
				long start = System.currentTimeMillis();
				logger.info("Consumer starting...: "+Thread.currentThread().getName());
				consume();
				logger.info("Consumer finish: "+(System.currentTimeMillis() - start) + "ms was spent! ");
				//消费完毕后要释放Mat对象，否则会内存溢出
				if(count++ == 15) {
					count = 0;
					System.gc();
				}
			}
			//最后一个消费者线程执行关闭线程池操作
			if(ConsumerThreadPool.getInstance().getActiveCount() == ConsumerThreadPool.getInstance().getCompletedTaskCount()) {
				logger.info(Thread.currentThread().getName()+" is the last task, consumer thread pool will be shutdown!!!");
				ConsumerThreadPool.getInstance().shutdownNow();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
