/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.xzj.ims.core;


/**
 * 
 * @author xuzhijun.online 
 * @date 2019年4月13日
 *
 * @param <K>
 * @param <V>
 */
public class ProducerRecord<K, V> extends Record<K, V>{

	public ProducerRecord(Topic<K, V> topic, Partition<ProducerRecord<K, V>> partition, Long timestamp, K key,
			V value) {
		super(topic, partition, timestamp, key, value);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2332484525409792065L;


   
}
