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


import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cache<K, V> implements javax.cache.Cache<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private final javax.cache.CacheManager cacheManager;
    private final String name;

    public Cache(CacheManager cacheManager, String name) {
        this.cacheManager = cacheManager;
        this.name = name;
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        Map<K,V> retMap = new HashMap<>();
        keys.stream().forEach(k -> map.computeIfPresent(k, retMap::put));
        return retMap;
    }

    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    @Override
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(K key, V value) {
        map.put(key, value);
    }

    @Override
    public V getAndPut(K key, V value) {
        V old = get(key);
        put(key, value);
        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        map.entrySet().stream().forEach(e -> put(e.getKey(), e.getValue()));
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        return map.putIfAbsent(key, value) == null;
    }

    @Override
    public boolean remove(K key) {
        return map.remove(key) != null;
    }

    @Override
    public boolean remove(K key, V oldValue) {
        return remove(key, oldValue);
    }

    @Override
    public V getAndRemove(K key) {
        V value = get(key);
        remove(key);
        return value;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public boolean replace(K key, V value) {
        return map.replace(key, value) != null;
    }

    @Override
    public V getAndReplace(K key, V value) {
        return map.replace(key, value);
    }

    @Override
    public void removeAll(Set<? extends K> keys) {
      keys.stream().forEach(this::remove);
    }

    @Override
    public void removeAll() {
        removeAll(map.keySet());
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
        return null;
    }

    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException {
        return null;
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public javax.cache.CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (!this.getClass().equals(clazz)) {
            throw new IllegalArgumentException();
        }
        return (T)this;
    }

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {

    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {

    }

    @Override
    public Iterator<Entry<K,V>> iterator() {
        return stream().collect(Collectors.toList()).iterator();
    }

    public Stream<Entry<K,V>> stream() {
        return map.entrySet().stream().map(e -> (Entry) new com.github.nwillc.simplecache.Entry<>(e));
    }
}
