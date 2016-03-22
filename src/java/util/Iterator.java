package java.util;

import java.lang.UnsupportedOperationException;

public interface Iterator<E> {
	boolean hasNext();
	
	E next();
	
	default void remove() {
        throw new UnsupportedOperationException("remove");
    }
}
