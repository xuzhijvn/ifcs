package com.xzj.ims.comsumer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import com.xzj.ims.cache.DefalutFace;
import com.xzj.ims.cache.Face;
import com.xzj.ims.cache.FaceDetector;
import com.xzj.ims.core.Partition;
import com.xzj.ims.core.Record;
import com.xzj.ims.util.PropertyFileReader;


/**
 * 
 * @author xuzhijun.online 
 * @date 2019年4月16日
 *
 * @param <T>
 * @param <K>
 * @param <V>
 */
public class DefalutConsumer extends AbstractConsumer<Face<String, Mat>, String, Mat>{
	
	private static final Logger logger = Logger.getLogger(DefalutConsumer.class);
	private static  String ROOT_DIR;

	private String faceDir;
	private String grayDir;
	private String frameDir;
	
	public DefalutConsumer(Partition<? extends Record<String, Mat>> partition) throws Exception {
		super(partition);
		ROOT_DIR = PropertyFileReader.readPropertyFile().getProperty("save.path");
	}

	@Override
	public void beforeConsume() {
		faceDir = ROOT_DIR+Thread.currentThread().getName() + "\\face\\";
		grayDir = ROOT_DIR+Thread.currentThread().getName() + "\\gray\\";
		frameDir = ROOT_DIR+Thread.currentThread().getName() + "\\frame\\";
		try {
			FileUtils.forceMkdir(new File(faceDir));
			FileUtils.forceMkdir(new File(grayDir));
			FileUtils.forceMkdir(new File(frameDir));
			logger.info("Created file dir for face, gray, feame image is success.");
		} catch (IOException e) {
			logger.error("Create file dir for face, gray, feame image is fail.", e);
			e.printStackTrace();
		}
	}

	@Override
	public void consume() throws Exception {
		Record<String, Mat> record = partition.take();
		logger.info("Consumeing cameraId " + record.key());
		Mat frame = record.value();
		List<Mat> faces = FaceDetector.getInstance().detect(frame);
		if(faces.size() == 0) {
			return;
		}
		
//		Imgcodecs.imwrite("C:\\Users\\GoneBoy\\Desktop\\test\\"+record.timestamp()+"-T-"+System.currentTimeMillis()+".jpg", frame);
		
		List<Face<String, Mat>> facesObj = new ArrayList<Face<String, Mat>>();
		for (Mat face : faces) {
			String gray = FaceDetector.getInstance().align(face);
			Face<String, Mat> faceObj = new DefalutFace<String, Mat>(record.key(),frame, face, gray);
			if(faceObj.isValid()) {
				facesObj.add(faceObj);
			}
		}
		
		cache.add(facesObj, t -> {
			String facePath = faceDir + record.key() + "-T-" + record.timestamp() + "-face.png";
			String grayPath = grayDir + record.key() + "-T-" + record.timestamp() + "-gray.png";
			String framePath = frameDir + record.key() + "-T-" + record.timestamp() + "-frame.png";
			
			Imgcodecs.imwrite(facePath, t.getFace());
			logger.warn("Saving face to " + facePath);
			
			Imgcodecs.imwrite(grayPath, Imgcodecs.imread(t.getGray()));
			logger.warn("Saving gray to " + grayPath);
			
			Imgcodecs.imwrite(framePath, t.getFrame());
			logger.warn("Saving frame to " + framePath);
			});
		
	}
}
