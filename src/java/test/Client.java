package java.test;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

public class Client {

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.removeAll(list);
	}

}
