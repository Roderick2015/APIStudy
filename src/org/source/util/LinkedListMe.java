package org.source.util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 双向链表
 */
public class LinkedListMe<E> extends AbstractSequentialListMe<E> implements ListMe<E>,
	DequeMe<E>, Cloneable, Serializable {
	private static final long serialVersionUID = -1190533378228822052L;
	
	transient int size;
	transient Node<E> first;
	transient Node<E> last;

	public LinkedListMe() {
	}

	public LinkedListMe(CollectionMe<? extends E> c) {
		this();
		addAll(c);
	}
	
	/**
	 * 添加首节点
	 */
	private void linkFirst(E e) {
		final Node<E> f = first; //为什么都加final?
		final Node<E> newNode = new Node<>(null, e, f);
		first = newNode;
		if (f == null) //空链表，首节点和尾节点是同一个，为什么不用size判断？
			last = newNode;
		else
			f.prev = newNode; //旧的首节点作为第二个节点
		size++;
		modCount++;
	}

	/**
	 * 添加尾节点
	 */
	void linkLast(E e) {
		final Node<E> l = last;
		final Node<E> newNode = new Node<>(l, e, null);
		last = newNode;
		if (l == null)
			first = newNode;
		else
			l.next = newNode;
		size++;
		modCount++;
	}
	
	/**
	 * 在节点succ的前面插入e
	 * @param e
	 * @param succ
	 */
	void linkBefore(E e, Node<E> succ) {
		// assert succ != null;
		final Node<E> pred = succ.prev;
		final Node<E> newNode = new Node<>(pred, e, succ);
		succ.prev = newNode;
		if (pred == null)
			first = newNode;
		else
			pred.next = newNode;
		size++;
		modCount++;
	}
	
	/**
	 * 移除首节点（f必须是首节点）
	 * @param f
	 * @return
	 */
	private E unlinkFirst(Node<E> f) {
		final E element = f.item;
		final Node<E> next = f.next;
		f.item = null;
		f.next = null; //让节点f尽快回收
		first = next;
		if (next == null)
			last = null;
		else 
			next.prev = null; //下个节点作为首节点
		size--;
		modCount++;
		return element;
	}
	
	/**
	 * 移除尾节点
	 * @param l
	 * @return
	 */
	private E unlinkLast(Node<E> l) {
		final E element = l.item;
		final Node<E> prev = l.prev;
		l.item = null;
		l.prev = null;
		last = prev;
		if (prev == null)
			first = null;
		else 
			prev.next = null;
		size--;
		modCount++;
		return element;
	}
	
	/**
	 * 把元素从链表中移除
	 */
	E unlink(Node<E> x) {
		// assert x != null;
		final E element = x.item;
		final Node<E> next = x.next;
		final Node<E> prev = x.prev;

		if (prev == null) { //链首
			first = next; //移除当前元素，下个元素顶替链首位置
		} else {
			prev.next = next; //该元素脱离链表
			x.prev = null;
		}

		if (next == null) { //链尾
			last = prev; //上个元素顶替链尾位置
		} else {
			next.prev = prev; //脱链
			x.next = null;
		}

		x.item = null; //当前元素置空，GC
		size--;
		modCount++;
		return element;
	}
	
	@Override
	public E getFirst() {
		final Node<E> f = first; //lock?
		if (f == null)
			throw new NoSuchElementException();
		return f.item;
	}

	@Override
	public E getLast() {
		final Node<E> l = last;
		if (l == null)
			throw new NoSuchElementException();
		return l.item;
	}
	
	@Override
	public E removeFirst() {
		final Node<E> f = first;
		if (f == null)
			throw new NoSuchElementException();
		return unlinkFirst(f);
	}
	
	@Override
	public E removeLast() {
		final Node<E> l = last;
		if (l == null)
			throw new NoSuchElementException();
		return unlinkLast(l);
	}
	
	@Override
	public void addFirst(E e) {
		linkFirst(e);
	}
	
	@Override
	public void addLast(E e) {
		linkLast(e);
	}
	
	@Override
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public boolean add(E e) {
		linkLast(e);
		return true;
	}
	
	@Override
	public boolean remove(Object o) {
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null) {
					unlink(x);
					return true;
				}
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item)) {
					unlink(x);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean addAll(CollectionMe<? extends E> c) {
		return addAll(size, c);
	}

	@Override
	public boolean addAll(int index, CollectionMe<? extends E> c) {
		checkPositionIndex(index);

		Object[] a = c.toArray();
		int numNew = a.length;
		if (numNew == 0)
			return false;

		Node<E> pred;
		Node<E> succ;
		if (index == size) { // 末尾
			succ = null;
			pred = last;
		} else { // 当前位置
			succ = node(index);
			pred = succ.prev;
		}

		for (Object o : a) {
			@SuppressWarnings("unchecked")
			E e = (E) o;
			Node<E> newNode = new Node<E>(pred, e, null);
			if (pred == null) // ?
				first = newNode;
			else
				pred.next = newNode;
			pred = newNode;
		}

		if (succ == null) {
			last = pred;
		} else {
			pred.next = succ; // ?
			succ.prev = pred;
		}

		size += numNew;
		modCount++;
		return true;
	}

	/**
	 *  清空
	 */
	@Override
	public void clear() {
		for (Node<E> x = first; x != null;) {
			Node<E> next = x.next;
			x.item = null;
			x.next = null;
			x.prev = null;
			x = next;
		}
		first = last = null;
		size = 0;
		modCount++;
	}

	@Override
	public E get(int index) {
		checkElementIndex(index);
		return node(index).item;
	}
	
	/**
	 * 替换该位置的元素 
	 */
	@Override
	public E set(int index, E element) {
		checkElementIndex(index);
		Node<E> x = node(index);
		E oldVal = x.item;
		x.item = element;
		return oldVal;
	}
	
	@Override
	public void add(int index, E element) {
		checkPositionIndex(index);
		
		if (index == size)
			linkLast(element);
		else 
			linkBefore(element, node(index)); //添加至该索引元素之前
	}
	
	@Override
	public E remove(int index) {
		checkElementIndex(index);
		return unlink(node(index));
	}
	
	/**
	 * 判断index是否为元素所在的有效位置，只包含当前链表中元素的索引位置
	 */
	private boolean isElementIndex(int index) {
		return index >= 0 && index < size;
	}
	
	/**
	 * 判断index是否为有效位置，包含即将新增的元素索引位置
	 */
	private boolean isPositionIndex(int index) {
		return index >= 0 && index <= size;
	}

	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size;
	}

	private void checkElementIndex(int index) {
		if (!isElementIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}
	
	private void checkPositionIndex(int index) {
		if (!isPositionIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	/**
	 * 根据索引找元素，得遍历，效率不高
	 */
	Node<E> node(int index) {
		if (index < (size >> 1)) { // index小于size/2，则从左边开始遍历，二分查找
			Node<E> x = first;
			for (int i = 0; i < index; i++)
				x = x.next;
			return x;
		} else {
			Node<E> x = last;
			for (int i = size - 1; i > index; i--)
				x = x.prev;
			return x;
		}
	}
	
	@Override
	public int indexOf(Object o) {
		int index = 0;
		if (o == null) {
			for (Node<E> x = first; x != null; x = x.next) {
				if (x.item == null)
					return index;
				index++;
			}
		} else {
			for (Node<E> x = first; x != null; x = x.next) {
				if (o.equals(x.item))
					return index;
				index++;
			}
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(Object o) {
		int index = size;
		if (o == null) {
			for (Node<E> x = last; x != null; x = x.prev) {
				index--; //实际索引是size-1
				if (x.item == null)
					return index;
			}
		} else {
			for (Node<E> x = last; x != null; x = x.prev) {
				index--;
				if (o.equals(x.item))
					return index;
			}
		}
		return -1;
	}
	
	/**
	 * 访问首节点元素，链表可为空
	 */
	@Override
	public E peek() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
	}
	
	/**
	 * 获取首节点元素，链表为空则抛出异常
	 */
	@Override
	public E element() {
		return getFirst();
	}
	
	/**
	 * 移除当前首节点，链表可为空
	 */
	@Override
	public E poll() {
		final Node<E> f = first;
		return (f == null) ? null : unlinkFirst(f);
	}
	
	/**
	 * 移除当前首节点，链表为空则抛出异常
	 */
	@Override
	public E remove() {
		return removeFirst();
	}
	
	/**
	 * 在链尾添加该元素
	 */
	@Override
	public boolean offer(E e) {
		return add(e);
	}
	
	/**
	 * 在链首添加该元素（与addFirst方法的区别?）
	 */
	@Override
	public boolean offerFirst(E e) {
		addFirst(e);
		return true;
	}
	
	@Override
	public boolean offerLast(E e) {
		addLast(e);
		return true;
	}
	
	/**
	 * what's the difference?
	 * <p>{@link #peek}
	 */
	@Override
	public E peekFirst() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
	}
	
	@Override
	public E peekLast() {
		final Node<E> l = last;
		return (l == null) ? null : l.item;
	}
	
	@Override
	public E pollFirst() {
		final Node<E> f = first;
		return (f == null) ? null : unlinkFirst(f);
	}

	@Override
	public E pollLast() {
		final Node<E> l = last;
		return (l == null) ? null : unlinkLast(l);
	}
	
	@Override
	public void push(E e) {
		addFirst(e);
	}
	
	/**
	 * 移除首节点元素并返回
	 */
	@Override
	public E pop() {
		return removeFirst();
	}
	
	/**
	 * 移除第一个被匹配到的元素，未匹配到则返回false
	 */
	@Override
	public boolean removeFirstOccurrence(Object o) {
		return remove(o);
	}
	
	@Override
	public boolean removeLastOccurrence(Object o) {
		if (o == null) {
			for (Node<E> x = last; x != null; x = x.prev) {
				if (x.item == null) {
					unlink(x);
					return true;
				}
			}
		} else {
			for (Node<E> x = last; x != null; x = x.prev) {
				if (o.equals(x.item)) {
					unlink(x);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public ListIteratorMe<E> listIterator(int index) {
		checkPositionIndex(index);
		return new ListItrMe(index);
	}

	private class ListItrMe implements ListIteratorMe<E> {
		private Node<E> lastReturned; //上次操作返回的节点
		private Node<E> next;
		private int nextIndex; //索引的下个位置
		private int expectedModCount = modCount;

		ListItrMe(int index) {
			// assert isPositionIndex(index);
			next = (index == size) ? null : node(index);
			nextIndex = index;
		}

		public boolean hasNext() {
			return nextIndex < size;
		}

		public E next() {
			checkForComodification();
			if (!hasNext())
				throw new NoSuchElementException();

			lastReturned = next;
			next = next.next;
			nextIndex++;
			return lastReturned.item;
		}

		public boolean hasPrevious() {
			return nextIndex > 0;
		}

		public E previous() {
			checkForComodification();
			if (!hasPrevious())
				throw new NoSuchElementException();

			lastReturned = next = (next == null) ? last : next.prev;
			nextIndex--;
			return lastReturned.item;
		}

		public int nextIndex() {
			return nextIndex;
		}

		public int previousIndex() {
			return nextIndex - 1;
		}

		public void remove() {
			checkForComodification();
			if (lastReturned == null)
				throw new IllegalStateException();

			Node<E> lastNext = lastReturned.next;//获取到当前位置的元素
			unlink(lastReturned);
			if (next == lastReturned)
				next = lastNext;
			else
				nextIndex--;
			lastReturned = null;
			expectedModCount++;
		}

		public void set(E e) {
			if (lastReturned == null)
				throw new IllegalStateException();
			checkForComodification();
			lastReturned.item = e;
		}

		public void add(E e) {
			checkForComodification();
			lastReturned = null;
			if (next == null)
				linkLast(e);
			else
				linkBefore(e, next);
			nextIndex++;
			expectedModCount++;
		}

		public void forEachRemaining(Consumer<? super E> action) {
			Objects.requireNonNull(action);
			while (modCount == expectedModCount && nextIndex < size) {
				action.accept(next.item);
				lastReturned = next;
				next = next.next;
				nextIndex++;
			}
			checkForComodification();
		}

		final void checkForComodification() {
			if (modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}
	
	/**
	 * 嵌套类
	 */
	private static class Node<E> {
		E item;
		Node<E> next;
		Node<E> prev;

		Node(Node<E> prev, E elment, Node<E> next) {
			this.item = elment;
			this.prev = prev;
			this.next = next;
		}
	}
	
	/**
	 * 倒序迭代器
	 */
	@Override
	public Iterator<E> descendingIterator() {
		return new DescendingIteratorMe();
	}
	
	private class DescendingIteratorMe implements Iterator<E> {
		private final ListItrMe itr = new ListItrMe(size());
		@Override
		public boolean hasNext() {
			return itr.hasPrevious();
		}

		@Override
		public E next() {
			return itr.previous();
		}
		
		@Override
		public void remove() {
			itr.remove();
		}
	}
	
	@SuppressWarnings("unchecked")
	private LinkedListMe<E> superClone() {
		try {
			return (LinkedListMe<E>) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}
	
	/**
	 * 浅克隆
	 */
	@Override
	public Object clone() {
		LinkedListMe<E> clone = superClone();
		
		//先回到初始状态，再逐个添加
		clone.first = clone.last = null;
		clone.size = 0;
		clone.modCount = 0;
		
		for (Node<E> x = first; x != null; x = x.next)
			clone.add(x.item);
		
		return clone;
	}
	
	@Override
	public Object[] toArray() {
		Object[] result = new Object[size];
		int i = 0;
		for (Node<E> x = first; x != null; x = x.next)
			result[i++] = x.item;
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		
		int i = 0;
		Object[] result = a;
		for (Node<E> x = first; x != null; x = x.next)
			result[i++] = x.item;
		
		if(a.length > size)
			a[size] = null;
		
		return a;
	}
	
	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		// Write out any hidden serialization magic
		s.defaultWriteObject();

		// Write out size
		s.writeInt(size);

		// Write out all elements in the proper order.
		for (Node<E> x = first; x != null; x = x.next)
			s.writeObject(x.item);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		// Read in any hidden serialization magic
		s.defaultReadObject();

		// Read in size
		int size = s.readInt();

		// Read in all elements in the proper order.
		for (int i = 0; i < size; i++)
			linkLast((E) s.readObject());
	}
	
	/***1.8版本的Spliterator类暂未研究，所以未写****/
	
}
