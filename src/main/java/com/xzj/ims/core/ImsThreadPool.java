package com.xzj.ims.core;

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
public class ImsThreadPool extends ThreadPoolExecutor {

	private static final ThreadFactory NAMED_THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("ai-video-pool-%d").build();
	
	private static ImsThreadPool AI_VIDEO_THREAD_POOL;

	private ImsThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
	}

	private ImsThreadPool(int corePoolSize) {
		
		this(corePoolSize, corePoolSize + 4, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(6), NAMED_THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());
	}
	
	private ImsThreadPool() throws Exception{
		//一个生产者、N个消费者、一个重连线程
		this(PropertyFileReader.readPropertyFile().getProperty("camera.url").split(",").length + 2);
	}

	public static ImsThreadPool getInstance() throws Exception {
		if(AI_VIDEO_THREAD_POOL == null) {
			AI_VIDEO_THREAD_POOL = new ImsThreadPool();
		}
		return AI_VIDEO_THREAD_POOL;
	}

	public void execute(Runnable command, long delay) throws InterruptedException {
		// TODO Auto-generated method stub
		Thread.sleep(delay);
		super.execute(command);
	}

}
