package com.xzj.ims.producer;


import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;

import com.xzj.ims.core.ImsThreadPool;
import com.xzj.ims.core.FutureExtractor;
import com.xzj.ims.core.ProducerRecord;
import com.xzj.ims.core.Topic;

/**
 * Class to convert Video Frame into byte array and generate JSON event using.
 * @author xuzhijun.online 
 * @date 2019年4月12日
 *
 */
public class VideoEventGenerator implements Runnable {
	
	private static final Logger logger = Logger.getLogger(VideoEventGenerator.class);
	private BlockingQueue<CameraConnect> connects;
	private Topic<String, Mat> topic;

	public VideoEventGenerator(BlockingQueue<CameraConnect> connects, Topic<String, Mat> topic) {
		this.connects = connects;
		this.topic = topic;
	}
	
	@Override
	public void run() {
		try {
			generateEvent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	/**
	 * generate ProducerRecord events for frame
	 * @throws Exception
	 */
	private void generateEvent() throws Exception {

		while (true) {
			//从连接队列阻塞的取连接
			CameraConnect connect = connects.take();
			String cameraId = connect.getCameraId();

			Mat mat = new Mat();
			boolean flag = connect.getCamera().read(mat);
			String url = connect.getUrl();
			long ts = System.currentTimeMillis();
			String timestamp = new Timestamp(ts).toString();
			if(flag == false) {
				logger.error("Failed read frame from cameraId " + cameraId + " with url " + url);
				System.exit(0);
				connect.release();
				//reconnect
				Future<CameraConnect> future = ImsThreadPool.getInstance().submit(new CameraConnect(cameraId, url));
				//把连接取出来
				ImsThreadPool.getInstance().execute(new FutureExtractor<CameraConnect>(future, connects));
				logger.info("Reconnect camera..., cameraId "+ cameraId + " with url " + url);
				continue;
			}
			
			if(connect.getPolledTimes() != 25) {
				connect.setPolledTimes(connect.getPolledTimes() + 1);
				connects.offer(connect);
				continue;
			}
			connect.setPolledTimes(0);
			
//			if(!topic.get(cameraId).isEmpty()) {
//				continue;
//			}
			
			// resize image before sending
//			Imgproc.resize(mat, mat, new Size(640, 480), 0, 0, Imgproc.INTER_CUBIC);
			logger.info("Processing cameraId " + cameraId + " with url " + url);
			topic.get(cameraId).put(new ProducerRecord<String, Mat>(topic, topic.get(cameraId),ts ,cameraId, mat.clone()));
			mat.release();
			connects.offer(connect);
			logger.info("Generated events for cameraId = " + cameraId + " timestamp = " + timestamp);
		}
	}
	
//	boolean isShouldExit(BlockingQueue<CameraConnect> connects) {
//		
//	}


}
