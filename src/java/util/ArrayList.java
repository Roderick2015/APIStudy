package java.util;

import java.lang.Override;

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
	
	private void ensureCapacityInternal(int minCapacity) {
		if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
			minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity); //?
		
		ensureExplicitCapacity(minCapacity);
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
		if (newCapacity - MAX_ARRAY_SIZE > 0) //����������ռ�����
			newCapacity = hugeCapacity(minCapacity);
		elementData = Arrays.copyOf(elementData, newCapacity);
	}

	private static int hugeCapacity(int minCapacity) { //�þ�̬����?
        if (minCapacity < 0)
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}
	
	@Override
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < size; i++) {
				if (elementData[i] == null)
					return i;
			}
		}else {
			for (int i = 0; i < size; i++) {
				if (o.equals(elementData[i]))
					return i;
			}
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = size-1; i >=0; i--)
				if(elementData[i] == null)
					return i;
		}else {
			for (int i = size-1; i >=0; i--)
				if(o.equals(elementData[i]))
					return i;
		}
		return -1;
	}
	
	@Override
	public Object clone() {
		try {
			ArrayList<?> v = (ArrayList<?>) super.clone(); //��¡����
			v.elementData = Arrays.copyOf(elementData, size); //��������
			v.modCount = 0; //��ʼ��
			return v;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}
	
	@Override
	public Object[] toArray() {
		return Arrays.copyOf(elementData, size);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < size) //����С�ڵ�ǰlist����a�����Ͱѵ�ǰlist�����ݸ�һ�ݲ�����
			return (T[]) Arrays.copyOf(elementData, size, a.getClass());
		System.arraycopy(elementData, 0, a, 0, size); //�����ݸ��Ƶ�a
		if (a.length > size) //a�бȵ�ǰlist�ж�����ǲ��֣���null
			a[size] = null;
		return a;
	}
	
	@SuppressWarnings("unchecked")
	E elementData(int index) {
		return (E) elementData[index];
	}
	
	@Override
	public E get(int index) {
		rangeCheck(index);
		return elementData(index);
	}
	
	@Override
	public E set(int index, E element) {
		rangeCheck(index);
		
		E oldValue = elementData(index);
		elementData[index] = element;
		return oldValue;
	}
	
	@Override
	public boolean add(E e) {
		ensureCapacityInternal(size + 1);
		
		return false;
	}

	private void rangeCheck(int index) {
		if (index > size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
	
	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size;
	}

}
