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
	
	//填充因子，影响哈希表自动扩容的大小
	static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	//当bucket上的结点数大于这个值时，转成红黑树
	static final int TREEIFY_THRESHOLD = 8;
	
	//当bucket上的结点数小于这个值时，转成链表
	static final int UNTREEIFY_THRESHOLD = 6;
	
	//树的最小容量
	static final int MIN_TREEIFY_CAPACITY = 64;
	
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
	
	/**
	 * 存储元素的数组，2的幂次倍
	 */
	transient Node<K, V>[] table;
	/**
	 * 存放具体元素的集合
	 */
	transient SetMe<MapMe.EntryMe<K, V>> entrySet;
	transient int size;
	transient int modCount;
	/**
	 * 当map的size(容量*填充因子)大于该值时，需使用resize方法扩容
	 */
	transient int threshold;
	final float loadFactor;
	
	public HashMapMe(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) 
			throw new IllegalArgumentException("Illegal initial capacity: " +
                    							initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		if (loadFactor <= 0 || Float.isNaN(loadFactor)) //判断一定浮点数？
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
	 * 返回大于给定容量的最小的二次幂数值
	 * 为什么？
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
			if (table == null) { //哈希表中无数据
				float ft = ((float)s / loadFactor) + 1.0F; //得到s所需的容量(实际大小为容量*填充因子)
				int t = (ft < (float)MAXIMUM_CAPACITY) ? (int)ft : MAXIMUM_CAPACITY;
				if (t > threshold) //所需容量大于自动扩容的量，根据t的大小修改
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
				return oldTab; //已大最大值无法扩容
			} else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
					oldCap >= DEFAULT_INITIAL_CAPACITY) //如果当前表容量翻倍可行
				newThr = oldThr << 1; //表容量翻倍
		} else if (oldThr > 0) { //如果容量为0，但threshold大于0，说明初始化过
			newCap = oldThr;
		} else { //未初始化过
			newCap = DEFAULT_INITIAL_CAPACITY;
			newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
		}
		if (newThr == 0) { //如果新threshold为0，则根据新的size和填充因子扩容
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
