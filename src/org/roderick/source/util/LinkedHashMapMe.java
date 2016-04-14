package org.roderick.source.util;

public class LinkedHashMapMe<K, V> extends HashMapMe<K, V> implements MapMe<K, V> {

	private static final long serialVersionUID = 6491923701507229140L;
	
	static class Entry<K, V> extends HashMapMe.Node<K, V> {
		Entry<K, V> before;
		Entry<K, V> after;
		Entry(int hash, K key, V value, HashMapMe.Node<K, V> next) {
			super(hash, key, value, next);
		}
	}

	void afterNodeInsertion(boolean evict) { // possibly remove eldest
        System.out.print("LinkedHashMapMe.afterNodeInsertion......");
       /* if (evict && (first = head) != null && removeEldestEntry(first)) {
            K key = first.key;
            removeNode(hash(key), key, null, false, true);
        }*/
    }
	
}
