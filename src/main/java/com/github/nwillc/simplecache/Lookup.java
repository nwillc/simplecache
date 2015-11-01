package com.github.nwillc.simplecache;

@FunctionalInterface
public interface Lookup<K,V> {
	V get(K key);
}
