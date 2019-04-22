/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional inforVion regarding copyright ownership. The ASF licenses this file
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

import java.io.Serializable;

/**
 * 
 * @author xuzhijun.online 
 * @date 2019年4月13日
 *
 * @param <K>
 * @param <V>
 */
abstract public class Record<K, V> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2574914518181778705L;
	private final Topic<K, V> topic;
    private final Partition<? extends Record<K, V>> partition;
    private final K key;
    private final V value;
    private final Long timestamp;

    /**
     * Creates a record with a specified timestamp to be sent to a specified topic and partition
     * 
     * @param topic The topic the record will be appended to
     * @param partition The partition to which the record should be sent
     * @param timestamp The timestamp of the record
     * @param key The key that will be included in the record
     * @param value The record contents
     */
    public Record(Topic<K, V> topic, Partition<? extends Record<K, V>> partition, Long timestamp, K key, V value) {
        if (topic == null) {
        	throw new IllegalArgumentException("Topic cannot be null.");
        }
        if (timestamp != null && timestamp < 0) {
        	throw new IllegalArgumentException(
        			String.format("Invalid timestamp: %d. Timestamp should always be non-negative or null.", timestamp));
        }

        this.topic = topic;
        this.partition = partition;
        this.key = key;
        this.value = value;
        this.timestamp = timestamp;
    }

    /**
     * Creates a record to be sent to a specified topic and partition
     *
     * @param topic The topic the record will be appended to
     * @param partition The partition to which the record should be sent
     * @param key The key that will be included in the record
     * @param value The record contents
     */
    public Record(Topic<K, V> topic, Partition<? extends Record<K, V>> partition, K key, V value) {
        this(topic, partition, null, key, value);
    }

    /**
     * Create a record to be sent to Kafka
     * 
     * @param topic The topic the record will be appended to
     * @param key The key that will be included in the record
     * @param value The record contents
     */
    public Record(Topic<K, V> topic, K key, V value) {
        this(topic, null, null, key, value);
    }

    /**
     * Create a record with no key
     * 
     * @param topic The topic this record should be sent to
     * @param value The record contents
     */
    public Record(Topic<K, V> topic, V value) {
        this(topic, null, null, null, value);
    }

    /**
     * @return The topic this record is being sent to
     */
    public Topic<K, V> topic() {
        return topic;
    }

    /**
     * @return The key (or null if no key is specified)
     */
    public K key() {
        return key;
    }

    /**
     * @return The value
     */
    public V value() {
        return value;
    }

    /**
     * @return The timestamp
     */
    public Long timestamp() {
        return timestamp;
    }

    /**
     * @return The partition to which the record will be sent (or null if no partition was specified)
     */
    public Partition<? extends Record<K, V>> partition() {
        return partition;
    }

    @Override
    public String toString() {
        String key = this.key == null ? "null" : this.key.toString();
        String value = this.value == null ? "null" : this.value.toString();
        String timestamp = this.timestamp == null ? "null" : this.timestamp.toString();
        return "ProducerRecord(topic=" + topic + ", partition=" + partition + ", key=" + key + ", value=" + value +
            ", timestamp=" + timestamp + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
        	return true;
        }
        else if (!(o instanceof Record)) {
        	return false;
        }
            
        Record<?, ?> that = (Record<?, ?>) o;

        if (key != null ? !key.equals(that.key) : that.key != null) {
            return false;
        }
        else if (partition != null ? !partition.equals(that.partition) : that.partition != null) {
        	return false;
        } else if (topic != null ? !topic.equals(that.topic) : that.topic != null) {
        	return false;
        }else if (value != null ? !value.equals(that.value) : that.value != null) {
        	return false;
        }else if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) {
        	return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = topic != null ? topic.hashCode() : 0;
        result = 31 * result + (partition != null ? partition.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
