/*
 * Copyright 2019 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.simplecache.spi;

import com.github.nwillc.simplecache.SCacheManager;
import org.junit.Before;
import org.junit.Test;

import javax.cache.CacheManager;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SuppressWarnings("unchecked")
public class SCachingProviderTest {
	private CachingProvider cachingProvider;

	@Before
	public void setUp() throws Exception {
		cachingProvider = new SCachingProvider();
		assertThat(cachingProvider).isNotNull();
	}



	@Test
	public void testGetDefaultClassLoader() throws Exception {
		assertThat(cachingProvider.getDefaultClassLoader()).isInstanceOf(ClassLoader.class);
	}

	@Test
	public void testGetDefaultURI() throws Exception {
		URI uri = cachingProvider.getDefaultURI();
		assertThat(uri).isNotNull();
	}

	@Test
	public void testGetDefaultProperties() throws Exception {
		Properties properties = cachingProvider.getDefaultProperties();
		assertThat(properties).isNotNull();
	}

    @Test
    public void testGetCacheManager1() throws Exception {
        CacheManager cacheManager = cachingProvider.getCacheManager(null, null, null);
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager).isInstanceOf(SCacheManager.class);
    }

	@Test
	public void testGetCacheManager2() throws Exception {
		CacheManager cacheManager = cachingProvider.getCacheManager(null, null);
		assertThat(cacheManager).isNotNull();
		assertThat(cacheManager).isInstanceOf(SCacheManager.class);
	}

	@Test
	public void testGetCacheManager3() throws Exception {
		CacheManager cacheManager = cachingProvider.getCacheManager();
		assertThat(cacheManager).isNotNull();
		assertThat(cacheManager).isInstanceOf(SCacheManager.class);
	}

    @Test
    public void testGetCacheManager4() throws Exception {
        CacheManager cacheManager = cachingProvider.getCacheManager(new URI("foo"), getClass().getClassLoader(), null);
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager).isInstanceOf(SCacheManager.class);
    }

	@Test
	public void testGetCacheManagerNeverReturnClosed() throws Exception {
		CacheManager cacheManager = cachingProvider.getCacheManager();
		cacheManager.close();
		assertThat(cacheManager.isClosed());
		CacheManager cacheManager2 = cachingProvider.getCacheManager();
		assertThat(cacheManager2.isClosed()).isFalse();
		assertThat(cacheManager2).isNotEqualTo(cacheManager);
	}

	@Test
	public void testClose() throws Exception {
		CacheManager cm = cachingProvider.getCacheManager();
		CacheManager cm2 = cachingProvider.getCacheManager();
		assertThat(cm2).isEqualTo(cm);
		cachingProvider.close();
		assertThat(cm.isClosed()).isTrue();
		cm2 = cachingProvider.getCacheManager();
		assertThat(cm2).isNotEqualTo(cm);
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
