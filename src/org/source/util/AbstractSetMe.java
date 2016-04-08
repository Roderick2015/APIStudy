package org.source.util;

import java.util.Iterator;
import java.util.Objects;

/**
 * 此类并没有重写 AbstractCollection 类中的任何实现
 */
public abstract class AbstractSetMe<E> extends AbstractCollectionMe<E> implements SetMe<E> {
	protected AbstractSetMe() {
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		
		if (!(o instanceof SetMe))
			return false;
		
		CollectionMe<?> c = (CollectionMe<?>) o;
		if (c.size() != size())
			return false;
		try {
			return containsAll(c);
		} catch (ClassCastException unused) {
			return false;
		} catch (NullPointerException unused) {
			return false;
		}
	}
	
	/**
	 * 集合的hashCode是所有元素哈希值的综合，null元素值为0
	 * 覆写了equals方法，需重写hashcode方法以保住hashCode的一致
	 */
	@Override
	public int hashCode() {
		int h = 0;
		Iterator<E> i = iterator();
		while (i.hasNext()) {
			E obj = i.next();
			if (obj != null)
				h += obj.hashCode();
		}
		return h;
	}
	
	public boolean removeAll(CollectionMe<?> c) {
		Objects.requireNonNull(c);
		boolean modified = false;

		if (size() > c.size()) { // 迭代小的那个
			for (Iterator<?> i = c.iterator(); i.hasNext();)
				modified |= remove(i.next()); // 位运算：或，即有一个返回true，则成功。因为该方法只有在未找到元素时返回false
		} else {
			for (Iterator<?> i = iterator(); i.hasNext();)
				if (c.contains(i.next())) {
					i.remove();
					modified = true;
				}
		}
		return modified;
	}
}
