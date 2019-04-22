package com.xzj.ims.producer;

import java.util.concurrent.Callable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.opencv.videoio.VideoCapture;

import lombok.Getter;
import lombok.Setter;
/**
 * Class to connect to Video camera url.
 * @author xuzhijun.online 
 * @date 2019年4月13日
 *
 */
@Setter
@Getter
public class CameraConnect implements Callable<CameraConnect> {
	
	private static final Logger logger = Logger.getLogger(CameraConnect.class);
	
	private String cameraId;
	private String url;
	private VideoCapture camera;
	private int polledTimes;
	
	public CameraConnect(String cameraId, String url) {
		this.cameraId = cameraId;
		this.url = url;
	}
	
	
	@Override
	public CameraConnect call() throws Exception {
		logger.info("Connecting cameraId " + cameraId + " with url " + url);
		try {
			return connectCamera(cameraId, url);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}
	
	private CameraConnect connectCamera(String cameraId, String url) throws Exception{
		if(StringUtils.isBlank(cameraId) || StringUtils.isBlank(cameraId)) {
			return null;
		}
		VideoCapture camera = null;
		if (StringUtils.isNumeric(url)) {
			camera = new VideoCapture(Integer.parseInt(url));
		} else {
			camera = new VideoCapture(url);
		}
		// check camera working
		if (!camera.isOpened()) {
			Thread.sleep(5000);
			if (!camera.isOpened()) {
				throw new Exception("Error opening cameraId " + cameraId + " with url=" + url + ".Set correct file path or url in camera.url key of property file.");
			}
		}
		this.setCamera(camera);
		return this;
	}
	
	public void release() {
		this.camera.release();
//		this.release();
	}

}
