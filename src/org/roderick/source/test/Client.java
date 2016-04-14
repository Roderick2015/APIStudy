package org.roderick.source.test;

import org.roderick.source.util.HashMapMe;
import org.roderick.source.util.MapMe;

public class Client {

	public static void main(String[] args) {
		MapMe<String, String> map = new HashMapMe<>();
		for(int i = 0; i < 100; i++) {
			map.put(i + "李四", "张三" + i);
		}
		/*map.put("1", "张三");
		map.put("2", "李四");
		map.put("3", "王五");*/
		map.put("1002", "看看"); //这个时候的loadfactor是多大？
		map.put("4", "新增");
		String a = map.get("4");
		String b = map.remove("2");
		map.containsKey("1");
	}
	
}
