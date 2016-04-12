package org.source.util;

public class LinkedHashMapMe<K, V> extends HashMapMe<K, V> implements MapMe<K, V> {

	private static final long serialVersionUID = 6491923701507229140L;
	
	static class Entry<K, V> extends HashMapMe.Node<K, V> {
		Entry<K, V> before;
		Entry<K, V> after;
		Entry(int hash, K key, V value, HashMapMe.Node<K, V> next) {
			super(hash, key, value, next);
		}
	}

}
