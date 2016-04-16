package org.roderick.source.util;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class LinkedHashMapMe<K, V> extends HashMapMe<K, V> implements MapMe<K, V> {

	private static final long serialVersionUID = 6491923701507229140L;
	
	static class Entry<K, V> extends HashMapMe.Node<K, V> {
		Entry<K, V> before; //双向链表
		Entry<K, V> after;
		Entry(int hash, K key, V value, HashMapMe.Node<K, V> next) {
			super(hash, key, value, next);
		}
	}

	/**
	 * 双向链表的头部，最早的那个元素
	 */
	transient LinkedHashMapMe.Entry<K, V> head;
	/**
	 * 双向链表的尾部
	 */
	transient LinkedHashMapMe.Entry<K, V> tail;
	
	/**
	 * true 访问顺序 ：按元素被访问的顺序排序，被get的元素会移至链尾/false 插入顺序:按元素插入的顺序进行排序
	 */
	final boolean accessOrder;
	
	/**
	 * 链尾加元素
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
	 * 把节点src的链接转给节点src，转完后src不用主动=null用于回收吗
	 */
	private void transferLinks(LinkedHashMapMe.Entry<K, V> src,
							   LinkedHashMapMe.Entry<K, V> dst) {
		LinkedHashMapMe.Entry<K, V> b = dst.before = src.before; //src的上一个节点
		LinkedHashMapMe.Entry<K, V> a = dst.after = src.after; //src的下一个节点
		
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
	 * 把Node转换下，然后在尾部加个新节点，何时赋值before和after
	 */
	@Override
	Node<K, V> newNode(int hash, K key, V value, Node<K, V> next) {
		LinkedHashMapMe.Entry<K, V> p = new LinkedHashMapMe.Entry<K, V>(hash, key, value, next);
		linkNodeLast(p);
		return p;
	}
	
	/**
	 * 不是就相当于给p加个next？ 返回新元素
	 */
	@Override
	Node<K, V> replacementNode(Node<K, V> p, Node<K, V> next) {
		LinkedHashMapMe.Entry<K, V> q = (LinkedHashMapMe.Entry<K, V>) p; //直接转型，不需要建立新节点
		LinkedHashMapMe.Entry<K, V> t = new LinkedHashMapMe.Entry<K, V>(q.hash, q.key, q.value, next);
		transferLinks(q, t);
		return t;
	}
	
	@Override
	TreeNode<K, V> newTreeNode(int hash, K key, V value, Node<K, V> next) {
		TreeNode<K, V> p = new TreeNode<K, V>(hash, key, value, next);
		linkNodeLast(p); //这个node关系有点乱
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
	 * 移除节点后，解链
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
	 * 把e移到链尾，一定是先解链，再加到末尾。
	 * 访问顺序，访问后提到链尾，而不是链首？
	 */
	@Override
	void afterNodeAccess(Node<K, V> e) {
		LinkedHashMapMe.Entry<K, V> last;
		if (accessOrder && (last = tail) != e) { //accessOrder起到什么作用，e如果就是链尾元素，则无需操作
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
				p.before = last; //加到last后面
				last.after = p;
			}
			tail = p;
			++modCount;
		}
	}
	
	/**
	 * 将元素写入OutSteam中
	 */
	void internalWriteEntries(ObjectOutputStream s) throws IOException {
		for (LinkedHashMapMe.Entry<K, V> e = head; e != null; e = e.after) {
			s.writeObject(e.key);
			s.writeObject(e.getValue());
		}
	}
	
	public LinkedHashMapMe(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
		accessOrder = false; //默认插入顺序
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
		this.accessOrder = accessOrder; //指定顺序
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
	 * 没找到就返回默认值
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
	 * 可覆写该方法，通过返回值来控制是访问还是顺序(当返回true时，将移除最老的节点)
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
