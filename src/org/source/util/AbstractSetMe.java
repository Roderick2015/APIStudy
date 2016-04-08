package org.source.util;

import java.util.Iterator;
import java.util.Objects;

/**
 * ���ಢû����д AbstractCollection ���е��κ�ʵ��
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
	 * ���ϵ�hashCode������Ԫ�ع�ϣֵ���ۺϣ�nullԪ��ֵΪ0
	 * ��д��equals����������дhashcode�����Ա�סhashCode��һ��
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

		if (size() > c.size()) { // ����С���Ǹ�
			for (Iterator<?> i = c.iterator(); i.hasNext();)
				modified |= remove(i.next()); // λ���㣺�򣬼���һ������true����ɹ�����Ϊ�÷���ֻ����δ�ҵ�Ԫ��ʱ����false
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
