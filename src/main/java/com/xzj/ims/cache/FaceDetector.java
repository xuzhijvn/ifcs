package com.xzj.ims.cache;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import com.icbc.bas.ai.common.Constant;
import com.icbc.bas.ai.face.FaceVertify;
/**
 * 
 * @author xuzhijun.online
 * @date 2019年4月13日
 *
 */
public class FaceDetector {
	
	private static final Logger logger = Logger.getLogger(FaceDetector.class);

	private  CascadeClassifier CASCADE_CLASSIFIER = new CascadeClassifier();

	public FaceDetector() {
		// 加载人脸检测模型
		CASCADE_CLASSIFIER.load(Constant.TMP + "bas-ai-tools\\model\\haarcascade_frontalface_alt.xml");
	}
	



	public List<Mat> detect(Mat frame) {
		List<Mat> res = new ArrayList<Mat>();
		//转灰
//		Mat gray=new Mat(frame.rows(),frame.cols(),frame.type());
//		Imgproc.cvtColor(frame, gray, Imgproc.COLOR_RGB2GRAY);
		MatOfRect faceDetections = new MatOfRect();
		CASCADE_CLASSIFIER.detectMultiScale(frame, faceDetections, 1.05, 6, 0, new Size(64,64));
		for (Rect rect : faceDetections.toArray()) {
//			Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
//					new Scalar(0, 0, 255));
			Mat sub = frame.submat(rect);
			res.add(sub);
//			Imgcodecs.imwrite("C:\\Users\\GoneBoy\\Desktop\\test\\"+System.currentTimeMillis()+".jpg", sub);
		}
		return res;
	}

	public String align(Mat face) throws IOException {
		String tmpFilePath = Constant.TMP + "bas-ai-tools\\tmp\\";
		try {
			FileUtils.forceMkdir(new File(tmpFilePath));
		} catch (IOException e) {
			logger.error("Fail, create tmp file dir is fail.", e);
			throw new IOException("Create tmp file dir is fail.", e);
		}
		String outImgFile = tmpFilePath + System.currentTimeMillis() + ".jpg";
		FaceVertify.align(face, 0, face.cols(), 0, face.rows(), outImgFile);
		return outImgFile;
	}

	public List<String> align(List<Mat> faces) throws IOException {
		List<String> grays = new ArrayList<String>();
		for (Mat face : faces) {
			grays.add(align(face));
		}
		return grays;
	}
}
