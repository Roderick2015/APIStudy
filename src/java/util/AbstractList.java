package java.util;

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E>{
	protected AbstractList() {
	}
	
	public boolean add(E e) {
		add(size(), e);
		return true;
	}
	
}
