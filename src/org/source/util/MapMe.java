package org.source.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.function.BiConsumer;

public interface MapMe<K, V> {
	int size();

	boolean isEmpty();

	boolean containsKey(Object key);

	boolean containsValue(Object value);

	V get(Object key);

	V put(K key, V value);

	V remove(Object key);

	void putAll(MapMe<? extends K, ? extends V> m);

	void clear();

	SetMe<K> keySet();

	CollectionMe<V> values();

	SetMe<MapMe.EntryMe<K, V>> entrySet();

	interface EntryMe<K, V> {
		K getKey();

		V getValue();

		V setValue(V value);

		boolean equals(Object o);

		int hashCode();

		/**
		 * 按key排序
		 */
		public static <K extends Comparable<? super K>, V> Comparator<MapMe.EntryMe<K, V>> comparingByKey() {
			return (Comparator<MapMe.EntryMe<K, V>> & Serializable) (c1, c2) -> c1.getKey().compareTo(c2.getKey());
		}

		/**
		 * 按value排序
		 */
		public static <K, V extends Comparable<? super V>> Comparator<MapMe.EntryMe<K, V>> comparingByValue() {
			return (Comparator<MapMe.EntryMe<K, V>> & Serializable) (c1, c2) -> c1.getValue().compareTo(c2.getValue());
		}

		/**
		 * 用给定的Comparator按key排序
		 */
		public static <K, V> Comparator<MapMe.EntryMe<K, V>> comparingByKey(Comparator<? super K> cmp) {
			Objects.requireNonNull(cmp);
			return (Comparator<MapMe.EntryMe<K, V>> & Serializable) (c1, c2) -> cmp.compare(c1.getKey(), c2.getKey());
		}

		/**
		 * 用给定的Comparator按value排序
		 */
		public static <K, V> Comparator<MapMe.EntryMe<K, V>> comparingByValue(Comparator<? super V> cmp) {
			Objects.requireNonNull(cmp);
			return (Comparator<MapMe.EntryMe<K, V>> & Serializable) (c1, c2) -> cmp.compare(c1.getValue(),
					c2.getValue());
		}
	}

	boolean equals(Object o);

	int hashCode();

	default V getOrDefault(Object key, V defaultValue) {
		V v;
		return (((v = get(key)) != null) || containsKey(key)) ? v : defaultValue;
	}

	default void forEach(BiConsumer<? super K, ? super V> action) {
		Objects.requireNonNull(action);
		for (MapMe.EntryMe<K, V> entry : entrySet()) {
			K k;
			V v;
			try {
				k = entry.getKey();
				v = entry.getValue();
			} catch (IllegalStateException ise) {
				// this usually means the entry is no longer in the map.
				throw new ConcurrentModificationException(ise);
			}
			action.accept(k, v);
		}
	}
}
