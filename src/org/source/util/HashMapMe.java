package org.source.util;

import java.io.Serializable;
import java.util.Objects;

public class HashMapMe<K, V> extends AbstractMapMe<K, V>
	implements MapMe<K, V>, Cloneable, Serializable {
	
	private static final long serialVersionUID = -6449487488242541901L;

	//初始容量，必须是2的幂，为什么要位运算不直接赋值
	static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
	
	//最大容量，必须是2的幂
	static final int MAXIMUM_CAPACITY = 1 << 30;
	
	//加载因子，影响哈希表自动扩容的大小
	static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	static class Node<K, V> implements MapMe.EntryMe<K, V> {
		final int hash; //final变量，存入常量池
		final K key;
		V value;
		Node<K, V> next;
		
		Node(int hash, K key, V value, Node<K, V> next) {
			this.hash = hash;
			this.key = key;
			this.value = value;
			this.next = next;
		}
		
		@Override
		public final K getKey() { //final方法被调用时会转为内嵌调用，不使用常规的压栈方式
			return key;
		}

		@Override
		public final V getValue() {
			return value;
		}

		@Override
		public final String toString() {
			return key + "=" + value;
		}
		
		@Override
		public final int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}
		
		@Override
		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}
		
		@Override
		public final boolean equals(Object o) {
			if (o == this)
				return true;
			if (o instanceof EntryMe) {
				MapMe.EntryMe<?, ?> e = (MapMe.EntryMe<?, ?>) o;
				if (Objects.equals(key, e.getKey()) &&
					Objects.equals(value, e.getValue()))
					return true;
			}
			return false;
		}
	}
	
	@Override
	public SetMe<MapMe.EntryMe<K, V>> entrySet() {
		return null;
	}

}
