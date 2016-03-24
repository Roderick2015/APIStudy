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
			if (elementData.getClass() != Object[].class) //如果toArray返回的数组类型不是Object[]，则按指定类型重新copy一份
				elementData = Arrays.copyOf(elementData, size, Object[].class);
		} else {
			this.elementData = EMPTY_ELEMENTDATA;
		}
	}
	
	public void trimToSize() {
		modCount++; //?
		if (size < elementData.length) { //如果实际的size小于数组当前的长度，则copy一份减少存储空间，如果里面没有值则置空
			elementData = (size == 0) 
				? EMPTY_ELEMENTDATA 
				: Arrays.copyOf(elementData, size);
		}
	}
	
	public void ensureCapacity(int minCapacity) {
		//最小扩展数，如果当前数据不为空，则大于0就好，数据为空，则扩展数必须大于默认的空间（10）
		int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
				? 0
				: DEFAULT_CAPACITY;
		
		if (minCapacity > minExpand) //最小空间大于最小扩展数
			ensureExplicitCapacity(minCapacity); //?如果最小空间小于当前的，就不会扩充，为什么分两步检验？
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
		if (newCapacity - minCapacity < 0) //比自动扩展的要大
			newCapacity = minCapacity;
	//	if (newCapacity - MAX_ARRAY_SIZE > 0) //比允许的最大空间数大
			
	}

	@Override
	public E get(int index) {
		return null;
	}

}
