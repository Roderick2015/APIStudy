package java.util;

public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {
	protected AbstractList() {
	}

	@Override
	public boolean add(E e) {
		add(size(), e);
		return true;
	}

	abstract public E get(int index);

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		ListIterator<E> it = listIterator();
		if (o == null) {
			while (it.hasNext()) {
				if (it.next() == null)
					return it.previousIndex();
			}
		} else {
			while (it.hasNext()) {
				if (o.equals(it.next()))
					return it.previousIndex();
			}
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		ListIterator<E> it = listIterator();
		if (o == null) {
			while (it.hasPrevious())
				if (it.previous() == null)
					return it.nextIndex();
		} else {
			while (it.hasPrevious())
				if (o.equals(it.previous()))
					return it.nextIndex();
		}
		return -1;
	}

	@Override
	public void clear() {
		removeRange(0, size());
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		rangeCheckForAdd(index);
		boolean modified = false;
		for(E e : c) {
			add(index++, e); //出现失败抛出异常？中断添加，之前的已加上？未抛出异常的话，改动了就成功
			modified = true;
		}
		return modified;
	}

	/**
	 * 覆写{@link AbstractCollection#iterator}提供具体的迭代器
	 */
	@Override
	public Iterator<E> iterator() {
		return new Itr();
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		rangeCheckForAdd(index);
		return new ListItr(index);
	}

	/**
	 * 检测该list是否实现了RandomAccess接口，而采用不同的遍历算法
	 */
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return (this instanceof RandomAccess ?
				new RandomAccessSubList<>(this, fromIndex, toIndex) :
				new SubList<>(this, fromIndex, toIndex));
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof List))
			return false;
		
		ListIterator<E> e1 = listIterator();
		ListIterator<?> e2 = ((List<?>) o).listIterator();
		while (e1.hasNext() && e2.hasNext()) {
			E o1 = e1.next();
			Object o2 = e2.next();
			if(!(o1 == null ? o2 == null : o1.equals(o2))) //这种判断的写法要学习
				return false;
		}
		return !(e1.hasNext() || e2.hasNext()); //如果比对完后，又有元素变动，则返回false
	}
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		for (E e : this)
			hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
		return hashCode;
	}
	
	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return super.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return super.contains(o);
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return super.toArray();
	}

	@Override
	public <E> E[] toArray(E[] a) {
		// TODO Auto-generated method stub
		return super.toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return super.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return super.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		// TODO Auto-generated method stub
		return super.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return super.retainAll(c);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	

	

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}
	
	protected transient int modCount = 0;
	
	/**
	 * 检查是否越界
	 */
	private void rangeCheckForAdd(int index) {
		if(index < 0 || index > size())
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private String outOfBoundsMsg(int index) {
		return "Index:" + index + ", Size:" + size();
	}
	
	protected void removeRange(int fromIndex, int toIndex) {
		ListIterator<E> it = listIterator(fromIndex);
		for (int i = 0, n = toIndex - fromIndex; i < n; i++) {
			it.next();
			it.remove(); //删除迭代器上一个next或previous方法返回的元素
		}
	}
	
	private class Itr implements Iterator<E> {
		int cursor = 0; //游标，索引
		int lastRet = -1; //上一次访问的是第几个元素
		int expectedModCount = modCount; //对list修改次数的期望值，默认为0

		@Override
		public boolean hasNext() {
			return cursor != size();
		}

		@Override
		public E next() {
			checkForComodification();
			try {
				int i = cursor;
				E next = get(i);
				lastRet = i;
				cursor = i + 1;
				return next;
			} catch (IndexOutOfBoundsException e) {
				checkForComodification(); //两种异常检测
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();
			
			try {
				AbstractList.this.remove(lastRet);
				if (lastRet < cursor)
					cursor--;
				lastRet = -1; //防止没有next就直接调用remove，或重复调用remove
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}
		}

		final void checkForComodification() {
			if(modCount != expectedModCount)
				throw new ConcurrentModificationException();
		}
	}
	
	private class ListItr extends Itr implements ListIterator<E> {
		ListItr(int index) {
			super.cursor = index;
		}
		
		@Override
		public boolean hasPrevious() {
			return cursor != 0;
		}

		@Override
		public E previous() {
			checkForComodification();
			try {
				int i = cursor - 1;
				E previous = get(i);
				lastRet = cursor = i;
				return previous;
			} catch (IndexOutOfBoundsException e) {
				checkForComodification();
				throw new NoSuchElementException();
			}
		}

		@Override
		public int nextIndex() {
			return cursor;
		}

		@Override
		public int previousIndex() {
			return cursor - 1;
		}

		@Override
		public void set(E e) {
			if (lastRet < 0)
				throw new IllegalStateException();
			checkForComodification();
			
			try {
				AbstractList.this.set(lastRet, e); //替换上一个元素
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}

		@Override
		public void add(E e) {
			checkForComodification();
			
			try {
				int i = cursor;
				AbstractList.this.add(i, e);
				lastRet = -1;
				cursor = i + 1; //专门拿个i变量来赋值？
				expectedModCount = modCount;
			} catch (IndexOutOfBoundsException ex) {
				throw new ConcurrentModificationException();
			}
		}
	}
	
	class SubList<E> extends AbstractList<E> {
		private final AbstractList<E> l;
		private final int offset;
		private int size;
		
		SubList(AbstractList<E> list, int fromIndex, int toIndex) {
			if (fromIndex < 0)
				throw new IndexOutOfBoundsException("fromIndex= " + fromIndex);
			if (toIndex > list.size())
				throw new IndexOutOfBoundsException("toIndex= " + toIndex);
			if (fromIndex > toIndex)
				throw new IndexOutOfBoundsException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");

			this.l = list;
			this.offset = fromIndex;
			this.size = toIndex - fromIndex;
			this.modCount = l.modCount; // ?
		}

		@Override
		public E set(int index, E element) {
			rangeCheck(index);
			checkForComodification();
			return (E)l.set(index + offset, element); //转换
		}
		
		@Override
		public E get(int index) {
			rangeCheck(index);
			checkForComodification();
			return (E)l.get(index + offset);
		}
		
		@Override
		public int size() {
			checkForComodification();
			return size;
		}
		
		@Override
		public void add(int index, E element) {
			rangeCheck(index);
			checkForComodification();
			l.add(index + offset, element);
			modCount = l.modCount; //l增加元素后，modCount会+1，sublist中的该变量应及时更新
			size += 1;
		}
		
		
		private void checkForComodification() {
			if(modCount != l.modCount)
				throw new ConcurrentModificationException();
		}

		private void rangeCheck(int index) {
			if((index < 0) || (index >= size)) {
				throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
			}
		}
		
		private void rangeCheckForAdd(int index) {
			if((index < 0) || (index > size)) { //可等于
				throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
			}
		}

		private String outOfBoundsMsg(int index) {
			return "Index:" + index + ", Size:" + size();
		}
		
		@Override
		public boolean add(E e) {
			// TODO Auto-generated method stub
			return super.add(e);
		}

		

		

		

		@Override
		public E remove(int index) {
			// TODO Auto-generated method stub
			return super.remove(index);
		}

		@Override
		public int indexOf(Object o) {
			// TODO Auto-generated method stub
			return super.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			// TODO Auto-generated method stub
			return super.lastIndexOf(o);
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			super.clear();
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			// TODO Auto-generated method stub
			return super.addAll(index, c);
		}

		@Override
		public Iterator<E> iterator() {
			// TODO Auto-generated method stub
			return super.iterator();
		}

		@Override
		public ListIterator<E> listIterator() {
			// TODO Auto-generated method stub
			return super.listIterator();
		}

		@Override
		public ListIterator<E> listIterator(int index) {
			// TODO Auto-generated method stub
			return super.listIterator(index);
		}

		@Override
		public List<E> subList(int fromIndex, int toIndex) {
			// TODO Auto-generated method stub
			return super.subList(fromIndex, toIndex);
		}

		@Override
		public boolean equals(Object o) {
			// TODO Auto-generated method stub
			return super.equals(o);
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return super.hashCode();
		}

		

		@Override
		public boolean isEmpty() {
			// TODO Auto-generated method stub
			return super.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			// TODO Auto-generated method stub
			return super.contains(o);
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			return super.toArray();
		}

		@Override
		public <E> E[] toArray(E[] a) {
			// TODO Auto-generated method stub
			return super.toArray(a);
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			return super.remove(o);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return super.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			// TODO Auto-generated method stub
			return super.addAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return super.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return super.retainAll(c);
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return super.toString();
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			// TODO Auto-generated method stub
			return super.clone();
		}

		@Override
		protected void finalize() throws Throwable {
			// TODO Auto-generated method stub
			super.finalize();
		}

		@Override
		protected void removeRange(int fromIndex, int toIndex) {
			// TODO Auto-generated method stub
			super.removeRange(fromIndex, toIndex);
		}
		
		
		
	}
}
