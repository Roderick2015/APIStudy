package org.source.util;

import java.util.Iterator;

public interface ListIteratorMe<E> extends Iterator<E> {
	boolean hasNext();

	E next();

	boolean hasPrevious();

	E previous();

	int nextIndex();

	int previousIndex();

	void remove();

	void set(E e);
	
	void add(E e);
}
