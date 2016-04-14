package org.roderick.source.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.UnaryOperator;

public interface ListMe<E> extends CollectionMe<E> {
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

	boolean addAll(int index, CollectionMe<? extends E> c); // additional

	boolean removeAll(CollectionMe<?> c);

	boolean retainAll(CollectionMe<?> c);

	void clear();

	boolean equals(Object o);

	/******** additional ********/

	int hashCode();

	E get(int index);

	E set(int index, E element);

	void add(int index, E element);

	E remove(int index);

	int indexOf(Object o);

	int lastIndexOf(Object o);

	ListIteratorMe<E> listIterator();

	ListIteratorMe<E> listIterator(int index);

	ListMe<E> subList(int fromIndex, int toIndex); // fromIndex（包括 ）和 toIndex（不包括）

	default void replaceAll(UnaryOperator<E> operator) {
		Objects.requireNonNull(operator);
		final ListIteratorMe<E> li = this.listIterator();
		while (li.hasNext()) {
			li.set(operator.apply(li.next()));
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
    default void sort(Comparator<? super E> c) {
        Object[] a = this.toArray();
        Arrays.sort(a, (Comparator) c);
        ListIteratorMe<E> i = this.listIterator();
        for (Object e : a) {
            i.next();
            i.set((E) e);
        }
    }
	
	/*@Override
    default Spliterator<E> spliterator() {
        return Spliterators.spliterator(this, Spliterator.ORDERED);
    }*/
}
