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
		list2.add("����");
		list2.add("����");
		list2.add("����");
		list2.add(1, "����");
		list2.contains("����");
		list2.get(3);
		list2.indexOf("����");
		DequeMe<String> que = new LinkedListMe<>();
		que.offerFirst("����");
		que.poll();
		
		list2.remove(3);
		list2.remove("����");
		ListMe<String> sublist = list2.subList(1, 2);
		Object[] r = list2.toArray(new Object[5]);
		String dd = r.toString();
		ListMe<String> list1 = new ArrayListMe<String>();
		list1.add("�·�");
		list1.add("����");
		list2.containsAll(list1);
		list2.addAll(2, list1);
		list2.containsAll(list1);
		System.out.println(list2.toString());
		
		LinkedListMe<String> list3 = new LinkedListMe<String>();
		list3.addFirst("��һ");
		list3.addFirst("����");
		list3.offerFirst("����");
		list3.offerLast("����");
		list3.offer("��һ");
		Iterator<String> it = list3.descendingIterator();
		String a = it.next();
		list3.removeFirstOccurrence("��һ");
		
	}
	
}
