package org.source.test;

import java.lang.String;

import org.source.util.ArrayListMe;
import org.source.util.ListMe;


public class Client {

	public static void main(String[] args) {
		ListMe<String> list1 = new ArrayListMe<String>();
		ListMe<String> list2 = new ArrayListMe<String>(20);
		
		list1.add("张三");
		list1.add("李四");
		list1.add("王五");
		ListMe<String> list3 = new ArrayListMe<String>(list1);
		System.out.println(list3.toString());
	}

}
