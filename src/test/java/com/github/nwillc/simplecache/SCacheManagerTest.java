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
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SuppressWarnings("unchecked")
public class SCacheManagerTest {
    private final MutableConfiguration configuration = new MutableConfiguration();
    private CacheManager cacheManager;

    @Before
    public void setUp() throws Exception {
        cacheManager = Caching.getCachingProvider().getCacheManager();
    }

    @Test
    public void testGetCachingProvider() throws Exception {
        CachingProvider cachingProvider = cacheManager.getCachingProvider();
        assertThat(cachingProvider).isNotNull();
        assertThat(cachingProvider).isInstanceOf(SCachingProvider.class);
    }

    @Test
    public void testGetClassLoader() throws Exception {
        ClassLoader classLoader = cacheManager.getClassLoader();
        assertThat(classLoader).isNotNull();
        assertThat(classLoader).isInstanceOf(ClassLoader.class);
    }

    @Test
    public void testGetProperties() throws Exception {
        Properties properties = cacheManager.getProperties();
        assertThat(properties).isNotNull();
        assertThat(properties).isInstanceOf(Properties.class);
    }

    @Test
    public void testGetURI() throws Exception {
        assertThatThrownBy(cacheManager::getURI).isInstanceOf(UnsupportedOperationException.class);

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCreateCache() throws Exception {
        Cache cache = cacheManager.createCache("foo", configuration);
        assertThat(cache).isNotNull();
        assertThat(cache).isInstanceOf(SCache.class);
    }

    @Test
    public void shouldDestroyCache() throws Exception {
        cacheManager.createCache("foo", configuration);
        assertThat(cacheManager.getCache("foo")).isNotNull();
        cacheManager.destroyCache("foo");
        assertThat(cacheManager.getCache("foo")).isNull();
    }

    @Test
    public void shouldGetCache() throws Exception {
        assertThat(cacheManager.getCache("foo")).isNull();
        cacheManager.createCache("foo", configuration);
        assertThat(cacheManager.getCache("foo")).isNotNull();
    }

    @Test
    public void shouldGetCache2() throws Exception {
        assertThat(cacheManager.getCache("foo")).isNull();
        cacheManager.createCache("foo", configuration);
        assertThat(cacheManager.getCache("foo", String.class, Integer.class)).isNotNull();
    }

    @Test
    public void testGetCacheNames() throws Exception {
        cacheManager.createCache("foo", configuration);
        cacheManager.createCache("bar", configuration);
        assertThat(cacheManager.getCacheNames()).contains("foo", "bar");
    }

    @Test
    public void testEnableManagement() throws Exception {
        assertThatThrownBy(() -> cacheManager.enableManagement("foo", true)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testEnableStatistics() throws Exception {
        assertThatThrownBy(() -> cacheManager.enableStatistics("foo", true)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testClose() throws Exception {
        assertThat(cacheManager.isClosed()).isFalse();
        cacheManager.close();
        assertThat(cacheManager.isClosed()).isTrue();
        assertThatThrownBy(() -> cacheManager.destroyCache("foo")).isInstanceOf(IllegalStateException.class);
        cacheManager.close();
    }

    @Test
    public void testUnwrap() throws Exception {
        CacheManager cacheManager1 = cacheManager.unwrap(SCacheManager.class);
        assertThat(cacheManager1).isNotNull();
        assertThat(cacheManager1).isInstanceOf(SCacheManager.class);
    }

    @Test
    public void testUnwrapFail() throws Exception {
        assertThatThrownBy(() -> cacheManager.unwrap(String.class)).isInstanceOf(IllegalArgumentException.class);
    }
}