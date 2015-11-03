/*
 * Copyright (c) 2015, nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.simplecache;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import java.net.URI;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager implements javax.cache.CacheManager {
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();
    private final Properties properties = new Properties();

    @Override
    public javax.cache.spi.CachingProvider getCachingProvider() {
        return new CachingProvider();
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
    public <K, V, C extends Configuration<K, V>> javax.cache.Cache<K, V> createCache(String cacheName, C configuration) throws IllegalArgumentException {
        Cache<K,V> cache = new com.github.nwillc.simplecache.Cache<>(this, cacheName, configuration);
        cacheMap.put(cacheName, cache);
        return cache;
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName, Class<K> keyType, Class<V> valueType) {
        return getCache(cacheName);
    }

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) {
        return cacheMap.get(cacheName);
    }

    @Override
    public Iterable<String> getCacheNames() {
        return cacheMap.keySet();
    }

    @Override
    public void destroyCache(String cacheName) {
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

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (!clazz.equals(CacheManager.class)) {
            throw new IllegalArgumentException();
        }

        return (T)this;
    }
}
