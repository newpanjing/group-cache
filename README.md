# group-cache
简单的内存缓存实现，实现`group`概念，一个`group`里面是个有序的集合，集合支持`key-value`、`expire`弥补redis list的不足

## 总共有3个类：

- `GroupCacheFactory` 工厂用于获取Group
- `Group` 组，存放多个key和value
- `CacheEntity` 缓存实体，所有缓存的数据都是以CacheEntity为载体放入Group中

## 博客
[https://www.88cto.com](https://www.88cto.com)

## 测试代码： 

```java
package com.qikenet.cache;

import java.util.Random;

public class CacheTest {

	public static void main(String[] args) {
		/**
		 * 概念：
		 * 组缓存，在传统redis、memcached 这一类的缓存容器中，都是key-value类型的
		 * 所谓的组缓存，就是在key-value的外层加一个标识
		 * 用list来做比较，在redis中，list的结构是这样的，并且list的某一项不能设置存活时间：
		 * 	abc
		 * 		1
		 * 		2
		 * 		3
		 * 
		 * 在GroupCache中结构是这样的	:
		 * abc
		 * 	a 1 10
		 *  b 2 5
		 *  c 3 1
		 * 第一行：a代表key，1代表value，10代表存活10秒，存活时间以秒为单位		 
		 */
		
		//创建一个工厂，暂时不支持持久化
		GroupCacheFactory factory=new GroupCacheFactory();
		//获取一个组
		Group group1=factory.group("group1");
		group1.push("a", 123);//默认永久存活
		group1.push("b", 321, 10);//存活10秒
		
		//查看还有多久失效
		System.out.println("group 1 的元素b 剩余失效时间："+group1.ttl("b"));
		
		//检测值是否存在
		System.out.println("a是否存在："+group1.exist("a"));
		System.out.println("c是否存在："+group1.exist("c"));
		
		//获取有效的key
		System.out.println("group1 keys:"+group1.getKeys());
		
		//获取有效的value
		System.out.println("group1 values:"+group1.getValues());
		
		//通过key获取值
		System.out.println("通过a获取："+group1.getValue("a"));
		
		
		//获取一个组，并设置容量为100，默认容量为1000,不同的group中数据不能共享
		Group group2=factory.group("group2",100);
		
		//如果push的数据大于容量，则会丢弃
		for(int i=0;i<101;i++){
			group2.push(String.valueOf(i), i, new Random().nextInt(10));
		}
		//获取存活的元素的数量
		System.out.println("group2大小:"+group2.size());
		
		//设置元素的存活时间
		group2.expire("2", 100);
		
		//删除一个元素
		group2.delete("3");
		System.out.println("group删除后的元素大小："+group2.size());
		
		//获取group容量大小
		System.out.println("group2容量："+group2.getCapacity());
		
		System.out.println(group2.getKeys());
		
	}
}
```
