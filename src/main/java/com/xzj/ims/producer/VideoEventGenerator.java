package com.xzj.ims.producer;


import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.videoio.Videoio;
import com.xzj.ims.core.FutureExtractor;
import com.xzj.ims.core.ProducerRecord;
import com.xzj.ims.core.ProducerThreadPool;
import com.xzj.ims.core.Topic;
import com.xzj.ims.core.UtilThreadPool;

/**
 * Class to convert Video Frame into byte array and generate record object event using.
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
			e.printStackTrace();
			logger.error(e.getMessage());
		}finally {
			try {
				ProducerThreadPool.getInstance().shutdown();
				UtilThreadPool.getInstance().shutdown();
				logger.info("Thread pool of producer and util is be shutdown!!!");
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * generate ProducerRecord events for frame
	 * @throws Exception
	 */
	private void generateEvent() throws Exception {

		while (connects.isEmpty() == false || UtilThreadPool.getInstance().getTaskCount() > 0) {
			//1, 从连接队列非阻塞的取连接
			CameraConnect connect = connects.poll();
			if(connect == null) {
				connects.offer(connect);
				continue;
			}
			//2, 如果读帧失败，网络流发起重连，离线视频流退出系统
			String cameraId = connect.getCameraId();
			Mat frame = new Mat();
			boolean flag = connect.getCamera().read(frame);
			String url = connect.getUrl();
			if(flag == false ) {
				double frameCount = connect.getCamera().get(Videoio.CAP_PROP_FRAME_COUNT);
				if(frameCount > 0) {
					logger.info(cameraId+": total frmae count =  "+frameCount);
					logger.info("Finished, read frame from cameraId " + cameraId + " with url " + url);
					continue;
				}
				logger.error("Failed, read frame from cameraId " + cameraId + " with url " + url);
				//reconnect
				logger.info("Reconnect camera..., cameraId "+ cameraId + " with url " + url);
				connect.release();
				Future<CameraConnect> future = UtilThreadPool.getInstance().submit(new CameraConnect(cameraId, url));
				//异步把连接取出来
				UtilThreadPool.getInstance().execute(new FutureExtractor<CameraConnect>(future, connects));
				continue;
			}
			//3, 每一秒取一帧
			int fps = (int) Math.floor(connect.getCamera().get(Videoio.CV_CAP_PROP_FPS));
			if(connect.getPolledTimes() != fps) {
				connect.setPolledTimes(connect.getPolledTimes() + 1);
				logger.info(cameraId +": The connect was taken out for the "+connect.getPolledTimes()+"rd time, fps = "+fps);
				connects.offer(connect);
				continue;
			}
			connect.setPolledTimes(0);
			//4, 构造帧记录插入到消费者队列
			long ts = System.currentTimeMillis();
			String timestamp = new Timestamp(ts).toString();
			ProducerRecord<String, Mat> record = new ProducerRecord<String, Mat>(topic, topic.get(cameraId), ts ,cameraId, frame);
			// resize image before sending
//			Imgproc.resize(mat, mat, new Size(640, 480), 0, 0, Imgproc.INTER_CUBIC);
			//离线视频流阻塞式的放入数据到队列，网络视频流非阻塞式放入
			if(connect.getCamera().get(Videoio.CAP_PROP_FRAME_COUNT) > 0) {
				topic.get(cameraId).put(record);
				logger.info("Generated events for cameraId = " + cameraId + " timestamp = " + timestamp);
			}else {
				boolean stage = topic.get(cameraId).offer(record);
				//插入失败说明消费者队列现在无法消费，可能是消费者消费能力不够
				if(stage == true) {
					logger.info("Generated a event for cameraId = " + cameraId + " timestamp = " + timestamp);
				}else {
					logger.warn(cameraId+": a event is dropped, consumer is busy...");
				}
			}
			connects.offer(connect);
		}
	}

}
