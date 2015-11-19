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

import com.github.nwillc.simplecache.integration.SCacheLoader;
import com.github.nwillc.simplecache.spi.SCachingProvider;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CompletionListener;
import javax.cache.integration.CompletionListenerFuture;
import javax.cache.spi.CachingProvider;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.fail;

@SuppressWarnings("unchecked")
public class SCacheReadThroughTest {
    private static final String NAME = "hoard";
    private final Map<Long, String> backingStore = new HashMap<>();
    private final Factory<CacheLoader<Long, String>> factory =
            (Factory<CacheLoader<Long, String>>) () -> new SCacheLoader<>(backingStore::get);
    private Cache<Long, String> cache;
    private CacheManager cacheManager;

    @Before
    public void setUp() throws Exception {
        CachingProvider cachingProvider = Caching.getCachingProvider(SCachingProvider.class.getCanonicalName());
        cacheManager = cachingProvider.getCacheManager();
        MutableConfiguration<Long, String> configuration = new MutableConfiguration<>();
        configuration.setReadThrough(true);
        configuration.setCacheLoaderFactory(factory);
        cache = cacheManager.createCache(NAME, configuration);
    }

    @Test
    public void shouldIsReadThrough() throws Exception {
        assertThat(cache.getConfiguration(CompleteConfiguration.class).isReadThrough()).isTrue();
    }

    @Test
    public void shouldReadThrough() throws Exception {
        assertThat(cache).isEmpty();
        backingStore.put(0L, "0");
        String value = cache.get(0L);
        assertThat(value).isEqualTo("0");
    }

    @Test
    public void testReadThroughFails() throws Exception {
        assertThat(cache).isEmpty();
        assertThat(backingStore).isEmpty();
        String value = cache.get(0L);
        assertThat(value).isNull();
    }

    @Test
    public void shouldNotReadThrough() throws Exception {
        backingStore.put(0L, "0");
        cache.put(0L, "bar");
        String value = cache.get(0L);
        assertThat(value).isEqualTo("bar");
    }

    @Test
    public void shouldReadThroughShutOff() throws Exception {
        backingStore.put(0L, "0");
        assertThat(cache.get(0L)).isEqualTo("0");
        cache.getConfiguration(MutableConfiguration.class).setReadThrough(false);
        backingStore.put(1L, "1");
        assertThat(cache.get(1L)).isNull();
    }

    @Test
    public void shouldHandleNoFunctor() throws Exception {
        MutableConfiguration<Long, String> mutableConfiguration = cache.getConfiguration(MutableConfiguration.class);
        mutableConfiguration.setCacheLoaderFactory(null);
        cache = cacheManager.createCache(NAME + "broken", mutableConfiguration);
        backingStore.put(1L, "1");
        assertThat(cache.get(1L)).isNull();
    }

    @Test
    public void testLoadAllNoReplace() throws Exception {
        backingStore.put(0L, "0");
        cache.put(0L, "old");
        backingStore.put(1L, "1");
        Set<Long> keys = new HashSet<>();
        keys.add(0L);
        keys.add(1L);
        assertThat(cache.containsKey(0L)).isTrue();
        assertThat(cache.containsKey(1L)).isFalse();
        final Semaphore completed = new Semaphore(1);
        completed.acquire();
        cache.loadAll(keys, false, new CompletionListener() {
            @Override
            public void onCompletion() {
                completed.release();
            }

            @Override
            public void onException(Exception e) {
                fail("Load failed");
            }
        });
        if (!completed.tryAcquire(1, 5, TimeUnit.SECONDS)) {
            fail("never completed");
        }
        assertThat(cache.get(0L)).isEqualTo("old");
        assertThat(cache.containsKey(1L)).isTrue();
    }

    @Test
    public void testLoadAllNoReplaceNoListener() throws Exception {
        backingStore.put(0L, "0");
        Set<Long> keys = new HashSet<>();
        keys.add(0L);
        assertThat(cache.containsKey(0L)).isFalse();
        cache.loadAll(keys, false, null);
        assertThat(cache.containsKey(0L)).isTrue();
    }

    @Test
    public void testLoadAllReplace() throws Exception {
        backingStore.put(0L, "0");
        cache.put(0L, "bad");
        backingStore.put(1L, "1");
        Set<Long> keys = new HashSet<>();
        keys.add(0L);
        keys.add(1L);
        assertThat(cache.containsKey(0L)).isTrue();
        assertThat(cache.containsKey(1L)).isFalse();
        CompletionListenerFuture done = new CompletionListenerFuture();
        cache.loadAll(keys, true, done);
        try {
            done.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            fail(e.toString());
        }
        assertThat(cache.get(0L)).isEqualTo("0");
        assertThat(cache.containsKey(1L)).isTrue();
    }

    @Test
    public void testListenForException() throws Exception {
        MutableConfiguration<Long, String> configuration = new MutableConfiguration<>();
        configuration.setReadThrough(true);
        Factory<CacheLoader<Long, String>> failingLoaderFactory = () -> new SCacheLoader<>(k -> {
            throw new RuntimeException("pop!");
        });
        configuration.setCacheLoaderFactory(failingLoaderFactory);
        cache = cacheManager.createCache(NAME + "exception", configuration);
        Set<Long> keys = new HashSet<>();
        keys.add(0L);
        CompletionListenerFuture done = new CompletionListenerFuture();
        cache.loadAll(keys, true, done);
        assertThatThrownBy(() -> done.get(2, TimeUnit.SECONDS)).isInstanceOf(ExecutionException.class);
    }
}
