package com.github.nwillc.simplecache;

public interface CacheManager {
	<K,V> Cache<K,V> createCache(String cacheName, Configuration<K,V> configuration);
	void destroyCache(String cacheName);
	<K,V> Cache<K,V> getCache(String cacheName);
}
