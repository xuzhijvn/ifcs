package com.xzj.ims.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

/**
 * @author xuzhijun.online  
 * @date 2019年4月14日
 */

@Setter
@Getter
public class Cache<T extends Face<?, ?>> {

	Map<T,Boolean> cache = new ConcurrentHashMap<T, Boolean>();
	
	public void add(List<T> faces, Consumer<T> consumer) {
		Map<T,Boolean> tmp = new ConcurrentHashMap<T, Boolean>();
		for (T face : faces) {
			tmp.put(face,true);
			boolean flag = cache.containsKey(face);
//			System.out.println("缓存是否已经存在该人脸："+flag);
			if(!flag) {
				consumer.accept(face);
			}
		}
		this.cache = tmp;
	}
	
	public void clear() {
		cache.clear();
	}
}
