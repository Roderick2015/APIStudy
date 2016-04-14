package org.roderick.source.util;

import java.util.Iterator;

public interface SetMe<E> extends CollectionMe<E> {
	int size();

	boolean isEmpty();

	boolean contains(Object o);

	Iterator<E> iterator();

	Object[] toArray();

	<T> T[] toArray(T[] a);

	boolean add(E e);

	boolean remove(Object o);

	boolean containsAll(CollectionMe<?> c);

	boolean addAll(CollectionMe<? extends E> c);

	boolean retainAll(CollectionMe<?> c);

	boolean removeAll(CollectionMe<?> c);

	void clear();

	boolean equals(Object o);

	int hashCode();
}
