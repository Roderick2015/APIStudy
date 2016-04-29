package org.roderick.source.test;

import org.roderick.source.lang.StringMe;

public class Client {

	public static void main(String[] args) {
	//	byte b[] = {1,0,1,1,0};
		char ch[] = {65,78,36};
		char ch2[] = {'a','b','c'};
		StringMe aStr = new StringMe(ch);
		System.out.println(aStr.hashCode());
		StringMe bStr = new StringMe(ch);
		aStr.equals(bStr);
		System.out.println(bStr.hashCode());
		StringMe cStr = new StringMe(ch2);
		System.out.println(aStr==bStr);
		
		String bc = "123";
		String ad = "123";
		String dd = ad;
		char ch3[] = {'1','2','3'};
		String cc = new String(ch3);
		String ee = new String(ch);
		System.out.println(cc.hashCode());
		System.out.println(bc.hashCode());
		System.out.println(ad.hashCode());
		System.out.println(bc==dd);
	}
	
}
