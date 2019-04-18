package com.xzj.ims.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author xuzhijun.online  
 * @date 2019年4月14日
 */
public class ImageUtil {
	public static byte[] mat2byte(Mat mat) {
		int byteSize = mat.channels()*mat.rows()*mat.cols();
		byte[] res = new byte[byteSize];
		mat.get(0, 0, res);
		return res;
	}
	public static BufferedImage mat2BufferedImage(Mat matrix)throws Exception {        
	    MatOfByte mob=new MatOfByte();
	    Imgcodecs.imencode(".jpg", matrix, mob);
	    byte ba[]=mob.toArray();
	    BufferedImage bi=ImageIO.read(new ByteArrayInputStream(ba));
	    return bi;
	}
}
