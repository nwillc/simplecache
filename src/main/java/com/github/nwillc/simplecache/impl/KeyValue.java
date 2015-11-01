package com.github.nwillc.simplecache.impl;

import com.github.nwillc.simplecache.Cache;

import java.util.AbstractMap;

public class KeyValue<K,V> extends AbstractMap.SimpleEntry<K,V> implements Cache.Entry<K,V> {
	public KeyValue(K key, V value) {
		super(key, value);
	}
}
