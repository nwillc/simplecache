/*
 * Copyright 2019 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.simplecache;

import com.github.nwillc.simplecache.spi.SCachingProvider;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class SCacheManager implements CacheManager {
	private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();
	private final Properties properties;
	private final SCachingProvider cachingProvider;
	private final AtomicBoolean closed = new AtomicBoolean(true);

	public SCacheManager(SCachingProvider cachingProvider, Properties properties) {
		this.cachingProvider = cachingProvider;
		this.properties = properties == null ? new Properties() : properties;
		closed.set(false);
	}

	@Override
	public CachingProvider getCachingProvider() {
		return cachingProvider;
	}

	@Override
	public URI getURI() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ClassLoader getClassLoader() {
		return ClassLoader.getSystemClassLoader();
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	@Override
	public <K, V, C extends Configuration<K, V>> Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException {
		exceptionIfClosed();
		Cache<K, V> cache = new SCache<>(this, cacheName, configuration);
		cacheMap.put(cacheName, cache);
		return cache;
	}

	@Override
	public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
		return getCache(cacheName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Cache<K, V> getCache(String cacheName) {
		exceptionIfClosed();
		return cacheMap.get(cacheName);
	}

	@Override
	public Iterable<String> getCacheNames() {
		exceptionIfClosed();
		return cacheMap.keySet();
	}

	@Override
	public void destroyCache(String cacheName) {
		exceptionIfClosed();
		cacheMap.remove(cacheName);
	}

	@Override
	public void enableManagement(String cacheName, boolean enabled) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void enableStatistics(String cacheName, boolean enabled) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void close() {
		if (closed.compareAndSet(false, true)) {
			cacheMap.values().forEach(Cache::close);
			cacheMap.clear();
		}
	}

	@Override
	public boolean isClosed() {
		return closed.get();
	}

	@Override
	public <T> T unwrap(Class<T> clazz) {
		if (clazz.isAssignableFrom(this.getClass())) {
			return clazz.cast(this);
		}

		throw new IllegalArgumentException();
	}

	private void exceptionIfClosed() {
		if (closed.get()) {
			throw new IllegalStateException("CacheManager is closed.");
		}
	}
}
