package org.roderick.source.util;

import java.lang.Object;
import java.util.Iterator;


public interface CollectionMe<T> extends Iterable<T> {
	int size();
	
	boolean isEmpty();
	
	boolean contains(Object o);
	
	Iterator<T> iterator(); // ?
	
	Object[] toArray();
	
	<E> E[] toArray(E[] a); //<E> ?
	
	boolean add(T e);
	
	boolean remove(Object o);
	
	boolean containsAll(CollectionMe<?> c); //?ºÍT
	
	boolean addAll(CollectionMe<? extends T> c);
	
	boolean removeAll(CollectionMe<?> c);
	
	boolean retainAll(CollectionMe<?> c);
	
	void clear();
	
	boolean equals(Object o);
	
	/***************************/
	int hashCode();
	
}
