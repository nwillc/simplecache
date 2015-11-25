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

package com.github.nwillc.simplecache.processor;

import com.github.nwillc.simplecache.spi.SCachingProvider;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class SMutableEntryTest {
    private Cache<Long,String> cache;
    private SMutableEntry<Long, String> entry;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        CachingProvider cachingProvider = Caching.getCachingProvider(SCachingProvider.class.getCanonicalName());
        CacheManager cacheManager = cachingProvider.getCacheManager();
        cache = cacheManager.createCache(this.getClass().getSimpleName(), new MutableConfiguration<>());
        cache.put(0L, "foo");
        entry = new SMutableEntry<>(cache, 0L);
    }

    @Test
    public void testExists() throws Exception {
        assertThat(entry.exists()).isTrue();
    }

    @Test
    public void testNotExists() throws Exception {
        entry = new SMutableEntry<>(cache, 1L);
        assertThat(entry.exists()).isFalse();
    }

    @Test
    public void testRemove() throws Exception {
        assertThat(cache.containsKey(0L)).isTrue();
        entry.remove();
        assertThat(cache.containsKey(0L)).isFalse();
    }

    @Test
    public void testSetValue() throws Exception {
        assertThat(entry.getValue()).isEqualTo("foo");
        entry.setValue("bar");
        assertThat(entry.getValue()).isEqualTo("bar");
    }
}