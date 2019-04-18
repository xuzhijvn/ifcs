package com.xzj.ims.cache;


import lombok.Getter;
import lombok.Setter;

/**
 * @author xuzhijun.online  
 * @date 2019年4月16日
 */
@Setter
@Getter
public abstract class Face<K,V> {
	
	protected K cameraId;
	protected V frame;
	protected V face;
	protected String gray;
	protected float[] feature;
	protected boolean isValid = false;
	
	public Face(K cameraId, V frame, V face, String gray) {
		this.cameraId = cameraId;
		this.frame = frame;
		this.face = face;
		this.gray = gray;
	}

}
