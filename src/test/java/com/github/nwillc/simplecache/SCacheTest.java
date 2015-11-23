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

import com.github.nwillc.simplecache.spi.SCachingProvider;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.*;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.data.MapEntry.entry;

@SuppressWarnings("unchecked")
public class SCacheTest  {
    private static final String NAME = "hoard";
    private Cache<Long, String> cache;
    private CacheManager cacheManager;

    @Before
    public void setUp() throws Exception {
        CachingProvider cachingProvider = Caching.getCachingProvider(SCachingProvider.class.getCanonicalName());
        cacheManager = cachingProvider.getCacheManager();
        cache = cacheManager.createCache(NAME, new MutableConfiguration<>());
    }

    @Test
    public void testGet() throws Exception {
        cache.put(0L, "foo");
        assertThat(cache.get(0L)).isEqualTo("foo");
    }

    @Test
    public void testGetAll() throws Exception {
        cache.put(0L, "zero");
        cache.put(1L, "one");
        cache.put(2L, "two");
        Set<Long> keys = new HashSet<>();
        keys.add(0L);
        keys.add(1L);
        assertThat(cache.getAll(keys)).containsExactly(entry(0L, "zero"), entry(1L, "one"));
        assertThat(cache).hasSize(3);
    }

    @Test
    public void testKnowOrigins() throws Exception {
        assertThat(cache.getCacheManager()).isEqualTo(cacheManager);
        assertThat(cache.getName()).isEqualTo(NAME);
    }

    @Test
    public void testUnwrap() throws Exception {
        assertThat(cache.unwrap(SCache.class)).isEqualTo(cache);
    }

