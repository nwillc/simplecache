/*
 *    Copyright (c) 2015, nwillc@gmail.com
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.simplecache;

import com.github.nwillc.simplecache.integration.SCacheWriter;
import com.github.nwillc.simplecache.spi.SCachingProvider;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.integration.CacheWriter;
import javax.cache.spi.CachingProvider;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.data.MapEntry.entry;

public class SCacheWriteThroughTest {
    private static final String NAME = "hoard";
    private Cache<Long, String> cache;
    private Map<Long,String> backingStore = new HashMap<>();
    private Factory<CacheWriter<Long,String>> factory =
            (Factory<CacheWriter<Long, String>>) () -> new SCacheWriter<>(backingStore::remove,e -> backingStore.put(e.getKey(), e.getValue()));

    @Before
    public void setUp() throws Exception {
        CachingProvider cachingProvider = Caching.getCachingProvider(SCachingProvider.class.getCanonicalName());
        CacheManager cacheManager = cachingProvider.getCacheManager();
        MutableConfiguration<Long,String> configuration = new MutableConfiguration<>();
        configuration.setWriteThrough(true);
        configuration.setCacheWriterFactory(factory);
        cache = cacheManager.createCache(NAME, configuration);
    }

    @Test
    public void testIsWriteThrough() throws Exception {
        assertThat(cache.getConfiguration(CompleteConfiguration.class).isWriteThrough()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldWriteThrough() throws Exception {
        assertThat(backingStore).isEmpty();
        assertThat(cache).isEmpty();
        cache.put(0L,"foo");
        assertThat(cache).containsExactly(new SEntry<>(0L,"foo"));
        assertThat(backingStore).containsExactly(entry(0L,"foo"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDeleteThrough() throws Exception {
        backingStore.put(0L, "foo");
        cache.put(0L,"foo");
        assertThat(cache).containsExactly(new SEntry<>(0L,"foo"));
        assertThat(backingStore).containsExactly(entry(0L,"foo"));
        cache.remove(0L);
        assertThat(backingStore).isEmpty();
        assertThat(cache).isEmpty();
    }
}