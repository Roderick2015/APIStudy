package org.roderick.source.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class LinkedHashMapMe<K, V> extends HashMapMe<K, V> implements MapMe<K, V> {

	private static final long serialVersionUID = 6491923701507229140L;
	
	static class Entry<K, V> extends HashMapMe.Node<K, V> {
		Entry<K, V> before; //˫������
		Entry<K, V> after;
		Entry(int hash, K key, V value, HashMapMe.Node<K, V> next) {
			super(hash, key, value, next);
		}
	}

	/**
	 * ˫�������ͷ����������Ǹ�Ԫ��
	 */
	transient LinkedHashMapMe.Entry<K, V> head;
	/**
	 * ˫�������β��
	 */
	transient LinkedHashMapMe.Entry<K, V> tail;
	
	/**
	 * true ����˳�� ����Ԫ�ر����ʵ�˳�����򣬱�get��Ԫ�ػ�������β/false ����˳��:��Ԫ�ز����˳���������
	 */
	final boolean accessOrder;
	
	/**
	 * ��β��Ԫ��
	 */
	private void linkNodeLast(LinkedHashMapMe.Entry<K, V> p) {
		LinkedHashMapMe.Entry<K, V> last = tail;
		tail = p;
		if (last == null)
			head = p;
		else {
			p.before = last;
			last.after = p;
		}
	}
	
	/**
	 * �ѽڵ�src������ת���ڵ�src��ת���src��������=null���ڻ�����
	 */
	private void transferLinks(LinkedHashMapMe.Entry<K, V> src,
							   LinkedHashMapMe.Entry<K, V> dst) {
		LinkedHashMapMe.Entry<K, V> b = dst.before = src.before; //src����һ���ڵ�
		LinkedHashMapMe.Entry<K, V> a = dst.after = src.after; //src����һ���ڵ�
		
		if (b == null)
			head = dst;
		else 
			b.after = dst;
		if (a == null)
			tail = dst;
		else 
			a.before = dst;
	}
	
	@Override
	void reinitialize() {
		super.reinitialize();
		head = tail = null;
	}
	
	/**
	 * ��Nodeת���£�Ȼ����β���Ӹ��½ڵ㣬��ʱ��ֵbefore��after
	 */
	@Override
	Node<K, V> newNode(int hash, K key, V value, Node<K, V> next) {
		LinkedHashMapMe.Entry<K, V> p = new LinkedHashMapMe.Entry<K, V>(hash, key, value, next);
		linkNodeLast(p);
		return p;
	}
	
	/**
	 * ���Ǿ��൱�ڸ�p�Ӹ�next�� ������Ԫ��
	 */
	@Override
	Node<K, V> replacementNode(Node<K, V> p, Node<K, V> next) {
		LinkedHashMapMe.Entry<K, V> q = (LinkedHashMapMe.Entry<K, V>) p; //ֱ��ת�ͣ�����Ҫ�����½ڵ�
		LinkedHashMapMe.Entry<K, V> t = new LinkedHashMapMe.Entry<K, V>(q.hash, q.key, q.value, next);
		transferLinks(q, t);
		return t;
	}
	
	@Override
	TreeNode<K, V> newTreeNode(int hash, K key, V value, Node<K, V> next) {
		TreeNode<K, V> p = new TreeNode<K, V>(hash, key, value, next);
		linkNodeLast(p); //���node��ϵ�е���
		return p;
	}
	
	@Override
	TreeNode<K, V> replacementTreeNode(Node<K, V> p, Node<K, V> next) {
		LinkedHashMapMe.Entry<K, V> q = (LinkedHashMapMe.Entry<K, V>) p;
		TreeNode<K, V> t = new TreeNode<K, V>(q.hash, q.key, q.value, next);
		transferLinks(q, t);
		return t;
	}
	
	/**
	 * �Ƴ��ڵ�󣬽���
	 */
	@Override
	void afterNodeRemoval(Node<K, V> e) {
		LinkedHashMapMe.Entry<K, V> p = (LinkedHashMapMe.Entry<K, V>) e;
		LinkedHashMapMe.Entry<K, V> b = (LinkedHashMapMe.Entry<K, V>) p.before;
		LinkedHashMapMe.Entry<K, V> a = (LinkedHashMapMe.Entry<K, V>) p.after;
		p.before = p.after = null;
		if (b == null)
			head = a;
		else 
			b.after = a;
		if (a == null)
			tail = b;
		else 
			a.before = b;
	}
	
	/**
	 * @param evict ?
	 */
	@Override
	void afterNodeInsertion(boolean evict) { // possibly remove eldest
		LinkedHashMapMe.Entry<K, V> first;
        if (evict && (first = head) != null && removeEldestEntry(first)) {
            K key = first.key;
            removeNode(hash(key), key, null, false, true);
        }
    }
	
	/**
	 * ��e�Ƶ���β��һ�����Ƚ������ټӵ�ĩβ��
	 * ����˳�򣬷��ʺ��ᵽ��β�����������ף�
	 */
	@Override
	void afterNodeAccess(Node<K, V> e) {
		LinkedHashMapMe.Entry<K, V> last;
		if (accessOrder && (last = tail) != e) { //accessOrder��ʲô���ã�e���������βԪ�أ����������
			LinkedHashMapMe.Entry<K, V> p = (LinkedHashMapMe.Entry<K, V>) e;
			LinkedHashMapMe.Entry<K, V> b = (LinkedHashMapMe.Entry<K, V>) p.before;
			LinkedHashMapMe.Entry<K, V> a = (LinkedHashMapMe.Entry<K, V>) p.after;
			p.after = null;
			if (b == null)
				head = a;
			else 
				b.after = a;
			if (a != null)
				a.before = b;
			else
				last = b;
			if (last == null)
				head = p;
			else {
				p.before = last; //�ӵ�last����
				last.after = p;
			}
			tail = p;
			++modCount;
		}
	}
	
	/**
	 * ��Ԫ��д��OutSteam��
	 */
	void internalWriteEntries(ObjectOutputStream s) throws IOException {
		for (LinkedHashMapMe.Entry<K, V> e = head; e != null; e = e.after) {
			s.writeObject(e.key);
			s.writeObject(e.getValue());
		}
	}
	
	public LinkedHashMapMe(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		accessOrder = false; //Ĭ�ϲ���˳��
	}
	
	public LinkedHashMapMe(int initialCapacity) {
		super(initialCapacity);
		accessOrder = false;
	}
	
	public LinkedHashMapMe() {
		super();
		accessOrder = false;
	}
	
	public LinkedHashMapMe(MapMe<? extends K, ? extends V> m) {
		super();
		accessOrder = false;
		putMapEntries(m, false);
	}
	
	public LinkedHashMapMe(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor);
		this.accessOrder = accessOrder; //ָ��˳��
	}
	
	@Override
	public boolean containsValue(Object value) {
		for (LinkedHashMapMe.Entry<K, V> e = head; e != null; e = e.after) {
			V v = e.value;
			if (v == value || (value != null && value.equals(v)))
				return true;
		}
		return false;
	}
	
	@Override
	public V get(Object key) {
		Node<K, V> e;
		if ((e = getNode(hash(key), key)) == null)
			return null;
		if (accessOrder)
			afterNodeAccess(e);
		return e.value;
	}
	
	/**
	 * û�ҵ��ͷ���Ĭ��ֵ
	 */
	@Override
	public V getOrDefault(Object key, V defaultValue) {
		Node<K, V> e;
		if ((e = getNode(hash(key), key)) == null)
			return defaultValue;
		if (accessOrder)
			afterNodeAccess(e);
		return e.value;
	}
	
	@Override
	public void clear() {
		super.clear();
		head = tail = null;
	}

	/**
	 * �ɸ�д�÷�����ͨ������ֵ�������Ƿ��ʻ���˳��(������trueʱ�����Ƴ����ϵĽڵ�)
	 */
	private boolean removeEldestEntry(MapMe.EntryMe<K, V> eldest) {
		return false;
	}
	
	@Override
	public SetMe<K> keySet() {
		SetMe<K> ks;
		return (ks = keySet) == null ? (new LinkedKeySetMe()) : ks;
	}
	
	final class LinkedKeySetMe extends AbstractSetMe<K> {
		@Override
		public final int size() {
			return size;
		}
		
		public final void clear() {
			LinkedHashMapMe.this.clear();
		}
		
		@Override
		public Iterator<K> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

	}
	
	abstract class LinkedHashIterator {
		LinkedHashMapMe.Entry<K, V> next;
		LinkedHashMapMe.Entry<K, V> current;
		int expectedModCount;
		
		public LinkedHashIterator() {
			next = head;
			expectedModCount = modCount;
			current = null;
		}
		
		public final boolean hasNext() {
			return next != null;
		}
		
		final LinkedHashMapMe.Entry<K, V> nextNode() {
			LinkedHashMapMe.Entry<K, V> e = next;
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
			
		}
		
	}
	
	final class LinkedKeyIterator extends 
	
}
