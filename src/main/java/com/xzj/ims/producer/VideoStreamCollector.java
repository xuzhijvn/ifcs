package com.xzj.ims.producer;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;

import com.icbc.bas.ai.face.FileUtils;
import com.xzj.ims.comsumer.DefalutConsumer;
import com.xzj.ims.core.ImsThreadPool;
import com.xzj.ims.core.ProducerThreadPool;
import com.xzj.ims.core.FutureExtractor;
import com.xzj.ims.core.Topic;
import com.xzj.ims.util.PropertyFileReader;

/**
 * 
 * @author xuzhijun.online 
 * @date 2019年4月13日
 *
 */
public class VideoStreamCollector {
	
	static {
		FileUtils.loadDll("bas-ai-tools\\win32-x86-64\\bin\\opencv_ffmpeg345_64.dll");
		FileUtils.loadDll("bas-ai-tools\\win32-x86-64\\bin\\opencv_java345.dll");
	}	

	private static final Logger logger = Logger.getLogger(VideoStreamCollector.class);
	
	public static void main(String[] args) throws Exception {

		Properties prop = PropertyFileReader.readPropertyFile();
		String[] urls = prop.getProperty("camera.url").split(",");
		String[] ids = prop.getProperty("camera.id").split(",");
		if (urls.length != ids.length) {
			throw new Exception("There should be same number of camera Id and url");
		}
		logger.info("Total urls to process " + urls.length);
		//bulid connect asynchronous
		final BlockingQueue<Future<CameraConnect>> futures = new LinkedBlockingQueue<Future<CameraConnect>>();
		for (int i = 0; i < urls.length; i++) {
			futures.offer(ImsThreadPool.getInstance().submit(new CameraConnect(ids[i].trim(), urls[i].trim())));
		}
		//initialize topic 
		final Topic<String, Mat> topic = new Topic<String, Mat>("ai-video-topic-1",ids);
		//extract CameraConnect from Future asynchronous
		final BlockingQueue<CameraConnect> connects = new LinkedBlockingQueue<CameraConnect>(urls.length);
		ImsThreadPool.getInstance().execute(new FutureExtractor<CameraConnect>(futures, connects));
		//sleep 2s, wait connects have value, one thread responsible for polling to read all connects
		ProducerThreadPool.getInstance().execute(new VideoEventGenerator(connects, topic),1000);
		//sleep 3s, wait partition have value
		Thread.sleep(2000);
		//one thread consumes one partition
		for (int i = 0; i < ids.length; i++) {
			ImsThreadPool.getInstance().execute(new DefalutConsumer(topic.get(ids[i])));
		}
	}
}
