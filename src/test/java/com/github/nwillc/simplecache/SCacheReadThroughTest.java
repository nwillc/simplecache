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
import javax.cache.spi.CachingProvider;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.data.MapEntry.entry;

public class SCacheReadThroughTest {
    private static final String NAME = "hoard";
    private Cache<Long, String> cache;
    private Map<Long,String> backingStore = new HashMap<>();
    private Factory<CacheLoader<Long,String>> factory =
            (Factory<CacheLoader<Long, String>>) () -> new SCacheLoader<>(backingStore::get);

    @Before
    public void setUp() throws Exception {
        CachingProvider cachingProvider = Caching.getCachingProvider(SCachingProvider.class.getCanonicalName());
        CacheManager cacheManager = cachingProvider.getCacheManager();
        MutableConfiguration<Long,String> configuration = new MutableConfiguration<>();
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
        backingStore.put(0L,"0");
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
}