package com.qikenet.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 组操作，内部数据有序队列
 * 
 * @author panjing
 * @date 2016年8月6日 上午9:33:34
 * @project qikenet-group-cache
 * @param <T>
 */
public class Group {

	private ArrayBlockingQueue<CacheEntity> queue;// 缓存队列

	private Integer capacity;

	public Group(int capacity) {
		queue = new ArrayBlockingQueue<CacheEntity>(capacity);
		this.capacity = capacity;
	}

	/**
	 * 尾部进
	 * 
	 * @param object
	 * @param second
	 */
	public void push(String key, Object object, int second) {

		// 放入队列，
		queue.offer(new CacheEntity(key, object, System.currentTimeMillis(), second, this));
	}

	/**
	 * 尾部进
	 * 
	 * @param object
	 */
	public void push(String key, Object object) {

		push(key, object, 0);
	}

	/**
	 * 返回并移除头部出
	 * 
	 * @return
	 */
	public Object poll() {

		CacheEntity entity = queue.poll();
		// 如果有效期超过，返回null
		if (!entity.isExpire()) {
			return null;
		}
		return entity.getValue();
	}

	/**
	 * 返回头部元素并放到末尾
	 * 
	 * @return
	 */
	public Object rPoll() {

		CacheEntity entity = queue.poll();
		// 如果有效期超过，返回null
		if (!entity.isExpire()) {
			return null;
		}
		Object object = entity.getValue();
		queue.offer(entity);
		return object;
	}

	/**
	 * 通过key寻找有效的缓存实体
	 * 
	 * @param key
	 * @return
	 */
	private CacheEntity find(String key) {

		synchronized (queue) {
			Iterator<CacheEntity> iterator = queue.iterator();
			while (iterator.hasNext()) {
				CacheEntity entity = iterator.next();
				if (key.equals(entity.getKey())) {
					return entity;
				}
			}
			return null;
		}
	}

	/**
	 * 删除key
	 * 
	 * @param key
	 */
	public void delete(String key) {

		synchronized (queue) {
			CacheEntity entity = find(key);
			if (entity != null) {
				queue.remove(entity);
			}
		}
	}

	/**
	 * 根据key获取
	 * 
	 * @param key
	 * @return
	 */
	public Object getValue(String key) {

		CacheEntity entity = find(key);
		if (entity != null && entity.isExpire()) {
			return entity.getValue();
		}

		return null;
	}

	/**
	 * 获取有效的缓存实体
	 * 
	 * @return
	 */
	private List<CacheEntity> getCacheEntitys() {

		List<CacheEntity> keys = new ArrayList<CacheEntity>();
		Iterator<CacheEntity> iterator = queue.iterator();
		while (iterator.hasNext()) {
			CacheEntity cacheEntity = iterator.next();
			if (cacheEntity.isExpire()) {
				keys.add(cacheEntity);
			}
		}
		return keys;
	}

	/**
	 * 获取key列表
	 * 
	 * @return
	 */
	public List<String> getKeys() {

		List<String> keys = new ArrayList<String>();
		List<CacheEntity> caches = getCacheEntitys();
		for (CacheEntity cacheEntity : caches) {
			keys.add(cacheEntity.getKey());
		}
		return keys;
	}

	/**
	 * 获取值列表
	 * 
	 * @return
	 */
	public List<Object> getValues() {

		List<Object> values = new ArrayList<Object>();
		List<CacheEntity> caches = getCacheEntitys();
		for (CacheEntity cacheEntity : caches) {
			values.add(cacheEntity.getValue());
		}
		return values;
	}

	/**
	 * 查看元素存活时间，-1 失效，0 长期有效
	 * 
	 * @param key
	 * @return
	 */
	public int ttl(String key) {

		CacheEntity entity = find(key);
		if (entity != null) {
			return entity.ttl();
		}
		return -1;
	}

	/**
	 * 返回头部的元素
	 * 
	 * @return
	 */
	public Object peek() {

		CacheEntity entity = queue.peek();
		if (entity != null) {
			return entity.getValue();
		}
		return null;
	}

	/**
	 * 设置元素存活时间
	 * 
	 * @param key
	 * @param second
	 */
	public void expire(String key, int second) {

		CacheEntity entity = find(key);
		if (entity != null) {
			entity.setTimestamp(System.currentTimeMillis());
			entity.setSeconds(second);
		}
	}

	/**
	 * 查看key是否存在
	 * 
	 * @param key
	 * @return
	 */
	public boolean exist(String key) {

		return find(key) != null;
	}

	/**
	 * 查看组是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty() {

		return queue.isEmpty();
	}

	/**
	 * 获取存活元素的大小
	 * 
	 * @return
	 */
	public int size() {

		return getCacheEntitys().size();
	}

	/**
	 * 获取容量
	 * 
	 * @return
	 */
	public Integer getCapacity() {

		return capacity;
	}
}
