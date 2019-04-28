package com.xzj.ims.core;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xzj.ims.util.PropertyFileReader;
/**
 * 
 * @author xuzhijun.online 
 * @date 2019年4月13日
 *
 */
public class ConsumerThreadPool extends ThreadPoolExecutor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("consumer-pool-%d").build();
	
	private static volatile ConsumerThreadPool INSTANCE;

	private ConsumerThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	private ConsumerThreadPool(int corePoolSize) {
		this(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(10), NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());
	}
	
	private ConsumerThreadPool() throws Exception{
		//N个消费者
		this(PropertyFileReader.readPropertyFile().getProperty("camera.url").split(",").length);
		if (INSTANCE != null) {
			throw new IllegalStateException("Already instantiated");
		}
	}

	public static ConsumerThreadPool getInstance() throws Exception {
		if(INSTANCE == null) {
			synchronized(ConsumerThreadPool.class) {
				if(INSTANCE == null) {
					INSTANCE = new ConsumerThreadPool();
				}
			}
		}
		return INSTANCE;
	}
	
	@SuppressWarnings("unused")
	private ConsumerThreadPool readResolve() {
		return INSTANCE;
	}

	public void execute(Runnable command, long delay) throws InterruptedException {
		// TODO Auto-generated method stub
		Thread.sleep(delay);
		super.execute(command);
	}
}