    @Test
    public void testUnwrapFail() throws Exception {
        assertThatThrownBy(() -> cache.unwrap(String.class)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testDeregisterListener() throws Exception {
        assertThatThrownBy(() -> cache.invokeAll(null, null)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testRegisterListener() throws Exception {
        Factory<CacheEntryListener<Long,String>> listenerFactory =
                () -> (CacheEntryCreatedListener<Long, String>) cacheEntryEvents -> {};
        CacheEntryListenerConfiguration<Long, String> cacheEntryListenerConfiguration =
                new MutableCacheEntryListenerConfiguration<>(listenerFactory, null, false, false);

        cache.registerCacheEntryListener(cacheEntryListenerConfiguration);
    }

    @Test
    public void testDeregisterCacheEntryListener() throws Exception {
        assertThatThrownBy(() -> cache.deregisterCacheEntryListener(null)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void shouldClear() throws Exception {
        cache.put(0L, "foo");
        assertThat(cache).hasSize(1);
        cache.clear();
        assertThat(cache).hasSize(0);
    }

    @Test
    public void testInvokeAll() throws Exception {
        assertThatThrownBy(() -> cache.invokeAll(null, null)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testInvoke() throws Exception {
        assertThatThrownBy(() -> cache.invoke(null, null)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void shouldContainsKey() throws Exception {
        assertThat(cache.containsKey(0L)).isFalse();
        cache.put(0L, "foo");
        assertThat(cache.containsKey(0L)).isTrue();
    }

    @Test
    public void shouldStream() throws Exception {
        assertThat(cache).hasSize(0);
        cache.put(0L, "foo");
        assertThat(cache).hasSize(1);
        assertThat(cache.containsKey(0L)).isTrue();
    }

    @Test
    public void testRemoveAll() throws Exception {
        cache.put(0L, "zero");
        cache.put(1L, "one");
        assertThat(cache).hasSize(2);
        cache.removeAll();
        assertThat(cache).isEmpty();
    }

    @Test
    public void testRemoveAllSet() throws Exception {
        cache.put(0L, "zero");
        cache.put(1L, "one");
        assertThat(cache).hasSize(2);

        Set<Long> keys = new HashSet<>();
        keys.add(0L);
        cache.removeAll(keys);
        assertThat(cache).containsExactly(new SEntry<>(1L, "one"));
    }

    @Test
    public void shouldPutIfAbsent() throws Exception {
        cache.put(0L, "foo");
        assertThat(cache.get(0L)).isEqualTo("foo");
        assertThat(cache.putIfAbsent(0L, "bar")).isFalse();
        assertThat(cache.putIfAbsent(1L, "bar")).isTrue();
        assertThat(cache.get(0L)).isEqualTo("foo");
        assertThat(cache.get(1L)).isEqualTo("bar");
    }

    @Test
    public void shouldRemove() throws Exception {
        cache.put(0L, "foo");
        assertThat(cache.containsKey(0L)).isTrue();
        assertThat(cache.remove(0L)).isTrue();
        assertThat(cache.containsKey(0L)).isFalse();

        assertThat(cache.remove(1L)).isFalse();
    }

    @Test
    public void testGetAndReplace() throws Exception {
        cache.put(0L, "foo");
        assertThat(cache.getAndReplace(0L, "zero")).isEqualTo("foo");
        assertThat(cache.get(0L)).isEqualTo("zero");
    }

    @Test
    public void testGetAndReplaceNotPresent() throws Exception {
        assertThat(cache.getAndReplace(0L, "zero")).isNull();
        assertThat(cache.get(0L)).isNull();
    }

    @Test
    public void testConfigurationFail() throws Exception {
        assertThatThrownBy(() -> cache.getConfiguration(OtherConfig.class)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testGetAndPut() throws Exception {
        cache.put(0L, "foo");
        assertThat(cache.getAndPut(0L, "0")).isEqualTo("foo");
        assertThat(cache.get(0L)).isEqualTo("0");
    }

    @Test
    public void testPutAll() throws Exception {
        Map<Long, String> map = new HashMap<>();
        map.put(0L, "0");
        map.put(1L, "1");
        assertThat(cache).isEmpty();
        cache.putAll(map);
        assertThat(cache).containsExactly(new SEntry<>(0L, "0"), new SEntry<>(1L, "1"));
    }

    @Test
    public void testGetAndRemove() throws Exception {
        cache.put(0L, "foo");
        String value = cache.getAndRemove(0L);
        assertThat(value).isEqualTo("foo");
        assertThat(cache.get(0L)).isNull();
    }

    @Test
    public void testRemove() throws Exception {
        cache.put(0L, "foo");
        assertThat(cache.remove(0L, "bar")).isFalse();
        assertThat(cache.get(0L)).isEqualTo("foo");
        assertThat(cache.remove(0L, "foo")).isTrue();
        assertThat(cache.get(0L)).isNull();
    }

    @Test
    public void testReplace() throws Exception {
        cache.put(0L, "foo");
        assertThat(cache.replace(0L, "bar")).isTrue();
        assertThat(cache.get(0L)).isEqualTo("bar");
    }

    @Test
    public void testReplaceFail() throws Exception {
        assertThat(cache.replace(0L, "foo")).isFalse();
        assertThat(cache.get(0L)).isNull();
    }

    @Test
    public void testReplaceWithValue() throws Exception {
        cache.put(0L, "foo");
        assertThat(cache.replace(0L, "bar", "baz")).isFalse();
        assertThat(cache.get(0L)).isEqualTo("foo");
        assertThat(cache.replace(0L, "foo", "bar")).isTrue();
        assertThat(cache.get(0L)).isEqualTo("bar");
    }

    @Test
    public void shouldExpire() throws Exception {
        MutableConfiguration<Long, String> conf = new MutableConfiguration<>();
        conf.setExpiryPolicyFactory(() -> new CreatedExpiryPolicy(new Duration(TimeUnit.SECONDS, 5)));
        cache = cacheManager.createCache(NAME + "-expiry", conf);
        SCache<Long, String> sCache = cache.unwrap(SCache.class);
        assertThat(sCache).isNotNull();
        final AtomicLong time = new AtomicLong(0L);
        sCache.setClock(time::get);
        sCache.put(0L, "foo");
        assertThat(sCache.get(0L)).isEqualTo("foo");
        time.set(TimeUnit.SECONDS.toNanos(10));
        assertThat(sCache.get(0L)).isNull();
    }

    @Test
    public void testClose() throws Exception {
        assertThat(cache.isClosed()).isFalse();
        cache.close();
        assertThat(cache.isClosed()).isTrue();
        assertThatThrownBy(() -> cache.get(0L)).isInstanceOf(IllegalStateException.class);
        cache.close();
    }

    @Test
    public void testIterator() throws Exception {
        cache.put(0L, "foo");
        cache.put(1L, "bar");
        List<MapEntry> all = stream(cache.spliterator(), false).map(e -> entry(e.getKey(), e.getValue())).collect(Collectors.toList());
        assertThat(all).containsExactly(entry(0L, "foo"), entry(1L, "bar"));
    }

    static class OtherConfig implements Configuration<Long, String> {
        @Override
        public Class<Long> getKeyType() {
            return null;
        }

        @Override
        public Class<String> getValueType() {
            return null;
        }

        @Override
        public boolean isStoreByValue() {
            return false;
        }
    }
}