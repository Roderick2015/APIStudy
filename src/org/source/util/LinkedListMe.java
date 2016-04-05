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
	 * ����׽ڵ�
	 */
	private void linkFirst(E e) {
		final Node<E> f = first; //Ϊʲô����final?
		final Node<E> newNode = new Node<>(null, e, f);
		first = newNode;
		if (f == null) //�������׽ڵ��β�ڵ���ͬһ����Ϊʲô����size�жϣ�
			last = newNode;
		else
			f.prev = newNode; //�ɵ��׽ڵ���Ϊ�ڶ����ڵ�
		size++;
		modCount++;
	}

	/**
	 * ���β�ڵ�
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
	 * �ڽڵ�succ��ǰ�����e
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
	 * �Ƴ��׽ڵ㣨f�������׽ڵ㣩
	 * @param f
	 * @return
	 */
	private E unlinkFirst(Node<E> f) {
		final E element = f.item;
		final Node<E> next = f.next;
		f.item = null;
		f.next = null; //�ýڵ�f�������
		first = next;
		if (next == null)
			last = null;
		else 
			next.prev = null; //�¸��ڵ���Ϊ�׽ڵ�
		size--;
		modCount++;
		return element;
	}
	
	/**
	 * �Ƴ�β�ڵ�
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
	 * ��Ԫ�ش��������Ƴ�
	 */
	E unlink(Node<E> x) {
		// assert x != null;
		final E element = x.item;
		final Node<E> next = x.next;
		final Node<E> prev = x.prev;

		if (prev == null) { //����
			first = next; //�Ƴ���ǰԪ�أ��¸�Ԫ�ض�������λ��
		} else {
			prev.next = next; //��Ԫ����������
			x.prev = null;
		}

		if (next == null) { //��β
			last = prev; //�ϸ�Ԫ�ض�����βλ��
		} else {
			next.prev = prev; //����
			x.next = null;
		}

		x.item = null; //��ǰԪ���ÿգ�GC
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
		if (index == size) { // ĩβ
			succ = null;
			pred = last;
		} else { // ��ǰλ��
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
	 * ����������Ԫ�أ��ñ�����Ч�ʲ���
	 */
	Node<E> node(int index) {
		if (index < (size >> 1)) { // indexС��size/2�������߿�ʼ���������ֲ���
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
				index--; //ʵ��������size-1
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
	 * �����׽ڵ�
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
		private Node<E> lastReturned; //�ϴβ������صĽڵ�
		private Node<E> next;
		private int nextIndex; //�������¸�λ��
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

			Node<E> lastNext = lastReturned.next;//��ȡ����ǰλ�õ�Ԫ��
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
