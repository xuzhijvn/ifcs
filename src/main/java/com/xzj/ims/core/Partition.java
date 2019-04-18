package com.xzj.ims.core;


import java.util.concurrent.SynchronousQueue;

import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @author xuzhijun.online 
 * @date 2019年4月13日
 *
 * @param <T>
 */
@Getter
@Setter
public class Partition<T> extends SynchronousQueue<T>{

	private static final long serialVersionUID = -3267228815026589339L;
	
	private String partitionKey;
	
	public Partition(String partitionKey) {
		super();
		this.partitionKey = partitionKey;
	}

}
