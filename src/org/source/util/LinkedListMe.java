package org.source.util;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public class LinkedListMe<E> extends AbstractSequentialListMe<E> implements ListMe<E> {
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
	
	public E getFirst() {
		final Node<E> f = first; //lock?
		if (f == null)
			throw new NoSuchElementException();
		return f.item;
	}

	public E getLast() {
		final Node<E> l = last;
		if (l == null)
			throw new NoSuchElementException();
		return l.item;
	}
	
	public E removeFirst() {
		final Node<E> f = first;
		if (f == null)
			throw new NoSuchElementException();
		return unlinkFirst(f);
	}
	
	public E removeLast() {
		final Node<E> l = last;
		if (l == null)
			throw new NoSuchElementException();
		return unlinkLast(l);
	}
	
	public void addFirst(E e) {
		linkFirst(e);
	}
	
	public void addLast(E e) {
		linkLast(e);
	}
	
	public boolean contains(Object o) {
		return indexOf(o) != -1;
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

	

	
	
	
	

	private void checkPositionIndex(int index) {
		if (!isPositionIndex(index))
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private boolean isPositionIndex(int index) {
		return index >= 0 && index <= size;
	}

	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size;
	}

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
	 * 访问首节点
	 */
	public E peek() {
		final Node<E> f = first;
		return (f == null) ? null : f.item;
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

}
