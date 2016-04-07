package org.source.util;

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
		return oldValue; //返回删除的value，未找到则返回null
	}
	
	public void putAll(MapMe<? extends K, ? extends V> m) {
		for (EntryMe<? extends K, ? extends V> e : m.entrySet())
			put(e.getKey(), e.getValue());
	}
	
	
}
