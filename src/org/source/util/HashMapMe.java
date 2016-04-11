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
	
	//������ӣ�Ӱ���ϣ���Զ����ݵĴ�С
	static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	//��bucket�ϵĽ�����������ֵʱ��ת�ɺ����
	static final int TREEIFY_THRESHOLD = 8;
	
	//��bucket�ϵĽ����С�����ֵʱ��ת������
	static final int UNTREEIFY_THRESHOLD = 6;
	
	//������С����
	static final int MIN_TREEIFY_CAPACITY = 64;
	
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
	
	/**
	 * �洢Ԫ�ص����飬2���ݴα�
	 */
	transient Node<K, V>[] table;
	/**
	 * ��ž���Ԫ�صļ���
	 */
	transient SetMe<MapMe.EntryMe<K, V>> entrySet;
	transient int size;
	transient int modCount;
	/**
	 * ��map��size(����*�������)���ڸ�ֵʱ����ʹ��resize��������
	 */
	transient int threshold;
	final float loadFactor;
	
	public HashMapMe(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) 
			throw new IllegalArgumentException("Illegal initial capacity: " +
                    							initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		if (loadFactor <= 0 || Float.isNaN(loadFactor)) //�ж�һ����������
			throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
		this.loadFactor = loadFactor;
		this.threshold = tableSizeFor(initialCapacity);
	}
	
	public HashMapMe(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	public HashMapMe() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
	}
	
	public HashMapMe(MapMe<? extends K, ? extends V> m) {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		
	}
	
	@Override
	public SetMe<MapMe.EntryMe<K, V>> entrySet() {
		return null;
	}

	/**
	 * ���ش��ڸ�����������С�Ķ�������ֵ
	 * Ϊʲô��
	 */
	static final int tableSizeFor(int cap) {
		int n = cap - 1;
		n |= n >>> 1;
		n |= n >>> 2;
		n |= n >>> 4;
		n |= n >>> 8;
		n |= n >>> 16;
		return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	}
	
	final void putMapEntries(MapMe<? extends K, ? extends V> m, boolean evict) {
		int s = size();
		if (s > 0) {
			if (table == null) { //��ϣ����������
				float ft = ((float)s / loadFactor) + 1.0F; //�õ�s���������(ʵ�ʴ�СΪ����*�������)
				int t = (ft < (float)MAXIMUM_CAPACITY) ? (int)ft : MAXIMUM_CAPACITY;
				if (t > threshold) //�������������Զ����ݵ���������t�Ĵ�С�޸�
					threshold = tableSizeFor(t);
			} else if (s > threshold) //?
				resize();
			for (org.source.util.MapMe.EntryMe<? extends K, ? extends V> e : m.entrySet()) {
				K key = e.getKey();
				V value = e.getValue();
				putVal(hash(key), key, value, false, evict);
			}
		}
	}

	static final int hash(Object key) {
		int h;
		return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
	}

	final Node<K, V>[] resize() {
		Node<K, V>[] oldTab = table;
		int oldCap = (oldTab == null) ? 0 : oldTab.length;
		int oldThr = threshold;
		int newCap = 0;
		int newThr = 0;
		if (oldCap > 0) {
			if (oldCap >= MAXIMUM_CAPACITY) {
				threshold = Integer.MAX_VALUE;
				return oldTab; //�Ѵ����ֵ�޷�����
			} else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
					oldCap >= DEFAULT_INITIAL_CAPACITY) //�����ǰ��������������
				newThr = oldThr << 1; //����������
		} else if (oldThr > 0) { //�������Ϊ0����threshold����0��˵����ʼ����
			newCap = oldThr;
		} else { //δ��ʼ����
			newCap = DEFAULT_INITIAL_CAPACITY;
			newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
		}
		if (newThr == 0) { //�����thresholdΪ0��������µ�size�������������
			float ft = (float) newCap * loadFactor;
			newThr = (newCap < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ?
					  (int) ft : Integer.MAX_VALUE);
		}
		threshold = newThr;
		@SuppressWarnings("unchecked")
		Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];
		table = newTab;
		if (oldTab != null) {
			for (int j = 0; j < oldCap; ++j) {
				Node<K, V> e;
				if ((e = oldTab[j]) != null) {
					oldTab[j] = null;
					
				}
			}
		}
		return newTab;
		
	}
	
	final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
             boolean evict) {
		Node<K, V>[] tab;
		Node<K, V> p;
		int n;
		int i;
		
		if ((tab = table) == null || (n = tab.length) == 0)
			n = (tab = resize()).length;
		return value;
	}
}
