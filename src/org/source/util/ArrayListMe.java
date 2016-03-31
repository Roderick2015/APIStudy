package org.source.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.function.Consumer;

//RandomAccess�ӿڣ�ʹArrayList���Բ����������Ԫ�ص��㷨����get(index)��Ȼ����������㷨Ч�ʸ��ߣ���
//Ĭ�ϲ������Է����㷨�������㷨�ĶԱȣ�����?
public class ArrayListMe<E> extends AbstractListMe<E> implements ListMe<E>, Cloneable,
	Serializable, RandomAccess {
	private static final long serialVersionUID = 3726548988176882299L;
	
	private static final int DEFAULT_CAPACITY = 10;
	private static final Object[] EMPTY_ELEMENTDATA = {};
	private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
	
	transient Object[] elementData;
	private int size;
	
	public ArrayListMe() {
		this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
	}
	
	public ArrayListMe(int initialCapacity) {
		if (initialCapacity > 0) {
			this.elementData = new Object[initialCapacity];
		} else if (initialCapacity == 0) {
			this.elementData = EMPTY_ELEMENTDATA;
		} else {
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		}
	}
	
	public ArrayListMe(CollectionMe<? extends E> c) {
		elementData = c.toArray();
		if ((size = elementData.length) != 0) {
			if (elementData.getClass() != Object[].class) //���toArray���ص��������Ͳ���Object[]����ָ����������copyһ�ݣ�Ϊʲô��
				elementData = Arrays.copyOf(elementData, size, Object[].class);
		} else {
			this.elementData = EMPTY_ELEMENTDATA;
		}
	}
	
	/**
	 * ȥ������ռ�
	 */
	public void trimToSize() {
		modCount++; //?
		if (size < elementData.length) { //���ʵ�ʵ�sizeС�����鵱ǰ�ĳ��ȣ���copyһ�ݼ��ٴ洢�ռ䣬�������û��ֵ���ÿ�
			elementData = (size == 0) 
				? EMPTY_ELEMENTDATA 
				: Arrays.copyOf(elementData, size);
		}
	}
	
	/**
	 * ����������list�ӿ���δ�����÷����������ֱ�Ӵ�����ArrayList���󣬿�ͨ���÷����ֶ���չ����ĳ��ȣ���������������ȫ
	 */
	public void ensureCapacity(int minCapacity) {
		//��С��չ���������ǰ���ݲ�Ϊ�գ������0�ͺã�����Ϊ�գ�����չ���������Ĭ�ϵĿռ䣨10��
		int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
				? 0
				: DEFAULT_CAPACITY;
		
		if (minCapacity > minExpand)
			ensureExplicitCapacity(minCapacity);
	}
	
	/**
	 * @param minCapacity �������С�ռ䣨�ڲ�������
	 */
	private void ensureCapacityInternal(int minCapacity) {
		if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
			minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity); //�������С�ռ��Ĭ�ϵ���С�ռ䣬ȡ���ֵ
		
		ensureExplicitCapacity(minCapacity);
	}
	
	private void ensureExplicitCapacity(int minCapacity) {
		modCount++;
		
		if (minCapacity - elementData.length > 0) //�������С�ռ������ڵ�ǰ��size����չ�����С
			grow(minCapacity);
	}

	private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
	
	private void grow(int minCapacity) {
		int oldCapacity = elementData.length;
		int newCapacity = oldCapacity + (oldCapacity >> 1); //��չ1.5��
		if (newCapacity - minCapacity < 0) //���Զ���չ��Ҫ��
			newCapacity = minCapacity;
		if (newCapacity - MAX_ARRAY_SIZE > 0) //�������������ռ����󣬽��н�ȡ
			newCapacity = hugeCapacity(minCapacity);
		elementData = Arrays.copyOf(elementData, newCapacity);
	}

	private static int hugeCapacity(int minCapacity) { //�þ�̬�������������л�?
        if (minCapacity < 0)
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ?
            Integer.MAX_VALUE :
            MAX_ARRAY_SIZE;
    }
	
	@Override
	public int size() {
		return size;
	}
	
	@Override
	public boolean isEmpty() {
		return size == 0;
	}
	
	@Override
	public boolean contains(Object o) {
		return indexOf(o) >= 0;
	}
	
	@Override
	public int indexOf(Object o) {
		if (o == null) {
			for (int i = 0; i < size; i++) {
				if (elementData[i] == null)
					return i;
			}
		}else {
			for (int i = 0; i < size; i++) {
				if (o.equals(elementData[i]))
					return i;
			}
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = size-1; i >=0; i--)
				if(elementData[i] == null)
					return i;
		}else {
			for (int i = size-1; i >=0; i--)
				if(o.equals(elementData[i]))
					return i;
		}
		return -1;
	}
	
	@Override
	public Object clone() {
		try {
			ArrayListMe<?> v = (ArrayListMe<?>) super.clone(); //��¡����
			v.elementData = Arrays.copyOf(elementData, size); //ȥ������Ŀռ䣬��ʡ��֧
			v.modCount = 0; //��ʼ��
			return v;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}
	
	@Override
	public Object[] toArray() {
		return Arrays.copyOf(elementData, size);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		if (a.length < size) //����С�ڵ�ǰlist����a�����Ͱѵ�ǰlist�����ݸ�һ�ݲ�����
			return (T[]) Arrays.copyOf(elementData, size, a.getClass());
		System.arraycopy(elementData, 0, a, 0, size); //�����ݸ��Ƶ�a
		if (a.length > size) //���a�Ŀռ�Ȱ�ԭ��������һλ��Ϊnull����ȷ���б�ĳ���
			a[size] = null;
		return a;
	}
	
	@SuppressWarnings("unchecked")
	E elementData(int index) {
		return (E) elementData[index];
	}
	
	@Override
	public E get(int index) {
		rangeCheck(index);
		return elementData(index);
	}
	
	@Override
	public E set(int index, E element) {
		rangeCheck(index);
		
		E oldValue = elementData(index);
		elementData[index] = element;
		return oldValue;
	}
	
	/** 
	 * ��ʵ֤�����Լ�nullԪ�أ��ؼ���sizeҪ++
	 */
	@Override
	public boolean add(E e) {
		ensureCapacityInternal(size + 1); //����ռ� = ��ǰsize + 1
		elementData[size++] = e;
		return true;
	}
	
	/**
	 * @param index ����λ��ֻ��<=size����ǰ�����ĩβλ�ò���Ԫ��
	 * ������a[1,2]��size=2����ֻ����a[0-2]��λ���ϸ�ֵ��a[2]�൱��add����
	 * index=1�൱�ڰ�ԭa[1]��a[2]��Ԫ������Ųһλ��Ȼ����a[1]λ�ò����µ�Ԫ��
	 */
	@Override
	public void add(int index, E element) {
		rangeCheckForAdd(index);
		ensureCapacityInternal(size + 1);
		System.arraycopy(elementData, index, elementData,
				index + 1, size - index); //��indexλ�ú����Ԫ��ͳһ����һλ
		elementData[index] = element;
		size++;
	}

	@Override
	public E remove(int index) {
		rangeCheck(index);
		
		modCount++;
		E oldValue = elementData(index);
		int numMoved = size - index - 1;
		if (numMoved > 0) //�����Ƴ�Ԫ�ص�λ�ã������ж��ٸ�Ԫ����Ҫ���������ƶ�����ԭλ�õ�Ԫ�ظ��ǵ�
			System.arraycopy(elementData, index + 1, 
					elementData, index, numMoved);
		elementData[--size] = null; //�������λ����Ϊnull,��GC���գ����õĻ�������ظ�Ԫ�أ���Ϊcopyֻ�Ǹ��Ƹ��ǣ�û�����ǵ���Ԫ�ػ�����
		return oldValue;
	}
	
	@Override
	public boolean remove(Object o) {
		if (o == null) {
			for (int index = 0; index < size; index++)
				if (elementData[index] == null) {
					fastRemove(index);
					return true;
				}
		} else {
			for (int index = 0; index < size; index++)
				if (o.equals(elementData[index])) {
					fastRemove(index);
					return true;
				}
		}
		return false;
	}
	
	private void fastRemove(int index) {
		modCount++;
		int numMoved = size - index - 1;
		if (numMoved > 0)
			System.arraycopy(elementData, index + 1, elementData,
					index, numMoved);
		elementData[--size] = null;
		
	}
	
	@Override
	public void clear() {
		modCount++;
		
		for (int i = 0; i < size; i++)
			elementData[i] = null; //������null��GC����
		
		size = 0;
	}
	
	@Override
	public boolean addAll(CollectionMe<? extends E> c) {
		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacityInternal(size + numNew);
		System.arraycopy(a, 0, elementData, size, numNew);
		size += numNew;
		return numNew != 0;
	}
	
	@Override
	public boolean addAll(int index, CollectionMe<? extends E> c) {
		rangeCheckForAdd(index);
		
		Object[] a = c.toArray();
		int numNew = a.length;
		ensureCapacityInternal(size + numNew);
		
		int numMoved = size - index;
		if (numMoved > 0)  //����Ԫ���м䣬�����Ԫ����ҪŲ
			System.arraycopy(elementData, index, elementData,
					index + numNew, numMoved);
		
		//index����size���ǽ�������Ӿ�����
		System.arraycopy(a, 0, elementData, index, numNew);
		size += numNew;
		return numNew != 0;
	}
	
	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		modCount++;
		int numMoved = size - toIndex; //����Ԫ����Ҫ�ƶ���λ��
		System.arraycopy(elementData, toIndex, elementData, fromIndex, numMoved);
		
		//�ѿյ�λ�ã��ֶ���Ϊnull
		int newSize = size - (toIndex-fromIndex);
		for (int i = newSize; i < size; i++) {
			elementData[i] = null;
		}
		size = newSize;
	}
	
	private void rangeCheck(int index) {
		if (index >= size)
			throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
	}

	private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
	
	private String outOfBoundsMsg(int index) {
		return "Index: " + index + ", Size: " + size;
	}
	
	/** 
	 * ��c�а�����Ԫ��ȫ��ɾ��
	 */
	@Override
	public boolean removeAll(CollectionMe<?> c) {
		Objects.requireNonNull(c);
		return batchRemove(c, false);
	}
	
	/** 
	 * ��c�а�����Ԫ��ȫ��������������ɾ��
	 */
	@Override
	public boolean retainAll(CollectionMe<?> c) {
		Objects.requireNonNull(c);
		return batchRemove(c, true);
	}

	private boolean batchRemove(CollectionMe<?> c, boolean complement) {
		final Object[] elementData = this.elementData;
		int r = 0, w = 0;
		boolean modified = false;
		try {
			for (; r < size; r++)
				if (c.contains(elementData[r]) == complement) //false �Ѳ�������Ԫ����������true�Ѱ�����Ԫ��������
					elementData[w++] = elementData[r]; //ע���Ǹ��ǣ�������ͨ��size��ֵnull
		} finally {
			// Preserve behavioral compatibility with AbstractCollection,
			// even if c.contains() throws.
			if (r != size) { //�������׳��쳣���ж�ʱ��r�ǻ�С��size�ģ���ͨ���÷������в�ȫ����Ϊһ�ֲ��ȴ�ʩ
				System.arraycopy(elementData, r, elementData, w, size - r);
				w += size - r;
			}
			if (w != size) {
				// clear to let GC do its work
				for (int i = w; i < size; i++)
					elementData[i] = null;
				modCount += size - w;
				size = w;
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * non-static and non-transient fields 
	 */
	private void writeObject(ObjectOutputStream s) throws IOException {
		int expectedModCount = modCount;
		s.defaultWriteObject();
		s.writeInt(size);
		
		for (int i = 0; i < size; i++) {
			s.writeObject(elementData[i]);
		}
		
		if (modCount != expectedModCount) {
			throw new ConcurrentModificationException();
		}
	}
	
	private void readObject(ObjectInputStream s) 
		throws ClassNotFoundException, IOException {
		elementData = EMPTY_ELEMENTDATA;
		
		s.defaultReadObject();
		s.readInt();
		
		if (size > 0) {
			ensureCapacityInternal(size);
			
			Object[] a = elementData;
			for (int i = 0; i < size; i++) {
				a[i] = s.readObject();
			}
		}
	}
	
	public ListIteratorMe<E> listIterator(int index) {
		if (index < 0 || index > size)
			throw new IndexOutOfBoundsException("Index: " + index);
		return new ListItrMe(index);
	}

	public ListIteratorMe<E> listIterator() {
		return new ListItrMe(0);
	}

	public Iterator<E> iterator() {
		return new ItrMe();
	}
	
	private class ItrMe implements Iterator<E> {
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such
        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != size;
        }

        /** 
         * 1.����cursorָ���Ԫ��
         * 2.cursor+1
         * 3.lastRet=cursor�ղŵ�λ��
         * 4.���Ե���remove��set������
         * 5.������hasNext�жϣ���ĩβ�׳��쳣
         */
        @SuppressWarnings("unchecked")
        public E next() {
            checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object[] elementData = ArrayListMe.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return (E) elementData[lastRet = i];
        }

        /**
         * 1.У�飬���û�е���nextֱ�ӵ���remove��lastRet<0����
         * ÿ��remove������lastRet��ʼ��������������У���أ�
         * 2.ɾ���ϸ�ָ���Ԫ��
         * 3.cursor�����ϸ�λ��
         * 4.lastRet��Ϊ��ʼֵ
         * 5.expectedModCountУ׼
         */
        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
            	ArrayListMe.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public void forEachRemaining(Consumer<? super E> consumer) {
            Objects.requireNonNull(consumer);
            final int size = ArrayListMe.this.size;
            int i = cursor;
            if (i >= size) {
                return;
            }
            final Object[] elementData = ArrayListMe.this.elementData;
            if (i >= elementData.length) {
                throw new ConcurrentModificationException();
            }
            while (i != size && modCount == expectedModCount) {
                consumer.accept((E) elementData[i++]);
            }
            // update once at end of iteration to reduce heap write traffic
            cursor = i;
            lastRet = i - 1;
            checkForComodification();
        }

        /**
         * ��ֹ�����޸�
         */
        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * An optimized version of AbstractList.ListItr
     */
    private class ListItrMe extends ItrMe implements ListIteratorMe<E> {
    	ListItrMe(int index) {
            super();
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        /** 
         * 1.cursor-1<br>
         * 2.�����ϸ�Ԫ��<br>
         * 3.lastRet=cursor<br>
         * 4.���Ե���remove��set������
         * 5.������hasPrevious�жϣ��ڵ�һλʱ�׳��쳣
         */
        @SuppressWarnings("unchecked")
        public E previous() {
            checkForComodification();
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            Object[] elementData = ArrayListMe.this.elementData;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i;
            return (E) elementData[lastRet = i];
        }

        /** 
         * 1.�����ȵ���previous��next����<br>
         * 2.�滻�ϸ�λ�õ�Ԫ�أ����Զ���滻(����)<br>
         * 3.lastRet�����ʼ��<br>
         */
        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ArrayListMe.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
        
        /** 
         * 1.��cursor��λ�ò�����Ԫ��<br>
         * 2.cursor+1<br>
         * 3.lastRet��ʼ��<br>
         * 3.expectedModCountУ׼<br>
         */
        public void add(E e) {
            checkForComodification();

            try {
                int i = cursor;
                ArrayListMe.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    public ListMe<E> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubListMe(this, 0, fromIndex, toIndex);
    }

    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex +
                                               ") > toIndex(" + toIndex + ")");
    }

    /**
     * 1.���ص�sublist�����õ�add(e)������AbstractListMe��add(E e)������
     * �����ǵ�����sublist��add(int index, E e)����<br>
     * 2.��sublist���޸ģ���ֱ���޸�ArrayList������ϵ��һ��ģ������ȡ�˶��sublist������������ص�list��sublistһ���޸ģ���
     * �粻���봴���¶���<br>
     * 3.�����ԭArrayList�����޸ģ��ٴβ���sublistʱ���ᵼ��modCount��һ�¶��׳��쳣��
     * ��ΪArrayList����������subList��modCount����У��
     */
    private class SubListMe extends AbstractListMe<E> implements RandomAccess {
        private final AbstractListMe<E> parent;
        private final int parentOffset;
        private final int offset;
        int size;

        SubListMe(AbstractListMe<E> parent,
                int offset, int fromIndex, int toIndex) {
            this.parent = parent;
            this.parentOffset = fromIndex;
            this.offset = offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = ArrayListMe.this.modCount;
        }

        public E set(int index, E e) {
            rangeCheck(index);
            checkForComodification();
            E oldValue = ArrayListMe.this.elementData(offset + index);
            ArrayListMe.this.elementData[offset + index] = e;
            return oldValue;
        }

        public E get(int index) {
            rangeCheck(index);
            checkForComodification();
            return ArrayListMe.this.elementData(offset + index);
        }

        public int size() {
            checkForComodification();
            return this.size;
        }

        public void add(int index, E e) {
            rangeCheckForAdd(index);
            checkForComodification();
            parent.add(parentOffset + index, e);
            this.modCount = parent.modCount;
            this.size++;
        }

        public E remove(int index) {
            rangeCheck(index);
            checkForComodification();
            E result = parent.remove(parentOffset + index);
            this.modCount = parent.modCount;
            this.size--;
            return result;
        }

        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            parent.removeRange(parentOffset + fromIndex,
                               parentOffset + toIndex);
            this.modCount = parent.modCount;
            this.size -= toIndex - fromIndex;
        }

        public boolean addAll(CollectionMe<? extends E> c) {
            return addAll(this.size, c);
        }

        public boolean addAll(int index, CollectionMe<? extends E> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize==0)
                return false;

            checkForComodification();
            parent.addAll(parentOffset + index, c);
            this.modCount = parent.modCount;
            this.size += cSize;
            return true;
        }

        public Iterator<E> iterator() {
            return listIterator();
        }

        public ListIteratorMe<E> listIterator(final int index) {
            checkForComodification();
            rangeCheckForAdd(index);
            final int offset = this.offset;

            return new ListIteratorMe<E>() {
                int cursor = index;
                int lastRet = -1;
                int expectedModCount = ArrayListMe.this.modCount;

                public boolean hasNext() {
                    return cursor != SubListMe.this.size;
                }

                @SuppressWarnings("unchecked")
                public E next() {
                    checkForComodification();
                    int i = cursor;
                    if (i >= SubListMe.this.size)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayListMe.this.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i + 1;
                    return (E) elementData[offset + (lastRet = i)];
                }

                public boolean hasPrevious() {
                    return cursor != 0;
                }

                @SuppressWarnings("unchecked")
                public E previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if (i < 0)
                        throw new NoSuchElementException();
                    Object[] elementData = ArrayListMe.this.elementData;
                    if (offset + i >= elementData.length)
                        throw new ConcurrentModificationException();
                    cursor = i;
                    return (E) elementData[offset + (lastRet = i)];
                }

                @SuppressWarnings("unchecked")
                public void forEachRemaining(Consumer<? super E> consumer) {
                    Objects.requireNonNull(consumer);
                    final int size = SubListMe.this.size;
                    int i = cursor;
                    if (i >= size) {
                        return;
                    }
                    final Object[] elementData = ArrayListMe.this.elementData;
                    if (offset + i >= elementData.length) {
                        throw new ConcurrentModificationException();
                    }
                    while (i != size && modCount == expectedModCount) {
                        consumer.accept((E) elementData[offset + (i++)]);
                    }
                    // update once at end of iteration to reduce heap write traffic
                    lastRet = cursor = i;
                    checkForComodification();
                }

                public int nextIndex() {
                    return cursor;
                }

                public int previousIndex() {
                    return cursor - 1;
                }

                public void remove() {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        SubListMe.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = ArrayListMe.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void set(E e) {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        ArrayListMe.this.set(offset + lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                public void add(E e) {
                    checkForComodification();

                    try {
                        int i = cursor;
                        SubListMe.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = ArrayListMe.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                final void checkForComodification() {
                    if (expectedModCount != ArrayListMe.this.modCount)
                        throw new ConcurrentModificationException();
                }
            };
        }

        public ListMe<E> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubListMe(this, offset, fromIndex, toIndex);
        }

        private void rangeCheck(int index) {
            if (index < 0 || index >= this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private String outOfBoundsMsg(int index) {
            return "Index: "+index+", Size: "+this.size;
        }

        private void checkForComodification() {
            if (ArrayListMe.this.modCount != this.modCount)
                throw new ConcurrentModificationException();
        }

        public Spliterator<E> spliterator() {
            checkForComodification();
            return new ArrayListSpliteratorMe<E>(ArrayListMe.this, offset,
                                               offset + this.size, this.modCount);
        }
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        @SuppressWarnings("unchecked")
        final E[] elementData = (E[]) this.elementData;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            action.accept(elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * Creates a <em><a href="Spliterator.html#binding">late-binding</a></em>
     * and <em>fail-fast</em> {@link Spliterator} over the elements in this
     * list.
     *
     * <p>The {@code Spliterator} reports {@link Spliterator#SIZED},
     * {@link Spliterator#SUBSIZED}, and {@link Spliterator#ORDERED}.
     * Overriding implementations should document the reporting of additional
     * characteristic values.
     *
     * @return a {@code Spliterator} over the elements in this list
     * @since 1.8
     */
    @Override
    public Spliterator<E> spliterator() {
        return new ArrayListSpliteratorMe<>(this, 0, -1, 0);
    }

    /** Index-based split-by-two, lazily initialized Spliterator */
    static final class ArrayListSpliteratorMe<E> implements Spliterator<E> {
        private final ArrayListMe<E> list;
        private int index; // current index, modified on advance/split
        private int fence; // -1 until used; then one past last index
        private int expectedModCount; // initialized when fence set

        /** Create new spliterator covering the given  range */
        ArrayListSpliteratorMe(ArrayListMe<E> list, int origin, int fence,
                             int expectedModCount) {
            this.list = list; // OK if null unless traversed
            this.index = origin;
            this.fence = fence;
            this.expectedModCount = expectedModCount;
        }

        private int getFence() { // initialize fence to size on first use
            int hi; // (a specialized variant appears in method forEach)
            ArrayListMe<E> lst;
            if ((hi = fence) < 0) {
                if ((lst = list) == null)
                    hi = fence = 0;
                else {
                    expectedModCount = lst.modCount;
                    hi = fence = lst.size;
                }
            }
            return hi;
        }

        public ArrayListSpliteratorMe<E> trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid) ? null : // divide range in half unless too small
                new ArrayListSpliteratorMe<E>(list, lo, index = mid,
                                            expectedModCount);
        }

        public boolean tryAdvance(Consumer<? super E> action) {
            if (action == null)
                throw new NullPointerException();
            int hi = getFence(), i = index;
            if (i < hi) {
                index = i + 1;
                @SuppressWarnings("unchecked") E e = (E)list.elementData[i];
                action.accept(e);
                if (list.modCount != expectedModCount)
                    throw new ConcurrentModificationException();
                return true;
            }
            return false;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            int i, hi, mc; // hoist accesses and checks from loop
            ArrayListMe<E> lst; Object[] a;
            if (action == null)
                throw new NullPointerException();
            if ((lst = list) != null && (a = lst.elementData) != null) {
                if ((hi = fence) < 0) {
                    mc = lst.modCount;
                    hi = lst.size;
                }
                else
                    mc = expectedModCount;
                if ((i = index) >= 0 && (index = hi) <= a.length) {
                    for (; i < hi; ++i) {
                        @SuppressWarnings("unchecked") E e = (E) a[i];
                        action.accept(e);
                    }
                    if (lst.modCount == mc)
                        return;
                }
            }
            throw new ConcurrentModificationException();
        }

        public long estimateSize() {
            return (long) (getFence() - index);
        }

        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
        }
    }

    /*@Override
    public boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        // figure out which elements are to be removed
        // any exception thrown from the filter predicate at this stage
        // will leave the collection unmodified
        int removeCount = 0;
        final BitSet removeSet = new BitSet(size);
        final int expectedModCount = modCount;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            @SuppressWarnings("unchecked")
            final E element = (E) elementData[i];
            if (filter.test(element)) {
                removeSet.set(i);
                removeCount++;
            }
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }

        // shift surviving elements left over the spaces left by removed elements
        final boolean anyToRemove = removeCount > 0;
        if (anyToRemove) {
            final int newSize = size - removeCount;
            for (int i=0, j=0; (i < size) && (j < newSize); i++, j++) {
                i = removeSet.nextClearBit(i);
                elementData[j] = elementData[i];
            }
            for (int k=newSize; k < size; k++) {
                elementData[k] = null;  // Let gc do its work
            }
            this.size = newSize;
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            modCount++;
        }

        return anyToRemove;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        final int expectedModCount = modCount;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            elementData[i] = operator.apply((E) elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super E> c) {
        final int expectedModCount = modCount;
        Arrays.sort((E[]) elementData, 0, size, c);
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }*/

}
