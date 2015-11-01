package com.github.nwillc.simplecache;

import java.util.stream.Stream;

/**
 * A JSR 107 inspired simple caching interface.
 * @param <K>
 * @param <V>
 */
public interface Cache<K,V> extends Lookup<K,V> {
	void clear();
	boolean containsKey(K key);
	Stream<Entry<K,V>> stream();
	void put(K key, V value);
	boolean putIfAbsent(K key, V value);
	boolean remove(K key);

	interface Entry<K, V> {
		K getKey();
		V getValue();
	}
}
