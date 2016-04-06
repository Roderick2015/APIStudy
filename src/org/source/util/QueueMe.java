package org.source.util;

/**
 * 队列
 */
public interface QueueMe<E> extends CollectionMe<E> {
	/**
	 * 容量不够时，抛出异常（一般用于链表？）
	 */
	boolean add(E e);
	
	/**
	 * 容量不够时，返回false，下面的方法类似（一般用于队列？）
	 */
	boolean offer(E e);
	
	E remove();
	
	E poll();
	
	E element();
	
	E peek();
}
