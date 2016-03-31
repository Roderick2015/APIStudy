package org.source.test;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import org.source.util.ArrayListMe;
import org.source.util.ListMe;


public class Client {

	public static void main(String[] args) {
		List<String> list1 = new ArrayList<String>(5);
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
		
		String aa = list1.remove(2);
		
		ArrayListMe<String> list3 = new ArrayListMe<String>(5);
		list3.add("王五");
		list3.add("王五");
		ArrayListMe<String> list4 = (ArrayListMe<String>) list3.clone();
		list4.ensureCapacity(20);
		list4.trimToSize();
	}
	
}
