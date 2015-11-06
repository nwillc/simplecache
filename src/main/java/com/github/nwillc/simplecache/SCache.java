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
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SCache<K, V> implements Cache<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private final CacheManager cacheManager;
    private final String name;
    private final MutableConfiguration<K, V> configuration;
    private final Optional<CacheLoader<K,V>> loader;
    private final Optional<CacheWriter<? super K,? super V>> writer;

    @SuppressWarnings("unchecked")
    public SCache(CacheManager cacheManager, String name, Configuration<K, V> configuration) {
        this.cacheManager = cacheManager;
        this.name = name;
        this.configuration = new MutableConfiguration<>((MutableConfiguration) configuration);
        loader = Optional.ofNullable( this.configuration.getCacheLoaderFactory() == null ?
                null : this.configuration.getCacheLoaderFactory().create());
        writer = Optional.ofNullable(this.configuration.getCacheWriterFactory() == null ?
                null : this.configuration.getCacheWriterFactory().create());
    }

    @Override
    public V get(K key) {
        V value = map.get(key);
        if (value == null) {
            value = readThrough(key);
        }
        return value;
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        Map<K, V> retMap = new HashMap<>();
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
        writeThrough(key,value);
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
        boolean wasPut = map.putIfAbsent(key, value) == null;
        if (wasPut) {
            writeThrough(key, value);
        }
        return wasPut;
    }

    @Override
    public boolean remove(K key) {
        boolean wasRemoved = map.remove(key) != null;
        if (wasRemoved) {
            removeThrough(key);
        }
        return wasRemoved;
    }

    @Override
    public boolean remove(K key, V oldValue) {
        boolean wasRemoved = map.remove(key, oldValue);
        if (wasRemoved) {
            removeThrough(key);
        }
        return wasRemoved;
    }

    @Override
    public V getAndRemove(K key) {
        V value = get(key);
        remove(key);
        return value;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        boolean wasPut = map.replace(key, oldValue, newValue);
        if (wasPut) {
           writeThrough(key, newValue);
        }
        return wasPut;
    }

    @Override
    public boolean replace(K key, V value) {
        boolean wasPut = map.replace(key, value) != null;
        if (wasPut) {
            writeThrough(key,value);
        }
        return wasPut;
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
        if (clazz.isInstance(configuration)) {
            return clazz.cast(configuration);
        }

        throw new IllegalArgumentException();
    }

    @Override
    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object... arguments) throws EntryProcessorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CacheManager getCacheManager() {
        return cacheManager;
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
        if (clazz.isAssignableFrom(this.getClass())) {
            return clazz.cast(this);
        }

        throw new IllegalArgumentException();
    }

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return stream().collect(Collectors.toList()).iterator();
    }

    public Stream<Entry<K, V>> stream() {
        return map.entrySet().stream().map(e -> (Entry) new SEntry<>(e));
    }

    private V readThrough(K key) {
        if (!(loader.isPresent() && configuration.isReadThrough())) {
            return null;
        }

        V value = loader.get().load(key);
        if (value != null) {
            map.put(key, value);
        }

        return value;
    }

    private void writeThrough(K key, V value) {
        if (!(writer.isPresent() && configuration.isWriteThrough())) {
            return;
        }

        writer.get().write(new SEntry<>(key, value));
    }

    private void removeThrough(K key) {
        if (!(writer.isPresent() && configuration.isWriteThrough())) {
            return;
        }

        writer.get().delete(key);
    }
}
