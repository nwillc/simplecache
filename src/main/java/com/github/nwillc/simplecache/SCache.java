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


import com.github.nwillc.simplecache.managment.SCacheStatisticsMXBean;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SCache<K, V> implements Cache<K, V> {
    private final Map<K, SExpiryData> expiry = new ConcurrentHashMap<>();
    private final Map<K, V> data = new ConcurrentHashMap<>();
    private final CacheManager cacheManager;
    private final String name;
    private final MutableConfiguration<K, V> configuration;
    private final Optional<CacheLoader<K, V>> loader;
    private final Optional<CacheWriter<? super K, ? super V>> writer;
    private final Factory<SExpiryData> expiryDataFactory;
    private final Optional<SCacheStatisticsMXBean> statistics;
    private Supplier<Long> clock = System::nanoTime;

    @SuppressWarnings("unchecked")
    public SCache(CacheManager cacheManager, String name, Configuration<K, V> configuration) {
        this.cacheManager = cacheManager;
        this.name = name;
        this.configuration = new MutableConfiguration<>((MutableConfiguration) configuration);
        loader = Optional.ofNullable(this.configuration.getCacheLoaderFactory() == null ?
                null : this.configuration.getCacheLoaderFactory().create());
        writer = Optional.ofNullable(this.configuration.getCacheWriterFactory() == null ?
                null : this.configuration.getCacheWriterFactory().create());
        expiryDataFactory = (Factory<SExpiryData>) () ->
                new SExpiryData(clock, (ExpiryPolicy) ((MutableConfiguration) configuration).getExpiryPolicyFactory().create());
        statistics = ((MutableConfiguration) configuration).isStatisticsEnabled() ?
                Optional.of(new SCacheStatisticsMXBean()) : Optional.<SCacheStatisticsMXBean>empty();
    }

    @Override
    public V get(K key) {
        statistics.ifPresent(SCacheStatisticsMXBean::get);
        expiryCheck(key);
        V value = data.get(key);
        if (value == null) {
            statistics.ifPresent(SCacheStatisticsMXBean::miss);
            value = readThrough(key);
        } else {
            statistics.ifPresent(SCacheStatisticsMXBean::hit);
        }
        expiry.compute(key, (k, v) -> v == null ? expiryDataFactory.create() : v.access());
        return value;
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys) {
        Map<K, V> retMap = new HashMap<>();
        keys.stream().forEach(k -> data.computeIfPresent(k, retMap::put));
        return retMap;
    }

    @Override
    public boolean containsKey(K key) {
        return data.containsKey(key);
    }

    @Override
    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        Optional<CompletionListener> listener = Optional.ofNullable(completionListener);

        try { // Should be in another thread
            for (K key : keys) {
                if (containsKey(key) && replaceExistingValues) {
                    expire(key);
                }
                get(key);
            }
        } catch (Exception e) {
            listener.ifPresent(l -> l.onException(e));
            return;
        }

        listener.ifPresent(CompletionListener::onCompletion);
    }

    @Override
    public void put(K key, V value) {
        getAndPut(key, value);
    }

    @Override
    public V getAndPut(K key, V value) {
        statistics.ifPresent(SCacheStatisticsMXBean::get);
        statistics.ifPresent(SCacheStatisticsMXBean::put);
        expiryCheck(key);
        V old = data.put(key, value);
        writeThrough(key, value);
        expiry.compute(key, (k, v) -> v == null ? expiryDataFactory.create() : v.update());
        return old;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        map.entrySet().stream().forEach(e -> put(e.getKey(), e.getValue()));
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        boolean wasPut = data.putIfAbsent(key, value) == null;
        if (wasPut) {
            statistics.ifPresent(SCacheStatisticsMXBean::put);
            writeThrough(key, value);
            expiry.put(key, expiryDataFactory.create());
        }
        return wasPut;
    }

    @Override
    public boolean remove(K key) {
        boolean wasRemoved = data.remove(key) != null;
        if (wasRemoved) {
            statistics.ifPresent(SCacheStatisticsMXBean::remove);
            removeThrough(key);
            expiry.remove(key);
        }
        return wasRemoved;
    }

    @Override
    public boolean remove(K key, V oldValue) {
        boolean wasRemoved = data.remove(key, oldValue);
        if (wasRemoved) {
            statistics.ifPresent(SCacheStatisticsMXBean::remove);
            removeThrough(key);
            expiry.remove(key);
        }
        return wasRemoved;
    }

    @Override
    public V getAndRemove(K key) {
        V value = get(key);
        statistics.ifPresent(SCacheStatisticsMXBean::remove);
        remove(key);
        return value;
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        boolean wasPut = data.replace(key, oldValue, newValue);
        if (wasPut) {
            writeThrough(key, newValue);
            expiry.get(key).update();
        }
        return wasPut;
    }

    @Override
    public boolean replace(K key, V value) {
        boolean wasPut = data.replace(key, value) != null;
        if (wasPut) {
            writeThrough(key, value);
            expiry.get(key).update();
        }
        return wasPut;
    }

    @Override
    public V getAndReplace(K key, V value) {
        expiryCheck(key);
        expiry.computeIfPresent(key, (k, v) -> {
            statistics.ifPresent(SCacheStatisticsMXBean::get);
            return v.update();
        });
        return data.replace(key, value);
    }

    @Override
    public void removeAll(Set<? extends K> keys) {
        keys.stream().forEach(this::remove);
    }

    @Override
    public void removeAll() {
        removeAll(data.keySet());
    }

    @Override
    public void clear() {
        data.clear();
        expiry.clear();
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
        return data.entrySet().stream().map(e -> (Entry) new SEntry<>(e));
    }

    private V readThrough(K key) {
        if (!(loader.isPresent() && configuration.isReadThrough())) {
            return null;
        }
        statistics.ifPresent(SCacheStatisticsMXBean::readThrough);
        V value = loader.get().load(key);
        if (value != null) {
            data.put(key, value);
        }

        return value;
    }

    private void writeThrough(K key, V value) {
        if (!(writer.isPresent() && configuration.isWriteThrough())) {
            return;
        }
        statistics.ifPresent(SCacheStatisticsMXBean::writeThrough);
        writer.get().write(new SEntry<>(key, value));
    }

    private void removeThrough(K key) {
        if (!(writer.isPresent() && configuration.isWriteThrough())) {
            return;
        }
        statistics.ifPresent(SCacheStatisticsMXBean::removeThrough);
        writer.get().delete(key);
    }

    private void expiryCheck(K key) {
        SExpiryData expiryData = expiry.get(key);
        if (expiryData != null && expiryData.expired()) {
            expire(key);
        }
    }

    private void expire(K key) {
        statistics.ifPresent(SCacheStatisticsMXBean::eviction);
        expiry.remove(key);
        data.remove(key);
    }

    void setClock(Supplier<Long> clock) {
        this.clock = clock;
    }

    public SCacheStatisticsMXBean getStatistics() {
        return statistics.orElse(null);
    }
}
