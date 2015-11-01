package com.github.nwillc.simplecache.impl;

import com.github.nwillc.simplecache.Cache;

public class KeyValue<K,V> implements Cache.Entry<K,V> {
	private K key;
	private V value;

	public KeyValue() {
		this(null,null);
	}

	public KeyValue(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public void setValue(V value) {
		this.value = value;
	}
}
