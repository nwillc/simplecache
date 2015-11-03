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

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.data.MapEntry.entry;

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
        assertThat(cache.getAll(keys)).containsExactly(entry(0L,"zero"), entry(1L, "one"));
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
    public void testUnwrapFail() throws Exception {
        assertThatThrownBy(() -> cache.unwrap(String.class)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testLoadAll() throws Exception {
        assertThatThrownBy(() -> cache.loadAll(null,false,null)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void testDeregisterListener() throws Exception {
        assertThatThrownBy(() -> cache.deregisterCacheEntryListener(null)).isInstanceOf(UnsupportedOperationException.class);

    }

    @Test
    public void testClose() throws Exception {
        cache.close();
    }

    @Test
    public void testIsClosed() throws Exception {
        assertThat(cache.isClosed()).isFalse();
    }

    @Test
    public void testRegisterListener() throws Exception {
        assertThatThrownBy(() -> cache.registerCacheEntryListener(null)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
	public void shouldClear() throws Exception {
		cache.put(0L, "foo");
		assertThat(cache.unwrap(Cache.class).stream().count()).isGreaterThan(0L);
		cache.clear();
		assertThat(cache.unwrap(Cache.class).stream().count()).isEqualTo(0L);
	}

    @Test
    public void testInvokeAll() throws Exception {
        assertThatThrownBy(() -> cache.invokeAll(null,null)).isInstanceOf(UnsupportedOperationException.class);
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

    @SuppressWarnings("unchecked")
	@Test
	public void shouldStream() throws Exception {
	   	assertThat(cache.unwrap(Cache.class).stream().count()).isEqualTo(0L);
		cache.put(0L, "foo");
		assertThat(cache.unwrap(Cache.class).stream().count()).isEqualTo(1L);
		assertThat(((Cache<Long,String>)cache.unwrap(Cache.class)).stream().anyMatch(e -> e.getKey().equals(0L))).isTrue();
	}

    @Test
    public void testRemoveAll() throws Exception {
        cache.put(0L, "zero");
        cache.put(1L, "one");
        assertThat(cache).hasSize(2);
        cache.removeAll();
        assertThat(cache).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRemoveAllSet() throws Exception {
        cache.put(0L, "zero");
        cache.put(1L, "one");
        assertThat(cache).hasSize(2);

        Set<Long> keys = new HashSet<>();
        keys.add(0L);
        cache.removeAll(keys);
        assertThat(cache).containsExactly(new Entry<>(1L, "one"));
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
}