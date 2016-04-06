package org.source.util;

/**
 * ����
 */
public interface QueueMe<E> extends CollectionMe<E> {
	/**
	 * ��������ʱ���׳��쳣��һ������������
	 */
	boolean add(E e);
	
	/**
	 * ��������ʱ������false������ķ������ƣ�һ�����ڶ��У���
	 */
	boolean offer(E e);
	
	E remove();
	
	E poll();
	
	E element();
	
	E peek();
}
