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
		list2.add("����");
		list2.add("����");
		list2.add("����");
		ListMe<String> list1 = new ArrayListMe<String>();
		list1.add("�·�");
		list1.add("����");
		list2.addAll(list1);
		System.out.println(list2.toString());
	}
	
}
