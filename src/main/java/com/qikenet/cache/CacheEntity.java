package com.qikenet.cache;

import java.io.Serializable;

/**
 * 缓存实体
 * 
 * @author panjing
 * @date 2016年8月6日 上午9:33:25
 * @project qikenet-group-cache
 */
public class CacheEntity implements Serializable {

	private static final long serialVersionUID = 2082223810638865724L;

	private String key; // key

	private Object value;// 值

	private Long timestamp;// 缓存的时候存的时间戳，用来计算该元素是否过期

	private int seconds = 0; // 默认长期有效

	private Group group;// 容器

	public CacheEntity(String key, Object value, Long timestamp, int seconds, Group group) {
		super();
		this.key = key;
		this.value = value;
		this.timestamp = timestamp;
		this.seconds = seconds;
		this.group = group;
	}

	public void setTimestamp(Long timestamp) {

		this.timestamp = timestamp;
	}

	public Long getTimestamp() {

		return timestamp;
	}

	public String getKey() {

		return key;
	}

	public void setKey(String key) {

		this.key = key;
	}

	public Object getValue() {

		return value;
	}

	public void setValue(Object value) {

		this.value = value;
	}

	
	public void setSeconds(int seconds) {

		this.seconds = seconds;
	}
	
	public int getSeconds() {

		return seconds;
	}
	/**
	 * 获取剩余时间
	 * 
	 * @return
	 */
	public int ttl() {

		if (this.seconds == 0) {
			return this.seconds;
		}
		return this.seconds - getTime();
	}
	
	/**
	 * 获取当前时间和元素的相差时间
	 * @return
	 */
	private int getTime() {

		if (this.seconds == 0) {
			return this.seconds;
		}
		Long current = System.currentTimeMillis();
		Long value = current - this.timestamp;
		return (int) (value / 1000) + 1;
	}

	/**
	 * 是否到期
	 * 
	 * @return
	 */
	public boolean isExpire() {

		if (this.seconds == 0) {
			return true;
		}
		if (getTime() > this.seconds) {
			// 失效了就移除
			group.delete(key);
			return false;
		}
		return true;
	}
}
