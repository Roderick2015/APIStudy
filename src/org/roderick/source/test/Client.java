package org.roderick.source.test;

import org.roderick.source.util.HashMapMe;
import org.roderick.source.util.MapMe;

public class Client {

	public static void main(String[] args) {
		MapMe<String, String> map = new HashMapMe<>();
		for(int i = 0; i < 100; i++) {
			map.put(i + "����", "����" + i);
		}
		/*map.put("1", "����");
		map.put("2", "����");
		map.put("3", "����");*/
		map.put("1002", "����"); //���ʱ���loadfactor�Ƕ��
		map.put("4", "����");
		String a = map.get("4");
		String b = map.remove("2");
		map.containsKey("1");
	}
	
}
