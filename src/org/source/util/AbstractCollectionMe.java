package org.source.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public abstract class AbstractCollectionMe<T> implements CollectionMe<T> {

	public AbstractCollectionMe() {
	}

	@Override
	public abstract Iterator<T> iterator();
	
	@Override
	public abstract int size();

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		Iterator<T> it = this.iterator();
		if(o == null) {
			while (it.hasNext())
				if (it.next() == null)
					return true;
		}else {
			while (it.hasNext()) {
				if (o.equals(it.next()))
					return true;
			}
		}
		return false;
	}

	@Override
	public Object[] toArray() {
		Object[] r = new Object[this.size()];
		Iterator<T> it = this.iterator();
		for (int i = 0; i < r.length; i++) {
			if (!it.hasNext()) //�������С��Ԥ�ڵĴ�С������ʵ�ʵ����ݸ���Ϊ׼
				return Arrays.copyOf(r, i);
			r[i] = it.next();
		}
		return it.hasNext() ? finishToArray(r, it) : r; //ʵ�����ݴ���Ԥ�ڵĴ�С�������ڴ��ڼ�����˲����޸ĵ��������������·�������
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(E[] a) {
		int size = this.size();
		E[] r = a.length >= size ? a :  //�����������鳤�ȴ��ڵ��ڵ�ǰ�ĳ��ȣ��Դ��������Ϊ׼�������ѱ����ϵĳ���newһ���µ�����
			(E[]) Array.newInstance(a.getClass().getComponentType(), size);
		Iterator<T> it = this.iterator();
		
		for (int i = 0; i < r.length; i++) {
			if(!it.hasNext()) { //��Ԥ�ڵ�Ԫ�ظ�����
				if (a == r) { //r����a����β������NULL��ʾ����
					r[i] = null;
				} else if (a.length < i) { //a�ĳ��ȱ�ʵ��Ԫ�ظ���С����ʵ�ʳ��ȸ�������
					return Arrays.copyOf(r, i);
				}else { //a�Ĵ��ڵ���ʵ�ʳ��ȣ���Ԫ�ظ��Ƶ�a��
					System.arraycopy(r, 0, a, 0, i);
					if(a.length > i) //���a����ʵ�ʳ��ȣ���β������NULL��ʾ����
						a[i] = null;
				}
				return a;
			}
			r[i] = (E) it.next();
		}

		return it.hasNext() ? finishToArray(r, it) : r;
	}

	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	/**
	 * it�ĳ����Ǵ���r�ģ���it�ж������Ԫ�أ�����r��
	 * @param r ʵ��ת��������
	 * @param it ������
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <E> E[] finishToArray(E[] r, Iterator<?> it) { //ΪʲôԴ��Ҫ�þ�̬�ģ�
		int i = r.length;
		while (it.hasNext()) { //��������r��û�е�Ԫ��
			int cap = r.length;
			if(i == cap) { //�����ڼ䣬����Ԫ��δ��������٣��Զ�������ռ������չ
				int newCap = cap + (cap >> 1) + 1; //�ο���λ���㣬�൱����ԭ�г��������1.5��+1�Ŀռ�
				if(newCap - MAX_ARRAY_SIZE > 0)
					newCap = hugeCapacity(cap + 1);
				r = Arrays.copyOf(r, newCap); //��ԭr���飬�����³��ȵ�������
			}
			r[i++] = (E) it.next(); //����ڵ����ڼ䣬�����������㹻�ĳ��ȣ�����ԭ��λ�ÿ�ʼ���и�ֵ��
			//��������䳤�Ȳ������С���ȣ��˴������׳��쳣(����i=10,cap=9,r[10]��������)
		}
		return (i == r.length) ? r : Arrays.copyOf(r, i); //��ʵ�ʵ�Ԫ�ظ���i����r���·���ռ䣬��ֹ�Զ���չʱ������ķ��������˷�
	}

	/**
	 * ����Ԥ����ĳ��ȴ��ڸ�������󳤶����
	 */
	private int hugeCapacity(int minCapacity) {
		if(minCapacity < 0) //�Ӹ�1С��0��������ˣ�
			throw new OutOfMemoryError("Required array size too large");
		return (minCapacity > MAX_ARRAY_SIZE) ?
				Integer.MAX_VALUE : MAX_ARRAY_SIZE; //���ڸ�������󳤶ȣ�������󳤶Ƚض�
	}

	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException(); //������Բ���д�÷���������ʹ�����Ļ��������Լ�ʵ�֣������׳��쳣
	}

	@Override
	public boolean remove(Object o) {
		Iterator<T> it = this.iterator();
		if(o == null) {
			while (it.hasNext()) { //����null�ļ���
				if (it.next() == null) {
					it.remove();
					return true;
				}
			}
		}else {
			while (it.hasNext()) {
				if (o.equals(it.next())) {
					it.remove();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(CollectionMe<?> c) {
		for (Object e : c)
			if (!this.contains(e))
				return false;
		return true;
	}

	@Override
	public boolean addAll(CollectionMe<? extends T> c) {
		boolean modified = false;
		for (T t : c)
			if (add(t))
				modified = true; //����֤ȫ������ϣ�
		return modified;
	}

	@Override
	public boolean removeAll(CollectionMe<?> c) {
		Objects.requireNonNull(c);
		boolean modified = false;
		Iterator<T> it = this.iterator();
		while (it.hasNext()) {
			if (c.contains(it.next())) {
				it.remove();
				modified = true;
			}
		}
		
		return modified;
	}

	@Override
	public boolean retainAll(CollectionMe<?> c) {
		Objects.requireNonNull(c);
		boolean modified = false;
		Iterator<T> it = this.iterator();
		while (it.hasNext()) {
			if (!c.contains(it.next())) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public void clear() {
		Iterator<T> it = this.iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}

	@Override
	public String toString() {
		Iterator<T> it = this.iterator();
		if (!it.hasNext())
			return "[]";
		
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (;;) {
			T e = it.next();
			sb.append(e == this ? "(this Collection1)" : e);
			if (!it.hasNext())
				return sb.append(']').toString();
			sb.append(',').append(' ');
		}
	}
	
}
