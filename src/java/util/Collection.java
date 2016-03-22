package java.util;

import java.lang.Object;

public interface Collection<T> extends Iterable<T> {
	int size();
	
	boolean isEmpty();
	
	boolean contains(Object o);
	
	Iterator<T> iterator(); // ?
	
	Object[] toArray();
	
	<E> E[] toArray(E[] a); //<E> ?
	
	boolean add(T e);
	
	boolean remove(Object o);
	
	boolean containsAll(Collection<?> c); //?ºÍT
	
	boolean addAll(Collection<? extends T> c);
	
	boolean removeAll(Collection<?> c);
	
	boolean retainAll(Collection<?> c);
	
	void clear();
	
	boolean equals(Object o);
}
