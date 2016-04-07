package org.source.test;

import java.lang.String;
import java.util.Iterator;
import org.source.util.ArrayListMe;
import org.source.util.DequeMe;
import org.source.util.LinkedListMe;
import org.source.util.ListMe;


public class Client {

	public static void main(String[] args) {
		ListMe<String> list2 = new LinkedListMe<>();
		list2.add("张三");
		list2.add("李四");
		list2.add("王五");
		list2.add(1, "新增");
		list2.contains("王五");
		list2.get(3);
		list2.indexOf("李四");
		DequeMe<String> que = new LinkedListMe<>();
		que.offerFirst("李四");
		que.poll();
		
		list2.remove(3);
		list2.remove("张三");
		ListMe<String> sublist = list2.subList(1, 2);
		Object[] r = list2.toArray(new Object[5]);
		String dd = r.toString();
		ListMe<String> list1 = new ArrayListMe<String>();
		list1.add("衣服");
		list1.add("裤子");
		list2.containsAll(list1);
		list2.addAll(2, list1);
		list2.containsAll(list1);
		System.out.println(list2.toString());
		
		LinkedListMe<String> list3 = new LinkedListMe<String>();
		list3.addFirst("嘴一");
		list3.addFirst("嘴三");
		list3.offerFirst("嘴三");
		list3.offerLast("嘴三");
		list3.offer("嘴一");
		Iterator<String> it = list3.descendingIterator();
		String a = it.next();
		list3.removeFirstOccurrence("嘴一");
		
	}
	
}
