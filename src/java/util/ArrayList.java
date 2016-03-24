package java.util;

import java.lang.Override;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class ArrayList<E> extends AbstractList<E> implements List<E> {
	private static final int DEFAULT_CAPACITY = 10;
	private static final Object[] EMPTY_ELEMENTDATA = {};
	private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
	
	transient Object[] elementData;
	private int size;
	
	public ArrayList() {
		this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
	}
	
	public ArrayList(int initialCapacity) {
		if (initialCapacity > 0) {
			this.elementData = new Object[initialCapacity];
		} else if (initialCapacity == 0) {
			this.elementData = EMPTY_ELEMENTDATA;
		} else {
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		}
	}
	
	public ArrayList(Collection<? extends E> c) {
		elementData = c.toArray();
		if ((size = elementData.length) != 0) {
			if (elementData.getClass() != Object[].class) //���toArray���ص��������Ͳ���Object[]����ָ����������copyһ��
				elementData = Arrays.copyOf(elementData, size, Object[].class);
		} else {
			this.elementData = EMPTY_ELEMENTDATA;
		}
	}
	
	public void trimToSize() {
		modCount++; //?
		if (size < elementData.length) { //���ʵ�ʵ�sizeС�����鵱ǰ�ĳ��ȣ���copyһ�ݼ��ٴ洢�ռ䣬�������û��ֵ���ÿ�
			elementData = (size == 0) 
				? EMPTY_ELEMENTDATA 
				: Arrays.copyOf(elementData, size);
		}
	}
	
	public void ensureCapacity(int minCapacity) {
		//��С��չ���������ǰ���ݲ�Ϊ�գ������0�ͺã�����Ϊ�գ�����չ���������Ĭ�ϵĿռ䣨10��
		int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
				? 0
				: DEFAULT_CAPACITY;
		
		if (minCapacity > minExpand) //��С�ռ������С��չ��
			ensureExplicitCapacity(minCapacity); //?�����С�ռ�С�ڵ�ǰ�ģ��Ͳ������䣬Ϊʲô���������飿
	}
	
	private void ensureExplicitCapacity(int minCapacity) {
		modCount++;
		
		if (minCapacity - elementData.length > 0)
			grow(minCapacity);
	}

	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	private void grow(int minCapacity) {
		int oldCapacity = elementData.length;
		int newCapacity = oldCapacity + (oldCapacity >> 1);
		if (newCapacity - minCapacity < 0) //���Զ���չ��Ҫ��
			newCapacity = minCapacity;
	//	if (newCapacity - MAX_ARRAY_SIZE > 0) //����������ռ�����
			
	}

	@Override
	public E get(int index) {
		return null;
	}

}
