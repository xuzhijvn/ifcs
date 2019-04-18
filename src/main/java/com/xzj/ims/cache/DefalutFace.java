package com.xzj.ims.cache;


import com.icbc.bas.ai.face.FaceVertify;

/**
 * @author xuzhijun.online  
 * @date 2019年4月14日
 */


public class DefalutFace<K,V> extends Face<K,V>{
	
	public DefalutFace(K cameraId, V frame, V face, String gray) {
		super(cameraId, frame, face, gray);
		this.feature = extractFeature(gray);
	}
	
	private float[] extractFeature(String gray) {
		float[] feature = new float[512];
		int ret = FaceVertify.getFaceFeature(gray, feature);
		if(ret == 1) {
			this.isValid = true;
		}
		return feature;
	}
	
	@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return 1;
		}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		DefalutFace<K,V> other = (DefalutFace<K,V>) obj;
		float sorce = FaceVertify.vertify(this.feature, other.feature);
//		System.out.println("sorce = "+sorce);
		if(sorce < 80) {
			return false;
		}
		return true;
	}
}
