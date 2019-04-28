package com.xzj.ims.core;

import java.io.Serializable;
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
public class UtilThreadPool extends ThreadPoolExecutor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("util-pool-%d").build();
	
	private static volatile UtilThreadPool INSTANCE;

	private UtilThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	private UtilThreadPool(int corePoolSize) {
		
		this(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(30), NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());
	}
	
	private UtilThreadPool() throws Exception{
		this(1);
		if (INSTANCE != null) {
			throw new IllegalStateException("Already instantiated");
		}
	}

	public static UtilThreadPool getInstance() throws Exception {
		if(INSTANCE == null) {
			synchronized(UtilThreadPool.class) {
				if(INSTANCE == null) {
					INSTANCE = new UtilThreadPool();
				}
			}
		}
		return INSTANCE;
	}
	
	@SuppressWarnings("unused")
	private UtilThreadPool readResolve() {
		return INSTANCE;
	}

	public void execute(Runnable command, long delay) throws InterruptedException {
		// TODO Auto-generated method stub
		Thread.sleep(delay);
		super.execute(command);
	}
}
