package org.source.test;

import java.lang.String;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.source.util.ArrayListMe;
import org.source.util.LinkedListMe;
import org.source.util.ListMe;


public class Client {

	public static void main(String[] args) {
		ListMe<String> list2 = new LinkedListMe<>();
		list2.add("张三");
		list2.add("李四");
		list2.add("王五");
		ListMe<String> list1 = new ArrayListMe<String>();
		list1.add("衣服");
		list1.add("裤子");
		list2.addAll(list1);
		System.out.println(list2.toString());
	}
	
}
