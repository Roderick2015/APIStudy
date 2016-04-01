package org.source.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 全是迭代器实现的，顺序访问优先
 */
public abstract class AbstractSequentialListMe<E> extends AbstractListMe<E> {
	protected AbstractSequentialListMe() {
    }
	
	@Override
	public abstract ListIteratorMe<E> listIterator(int index);
	
	public E get(int index) {
		try {
			return listIterator(index).next();
		} catch (NoSuchElementException e) {
			throw new IndexOutOfBoundsException("Index: "+index);
		}
	}
	
	public E set(int index, E element) {
		try {
			ListIteratorMe<E> e = listIterator(index);
			E oldValue = e.next();
			e.set(element);
			return oldValue;
		} catch (NoSuchElementException e) {
			 throw new IndexOutOfBoundsException("Index: "+index);
		}
	}
	
	public void add(int index, E element) {
		try {
			listIterator(index).add(element);
		} catch (NoSuchElementException e) {
			 throw new IndexOutOfBoundsException("Index: "+index);
		}
	}
	
	public E remove(int index) {
		try {
			ListIteratorMe<E> e = listIterator(index);
			E outCast = e.next();
			e.remove();
			return outCast;
		} catch (NoSuchElementException e) {
			throw new IndexOutOfBoundsException("Index: "+index);
		}
	}
	
	public boolean addAll(int index, CollectionMe<? extends E> c) {
		try {
			boolean modified = false;
			ListIteratorMe<E> e1 = listIterator(index);
			Iterator<? extends E> e2 = c.iterator();
			while (e2.hasNext()) {
				e1.add(e2.next());
				modified = true;
			}
			return modified;
		} catch (NoSuchElementException e) {
			 throw new IndexOutOfBoundsException("Index: "+index);
		}
	}
	
	public Iterator<E> iterator() {
		return listIterator();
	}
}
