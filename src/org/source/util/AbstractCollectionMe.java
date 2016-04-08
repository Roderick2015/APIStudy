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
			if (!it.hasNext()) //如果数据小于预期的大小，则以实际的数据个数为准
				return Arrays.copyOf(r, i);
			r[i] = it.next();
		}
		return it.hasNext() ? finishToArray(r, it) : r; //实际数据大于预期的大小（比如在此期间进行了并发修改等情况），则会重新分配数组
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(E[] a) {
		int size = this.size();
		E[] r = a.length >= size ? a :  //如果传入的数组长度大于等于当前的长度，以传入的数组为准。否则已本集合的长度new一个新的数组
			(E[]) Array.newInstance(a.getClass().getComponentType(), size);
		Iterator<T> it = this.iterator();
		
		for (int i = 0; i < r.length; i++) {
			if(!it.hasNext()) { //比预期的元素个数少
				if (a == r) { //r就是a，在尾部加上NULL表示结束
					r[i] = null;
				} else if (a.length < i) { //a的长度比实际元素个数小，以实际长度复制数组
					return Arrays.copyOf(r, i);
				}else { //a的大于等于实际长度，把元素复制到a中
					System.arraycopy(r, 0, a, 0, i);
					if(a.length > i) //如果a大于实际长度，在尾部加上NULL表示结束
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
	 * it的长度是大于r的，把it中多出来的元素，放入r中
	 * @param r 实际转换的数组
	 * @param it 迭代器
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <E> E[] finishToArray(E[] r, Iterator<?> it) { //为什么源码要用静态的？
		int i = r.length;
		while (it.hasNext()) { //继续迭代r中没有的元素
			int cap = r.length;
			if(i == cap) { //迭代期间，数组元素未新增或减少，自动对数组空间进行扩展
				int newCap = cap + (cap >> 1) + 1; //参考移位运算，相当于在原有长度上添加1.5倍+1的空间
				if(newCap - MAX_ARRAY_SIZE > 0)
					newCap = hugeCapacity(cap + 1);
				r = Arrays.copyOf(r, newCap); //把原r数组，放入新长度的数组中
			}
			r[i++] = (E) it.next(); //如果在迭代期间，主动扩充了足够的长度，则会从原来位置开始进行赋值。
			//如果，扩充长度不够或减小长度，此处将会抛出异常(比如i=10,cap=9,r[10]出现问题)
		}
		return (i == r.length) ? r : Arrays.copyOf(r, i); //以实际的元素个数i，把r重新分配空间，防止自动扩展时，过多的分配而造成浪费
	}

	/**
	 * 处理预分配的长度大于给定的最大长度情况
	 */
	private int hugeCapacity(int minCapacity) {
		if(minCapacity < 0) //加个1小于0就是溢出了？
			throw new OutOfMemoryError("Required array size too large");
		return (minCapacity > MAX_ARRAY_SIZE) ?
				Integer.MAX_VALUE : MAX_ARRAY_SIZE; //大于给定的最大长度，则以最大长度截断
	}

	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException(); //子类可以不覆写该方法，但是使用它的话，必须自己实现，否则抛出异常
	}

	@Override
	public boolean remove(Object o) {
		Iterator<T> it = this.iterator();
		if(o == null) {
			while (it.hasNext()) { //包含null的集合
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
				modified = true; //不保证全部添加上？
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
