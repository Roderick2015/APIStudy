package org.source.util;

import java.io.Serializable;
import java.util.Iterator;

public abstract class AbstractMapMe<K, V> implements MapMe<K, V> {
	protected AbstractMapMe() {
    }
	
	@Override
	public abstract SetMe<EntryMe<K, V>> entrySet();
	
	@Override
	public int size() {
		return entrySet().size();
	}
	
	@Override
	public boolean isEmpty() {
		return size() == 0;
	}
	
	@Override
	public boolean containsValue(Object value) {
		Iterator<EntryMe<K, V>> i = entrySet().iterator();
		if (value == null) {
			while (i.hasNext()) {
				EntryMe<K, V> e = i.next();
				if (e.getValue() == null)
					return true;
			}
		} else {
			while (i.hasNext()) {
				EntryMe<K, V> e = i.next();
				if (value.equals(e.getValue()))
					return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containsKey(Object key) {
		Iterator<EntryMe<K, V>> i = entrySet().iterator();
		if (key == null) {
			while (i.hasNext()) {
				EntryMe<K, V> e = i.next();
				if (e.getKey() == null)
					return true;
			}
		} else {
			while (i.hasNext()) {
				EntryMe<K, V> e = i.next();
				if (key.equals(e.getKey()))
					return true;
			}
		}
		return false;
	}
	
	@Override
	public V get(Object key) {
		Iterator<EntryMe<K, V>> i = entrySet().iterator();
		if (key == null) {
			while (i.hasNext()) {
				EntryMe<K, V> e = i.next();
				if (e.getKey() == null)
					return e.getValue();
			}
		} else {
			while (i.hasNext()) {
				EntryMe<K, V> e = i.next();
				if (key.equals(e.getKey()))
					return e.getValue();
			}
		}
		return null;
	}
	
	@Override
	public V put(K key, V value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public V remove(Object key) {
		Iterator<EntryMe<K, V>> i = entrySet().iterator();
		EntryMe<K, V> correctEntry = null;
		if (key == null) {
			while (correctEntry == null && i.hasNext()) {
				EntryMe<K, V> e = i.next();
				if (e.getKey() == null)
					correctEntry = e;
			}
		} else {
			while (correctEntry == null && i.hasNext()) {
				EntryMe<K, V> e = i.next();
				if (key.equals(e.getKey()))
					correctEntry = e;
			}
		}
		
		V oldValue = null;
		if (correctEntry != null) {
			oldValue = correctEntry.getValue();
			i.remove();
		}
		return oldValue; //����ɾ����value��δ�ҵ��򷵻�null
	}
	
	@Override
	public void putAll(MapMe<? extends K, ? extends V> m) {
		for (EntryMe<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}
	
	@Override
	public void clear() {
		entrySet().clear();
	}
	
	transient volatile SetMe<K> 	   keySet; //map��key���ϣ�key�����ظ������Բ�����Set
	transient volatile CollectionMe<V> values; //map��value����
	
	@Override
	public SetMe<K> keySet() {
		if (keySet == null) {
			keySet = new AbstractSetMe<K>() { //����������ε��ӿ��б���ʵ�ֵķ��������Ǹ���ĳ��󷽷��Ա���ʵ��
				@Override
				public Iterator<K> iterator() { 
					return new Iterator<K>() { //����map��keyֵ���ϵĵ�����
						//�ü��ϵĵ�����remove��������ʵ�֣��������쳣
						private Iterator<EntryMe<K, V>> i = entrySet().iterator(); 
						
						@Override
						public boolean hasNext() {
							return i.hasNext();
						}

						@Override
						public K next() {
							return i.next().getKey();
						}
						
						@Override
						public void remove() {
							i.remove();
						}
					};
				}

				@Override
				public int size() {
					return AbstractMapMe.this.size();
				}

				@Override
				public boolean isEmpty() {
					return AbstractMapMe.this.isEmpty();
				}

				@Override
				public void clear() {
					AbstractMapMe.this.clear();
				}
				
				@Override
				public boolean contains(Object k) {
					return AbstractMapMe.this.containsKey(k);
				}
			};
		}
		return keySet;
	}
	
	@Override
	public CollectionMe<V> values() {
		if (values == null) {
			values = new AbstractCollectionMe<V>() {
				@Override
				public Iterator<V> iterator() {
					return new Iterator<V>() { //����map��valueֵ���ϵĵ�����
						private Iterator<EntryMe<K, V>> i = entrySet().iterator();

						@Override
						public boolean hasNext() {
							return i.hasNext();
						}

						@Override
						public V next() {
							return i.next().getValue();
						}

						@Override
						public void remove() {
							i.remove();
						}
					};
				}

				@Override
				public int size() {
					return AbstractMapMe.this.size();
				}

				@Override
				public boolean isEmpty() {
					return AbstractMapMe.this.isEmpty();
				}

				@Override
				public void clear() {
					AbstractMapMe.this.clear();
				}

				@Override
				public boolean contains(Object v) {
					return AbstractMapMe.this.containsValue(v);
				}
			};
		}
		return values;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		
		if (!(o instanceof MapMe))
			return false;
		
		MapMe<?, ?> m = (MapMe<?, ?>) o;
		if (m.size() != size())
			return false;
		
		try {
			Iterator<EntryMe<K, V>> i = entrySet().iterator();
			while (i.hasNext()) {
				EntryMe<K, V> e = i.next();
				K key = e.getKey();
				V value = e.getValue();
				if (value == null) {
					if (!(m.get(key) == null && m.containsKey(key)))
						return false;
				} else {
					if (!value.equals(m.get(key)))
						return false;
				}
			}
		} catch (ClassCastException unused) {
			return false;
		} catch (NullPointerException unused) {
			return false;
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int h = 0;
		Iterator<EntryMe<K, V>> i = entrySet().iterator();
		while (i.hasNext())
			h += i.next().hashCode();
		return h;
	}
	
	@Override
	public String toString() {
		Iterator<EntryMe<K, V>> i = entrySet().iterator();
		if (!i.hasNext())
			return "{}";
		
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (;;) {
			EntryMe<K, V> e = i.next();
			K key = e.getKey();
			V value = e.getValue();
			sb.append(key == this ? "(this Map)" : key);
			sb.append('=');
			sb.append(value == this ? "(this Map)" : value); //ʲô����»����this?
			if (!i.hasNext())
				return sb.append('}').toString();
			sb.append(',').append(' ');
		}
	}
	
	/**
	 * keys �� values���ᱻ��¡��
	 */
	protected Object clone() throws CloneNotSupportedException {
		AbstractMapMe<?, ?> result = (AbstractMapMe<?, ?>) super.clone();
		result.keySet = null;
		result.values = null;
		return result;
	}
	
	/**
	 * ������key��value�ȶ�
	 */
	private static boolean eq(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}
	
	/**
	 * �洢����key��value
	 */
	public static class SimpleEntryMe<K, V>
		implements EntryMe<K, V>, Serializable {
		private static final long serialVersionUID = -4517525601307819457L;
		
		private final K key;
		private V value;
		
		public SimpleEntryMe(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public SimpleEntryMe(EntryMe<? extends K, ? extends V> entry) {
			this.key = entry.getKey();
			this.value = entry.getValue();
		}
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof MapMe.EntryMe)) //���ж�����һ��
				return false;
			MapMe.EntryMe<?, ?> e = (MapMe.EntryMe<?, ?>) o; 
			return eq(key, e.getKey()) && eq(value, e.getValue());
		}
		
		@Override
		public int hashCode() {
			return (key == null ? 0 : key.hashCode()) ^
				   (value == null ? 0 : value.hashCode()); //��������������������
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}
	}
	
	public static class SimpleImmutableEntryMe<K, V>
		implements EntryMe<K, V>, Serializable {
		private static final long serialVersionUID = 142620153596564863L;

		 private final K key;
	        private final V value;
		public SimpleImmutableEntryMe(K key, V value) {
            this.key   = key;
            this.value = value;
        }
		
		public SimpleImmutableEntryMe(EntryMe<? extends K, ? extends V> entry) {
            this.key   = entry.getKey();
            this.value = entry.getValue();
        }
		
		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		/**
		 * ���ɱ��ʵ�֣����Բ����滻ֵ���̰߳�ȫ
		 */
		@Override
		public V setValue(V value) {
			 throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof MapMe.EntryMe)) //���ж�����һ��
				return false;
			MapMe.EntryMe<?, ?> e = (MapMe.EntryMe<?, ?>) o; 
			return eq(key, e.getKey()) && eq(value, e.getValue());
		}
		
		@Override
		public int hashCode() {
			return (key == null ? 0 : key.hashCode()) ^
				   (value == null ? 0 : value.hashCode()); //��������������������
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}
	}
}
