package com.github.nwillc.simplecache.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Cache<K,V> implements com.github.nwillc.simplecache.Cache<K,V> {
	private Map<K,V> map = new HashMap<>();

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public Stream<Entry<K, V>> stream() {
		return map.entrySet().stream().map(e -> new KeyValue<>(e.getKey(), e.getValue()));
	}

	@Override
	public void put(K key, V value) {
	   map.put(key, value);
	}

	@Override
	public boolean putIfAbsent(K key, V value) {
		if (containsKey(key)) {
			return false;
		}

		put(key, value);
		return true;
	}

	@Override
	public boolean remove(K key) {
		if (!containsKey(key)) {
			return false;
		}
		map.remove(key);
		return true;
	}

	@Override
	public V get(K key) {
		return map.get(key);
	}
}
