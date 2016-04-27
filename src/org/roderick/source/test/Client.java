package org.roderick.source.test;

import java.io.FileInputStream;
import java.io.IOException;

public class Client {

	public static void main(String[] args) {
		try (
				FileInputStream ftStream = new FileInputStream("c/a.txt");
			) 
		{
			ftStream.read();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
			StackTraceElement[] a = e.getStackTrace();
			for (StackTraceElement t : a) {
				System.out.println(t.toString());
			}
			e.printStackTrace();
			StackTraceElement[] st = new StackTraceElement[1];
			st[0] = new StackTraceElement("org.class", "myMehtod", null, 33);
			e.setStackTrace(st);
			e.initCause(new IOException());
			System.out.println(e.getCause());
			e.addSuppressed(new NullPointerException("suppressed ex"));
			e.addSuppressed(new NullPointerException("other ex"));
			e.printStackTrace();
			e.fillInStackTrace();
			StackTraceElement[] s = e.getStackTrace();
			for (StackTraceElement t : s) {
				System.out.println(t.toString());
			}
			e.printStackTrace();
		}
	}
	
}
