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

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class CacheTest {
	private javax.cache.Cache<Long, String> cache;
    private javax.cache.CacheManager cacheManager;
    private static final String NAME = "hoard";

	@Before
	public void setUp() throws Exception {
        javax.cache.spi.CachingProvider cachingProvider = javax.cache.Caching.getCachingProvider(CachingProvider.class.getCanonicalName());
        cacheManager = cachingProvider.getCacheManager();
		cache = cacheManager.createCache(NAME, null);
	}

    @Test
    public void testKnowOrigins() throws Exception {
        assertThat(cache.getCacheManager()).isEqualTo(cacheManager);
        assertThat(cache.getName()).isEqualTo(NAME);
    }

    @Test
    public void testUnwrap() throws Exception {
        assertThat(cache.unwrap(Cache.class)).isEqualTo(cache);
    }

    @Test
	public void shouldClear() throws Exception {
		cache.put(0L, "foo");
		assertThat(cache.unwrap(Cache.class).stream().count()).isGreaterThan(0L);
		cache.clear();
		assertThat(cache.unwrap(Cache.class).stream().count()).isEqualTo(0L);
	}

	@Test
	public void shouldContainsKey() throws Exception {
		assertThat(cache.containsKey(0L)).isFalse();
		cache.put(0L, "foo");
		assertThat(cache.containsKey(0L)).isTrue();
	}

	@Test
	public void shouldStream() throws Exception {
	   	assertThat(cache.unwrap(Cache.class).stream().count()).isEqualTo(0L);
		cache.put(0L, "foo");
		assertThat(cache.unwrap(Cache.class).stream().count()).isEqualTo(1L);
		assertThat(((Cache<Long,String>)cache.unwrap(Cache.class)).stream().anyMatch(e -> e.getKey().equals(0L))).isTrue();
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
	}

}