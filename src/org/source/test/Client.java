package org.source.test;

import java.lang.String;
import org.source.util.ArrayListMe;
import org.source.util.ListMe;


public class Client {

	public static void main(String[] args) {
		ListMe<String> list1 = new ArrayListMe<String>(5);
		ListMe<String> list2 = new ArrayListMe<>(20);
		list2.add("����");
		list2.add("����");
		list2.add("����");
		list2.add("����");
		list2.add("����");
		list2.add("����");
		list2.add("����");
		list2.add("����");
		list1.add("����");
		list1.add("����");
		list1.add("����");
		list1.add("����");
		list1.add("����");
		
		ListMe<String> it = list1.subList(2, 4);
		it.add("�µ�");
		ListMe<String> it2 = it.subList(1,3);
		it2.add(0, "�ɵ�");
		
		
		ArrayListMe<String> list3 = new ArrayListMe<String>(5);
		list3.add("����");
		list3.add("����");
		@SuppressWarnings("unchecked")
		ArrayListMe<String> list4 = (ArrayListMe<String>) list3.clone();
		list4.ensureCapacity(20);
		list4.trimToSize();
	}
	
}
