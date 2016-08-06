package com.qikenet.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * 简单的内存缓存实现，实现group概念，<br/>
 * 一个group里面是个有序的集合，<br/>
 * 集合支持key-value expire弥补redis list的不足
 * 
 * @author panjing
 * @date 2016年8月6日 上午9:27:22
 * @project qikenet-group-cache
 */
public class GroupCacheFactory {

	// 数据容器
	private Map<String, Object> container;

	public GroupCacheFactory() {
		container = new LinkedHashMap<String, Object>();
	}

	/**
	 * 如果组存在就返回，不存在就创建，保证不为null
	 * 
	 * @param key
	 * @return
	 */
	public Group group(String key, int capacity) {

		Group group = null;
		Object entry = container.get(key);
		if (entry != null) {
			group = (Group) entry;
		} else {
			group = new Group(capacity);
			container.put(key, group);
		}

		return group;
	}

	/**
	 * 如果组存在就返回，不存在就创建，默认容量1000
	 * 
	 * @param key
	 * @return
	 */
	public Group group(String key) {

		return this.group(key, 1000);
	}

	public static void main(String[] args) {

		GroupCacheFactory factory = new GroupCacheFactory();
		Group group = factory.group("abc",1000);
		for(int i=0;i<1000;i++){
			group.push(String.valueOf(i), i, new Random().nextInt(100));
		}
		
		for (int i = 0; i < 150; i++) {
			System.out.println(group.getKeys());
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		group.push("one", "你好", 15);
//		group.push("t", "tt", 3);
//		group.push("长期", "长期有效", 1);

//		for (int i = 0; i < 20; i++) {
//			System.out.println(group.ttl("abc"));
//			System.out.println(group.getKeys());
//			System.out.println(group.getValues());
//			if(i==3){
//				group.expire("abc", 10);
//			}
//			try {
//				Thread.sleep(1000);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		for (int i = 0; i < 20; i++) {
//			Integer value=(Integer)group.pop();
//			group.push(value.toString(), value);
//			System.out.println(group.getKeys());
//			try {
//				Thread.sleep(1000);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}

	}
}
