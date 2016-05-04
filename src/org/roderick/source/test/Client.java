package org.roderick.source.test;

import org.roderick.source.lang.StringMe;

public class Client {

	public static void main(String[] args) {
	//	byte b[] = {1,0,1,1,0};
		char ch[] = {65,78,36};
		char ch2[] = {'c','d'};
		char ch3[] = {'B','a','c','c'};
		StringMe aStr = new StringMe(ch);
		StringMe bStr = new StringMe(ch2);
		StringMe cStr = new StringMe(ch3);
		StringMe aa = cStr.replace('s', '1');
		aa = new StringMe(ch2);
		System.out.println(bStr.concat(cStr));
		String bc = "123";
		String ad = "123";
		bc.equals(ad);
		String dd = ad;
		String ee = new String(ch);
		System.out.println(bc.hashCode());
		System.out.println(ad.hashCode());
		System.out.println(bc==dd);
	}
	
}
