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


public class CacheManagerTest {
	private javax.cache.CacheManager cacheManager;

	@Before
	public void setUp() throws Exception {
		cacheManager = new CacheManager();
	}

	@Test
	public void shouldCreateCache() throws Exception {
		javax.cache.Cache cache = cacheManager.createCache("foo", null);
		assertThat(cache).isNotNull();
		assertThat(cache).isInstanceOf(Cache.class);
	}

	@Test
	public void shouldDestroyCache() throws Exception {
		cacheManager.createCache("foo", null);
		assertThat(cacheManager.getCache("foo")).isNotNull();
		cacheManager.destroyCache("foo");
		assertThat(cacheManager.getCache("foo")).isNull();
	}

	@Test
	public void shouldGetCache() throws Exception {
		assertThat(cacheManager.getCache("foo")).isNull();
		cacheManager.createCache("foo", null);
		assertThat(cacheManager.getCache("foo")).isNotNull();
	}
}