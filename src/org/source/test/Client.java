package org.source.test;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class Client {

	public static void main(String[] args) {
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>(20);
		
		list1.add("����");
		list1.add("����");
		list1.add("����");
		List<String> list3 = new ArrayList<String>(list1);
		System.out.println(list3.toString());
	}

}
