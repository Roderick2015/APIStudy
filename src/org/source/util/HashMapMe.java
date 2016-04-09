package org.source.util;

import java.io.Serializable;
import java.util.Objects;

public class HashMapMe<K, V> extends AbstractMapMe<K, V>
	implements MapMe<K, V>, Cloneable, Serializable {
	
	private static final long serialVersionUID = -6449487488242541901L;

	//��ʼ������������2���ݣ�ΪʲôҪλ���㲻ֱ�Ӹ�ֵ
	static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
	
	//���������������2����
	static final int MAXIMUM_CAPACITY = 1 << 30;
	
	//�������ӣ�Ӱ���ϣ���Զ����ݵĴ�С
	static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	static class Node<K, V> implements MapMe.EntryMe<K, V> {
		final int hash; //final���������볣����
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
		public final K getKey() { //final����������ʱ��תΪ��Ƕ���ã���ʹ�ó����ѹջ��ʽ
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
