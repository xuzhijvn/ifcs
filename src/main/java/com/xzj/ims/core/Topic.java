package com.xzj.ims.core;

import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @author xuzhijun.online 
 * @date 2019年4月13日
 *
 * @param <K>
 * @param <V>
 */
@Setter
@Getter
public class Topic<K,V> extends ConcurrentHashMap<String, Partition<ProducerRecord<K,V>>>{

	private static final long serialVersionUID = 9195161824395771120L;
	
	private String name;
	
	private int partitionCapacity;
	
	public Topic(String name, String[] partitionKeys) {
		if(partitionKeys == null || partitionKeys.length == 0) {
			return;
		}
		this.name = name;
		for (int i = 0; i < partitionKeys.length; i++) {
			this.put(partitionKeys[i], new Partition<ProducerRecord<K,V>>(partitionKeys[i]));
		}
	}

}
