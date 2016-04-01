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

	@Override
	public ListIteratorMe<E> listIterator(int index) {
		checkPositionIndex(index);
		return new ListItrMe(index);
	}

	private class ListItrMe implements ListIteratorMe<E> {
		private Node<E> lastReturned;
		private Node<E> next;
		private int nextIndex;
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

			Node<E> lastNext = lastReturned.next;
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

	E unlink(Node<E> x) {
		// assert x != null;
		final E element = x.item;
		final Node<E> next = x.next;
		final Node<E> prev = x.prev;

		if (prev == null) {
			first = next;
		} else {
			prev.next = next;
			x.prev = null;
		}

		if (next == null) {
			last = prev;
		} else {
			next.prev = prev;
			x.next = null;
		}

		x.item = null;
		size--;
		modCount++;
		return element;
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

}
