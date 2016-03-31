package org.source.test;

import java.lang.String;
import org.source.util.ArrayListMe;
import org.source.util.ListMe;


public class Client {

	public static void main(String[] args) {
		ListMe<String> list1 = new ArrayListMe<String>(5);
		ListMe<String> list2 = new ArrayListMe<>(20);
		list2.add("张三");
		list2.add("张三");
		list2.add("张三");
		list2.add("张三");
		list2.add("张三");
		list2.add("张三");
		list2.add("张三");
		list2.add("张三");
		list1.add("张三");
		list1.add("李四");
		list1.add("王五");
		list1.add("王五");
		list1.add("王五");
		
		ListMe<String> it = list1.subList(2, 4);
		it.add("新的");
		ListMe<String> it2 = it.subList(1,3);
		it2.add(0, "旧的");
		
		
		ArrayListMe<String> list3 = new ArrayListMe<String>(5);
		list3.add("王五");
		list3.add("王五");
		@SuppressWarnings("unchecked")
		ArrayListMe<String> list4 = (ArrayListMe<String>) list3.clone();
		list4.ensureCapacity(20);
		list4.trimToSize();
	}
	
}
