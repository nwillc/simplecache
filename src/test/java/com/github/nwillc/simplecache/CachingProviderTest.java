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

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CachingProviderTest {
    private javax.cache.spi.CachingProvider cachingProvider;

    @Before
    public void setUp() throws Exception {
        cachingProvider = new CachingProvider();
        assertThat(cachingProvider).isNotNull();
    }

    @Test
    public void testGetCacheManager() throws Exception {
        assertThatThrownBy(() -> cachingProvider.getCacheManager(null, null, null)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testGetDefaultClassLoader() throws Exception {
        assertThat(cachingProvider.getDefaultClassLoader()).isInstanceOf(ClassLoader.class);
    }

    @Test
    public void testGetDefaultURI() throws Exception {
        assertThatThrownBy(cachingProvider::getDefaultURI).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testGetDefaultProperties() throws Exception {
        Properties properties = cachingProvider.getDefaultProperties();
        assertThat(properties).isNotNull();
    }

    @Test
    public void testGetCacheManager1() throws Exception {
        assertThatThrownBy(() -> cachingProvider.getCacheManager(null, null)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testGetCacheManager2() throws Exception {
        javax.cache.CacheManager cacheManager = cachingProvider.getCacheManager();
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager).isInstanceOf(CacheManager.class);
    }

    @Test
    public void testClose() throws Exception {
        cachingProvider.close();
    }

    @Test
    public void testClose1() throws Exception {
        cachingProvider.close(null);
    }

    @Test
    public void testClose2() throws Exception {
        assertThatThrownBy(() -> cachingProvider.close(null, null)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testIsSupported() throws Exception {
        assertThat(cachingProvider.isSupported(null)).isFalse();
    }
}