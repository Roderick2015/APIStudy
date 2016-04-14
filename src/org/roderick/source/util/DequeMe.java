package org.roderick.source.util;

import java.util.Iterator;

/**
 * ˫�˶��нӿ�
 */
public interface DequeMe<E> extends QueueMe<E> {
	/**
	 * ����������ʱ������offerFirst����Ϊ�����쳣
	 */
	void addFirst(E e);

	void addLast(E e);

	boolean offerFirst(E e);

	boolean offerLast(E e);

	E removeFirst();

	E removeLast();

	E pollFirst();

	E pollLast();

	E getFirst();

	E getLast();

	E peekFirst();

	E peekLast();

	boolean removeFirstOccurrence(Object o);

	boolean removeLastOccurrence(Object o);

	/**
	 * ����������ʱ������offer
	 */
	boolean add(E e);

	boolean offer(E e);

	E remove();

	E poll();

	E element();

	E peek();

	void push(E e);

	E pop();

	boolean remove(Object o);

	boolean contains(Object o);

	public int size();

	Iterator<E> iterator();

	Iterator<E> descendingIterator();
}
