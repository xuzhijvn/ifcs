package com.xzj.ims.comsumer;

import org.apache.log4j.Logger;

import com.xzj.ims.cache.Cache;
import com.xzj.ims.cache.Face;
import com.xzj.ims.core.Partition;
import com.xzj.ims.core.Record;

/**
 * @author xuzhijun.online  
 * @date 2019年4月16日
 */
abstract public class AbstractConsumer<T extends Face<K, V>, K, V> implements Consumer<T>{
	
	private static final Logger logger = Logger.getLogger(AbstractConsumer.class);
	protected Cache<T> cache;
	protected Partition<? extends Record<K,V>> partition;
	
	public AbstractConsumer(Partition<? extends Record<K,V>> partition) {
		this.partition = partition;
		this.cache = new Cache<T>();
	}
	
	abstract public void beforeConsume();
	
	abstract public void consume() throws Exception;
	
	@Override
	public void run() {
		beforeConsume();
		while(true){
			try {
				consume();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error(e.getMessage());
			}
		}
	}
}
