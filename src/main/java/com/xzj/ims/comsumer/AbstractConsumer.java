package com.xzj.ims.comsumer;

import org.apache.log4j.Logger;

import com.xzj.ims.cache.Cache;
import com.xzj.ims.cache.Face;
import com.xzj.ims.core.ImsThreadPool;
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
	
	public AbstractConsumer() {
		// TODO Auto-generated constructor stub
	}
	public AbstractConsumer(Partition<? extends Record<K,V>> partition) {
		this.partition = partition;
		this.cache = new Cache<T>();
	}
	
	abstract public void beforeConsume() throws Exception;
	
	abstract public void consume() throws Exception;
	
	@Override
	public void run() {
		try {
			beforeConsume();
			int count = 0;
			while (ProducerThreadPool.getInstance().isTerminated() == false) {
				//消费完毕后要释放Mat对象，否则会内存溢出
				consume();
				if(count++ == 100) {
					count = 0;
					System.gc();
				}
			}
			if(ImsThreadPool.getInstance().getActiveCount() == 1) {
				ImsThreadPool.getInstance().shutdown();
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
