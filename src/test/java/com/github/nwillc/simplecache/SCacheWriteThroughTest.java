/*
 * Copyright 2019 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
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

@SuppressWarnings("unchecked")
public class SCacheWriteThroughTest {
	private static final String NAME = "hoard";
	private final Map<Long, String> backingStore = new HashMap<>();
	private final Factory<CacheWriter<Long, String>> factory =
			(Factory<CacheWriter<Long, String>>) () -> new SCacheWriter<>(backingStore::remove, e -> backingStore.put(e.getKey(), e.getValue()));
	private Cache<Long, String> cache;
	private CacheManager cacheManager;

	@Before
	public void setUp() throws Exception {
		CachingProvider cachingProvider = Caching.getCachingProvider(SCachingProvider.class.getCanonicalName());
		cacheManager = cachingProvider.getCacheManager();
		MutableConfiguration<Long, String> configuration = new MutableConfiguration<>();
        configuration.setStoreByValue(false);
		configuration.setWriteThrough(true);
		configuration.setCacheWriterFactory(factory);
		cache = cacheManager.createCache(NAME, configuration);
	}

	@Test
	public void testIsWriteThrough() throws Exception {
		assertThat(cache.getConfiguration(CompleteConfiguration.class).isWriteThrough()).isTrue();
	}


	@Test
	public void shouldWriteThrough() throws Exception {
		assertThat(backingStore).isEmpty();
		assertThat(cache).isEmpty();
		cache.put(0L, "foo");
		assertThat(cache).containsExactly(new SEntry<>(0L, "foo"));
		assertThat(backingStore).containsExactly(entry(0L, "foo"));
	}

	@Test
	public void testDeleteThrough() throws Exception {
		backingStore.put(0L, "foo");
		cache.put(0L, "foo");
		assertThat(cache).containsExactly(new SEntry<>(0L, "foo"));
		assertThat(backingStore).containsExactly(entry(0L, "foo"));
		cache.remove(0L);
		assertThat(backingStore).isEmpty();
		assertThat(cache).isEmpty();
	}

	@Test
	public void shouldShouldWriteThroughShutOff() throws Exception {
		assertThat(backingStore).isEmpty();
		cache.put(0L, "0");
		assertThat(backingStore.get(0L)).isEqualTo("0");
		cache.getConfiguration(MutableConfiguration.class).setWriteThrough(false);
		cache.put(1L, "1");
		assertThat(backingStore.get(1L)).isNull();
		cache.remove(0L);
		assertThat(cache.get(0L)).isNull();
		assertThat(backingStore.get(0L)).isNotNull();
	}

	@Test
	public void shouldHandleMissingFunctor() throws Exception {
		MutableConfiguration<Long, String> mutableConfiguration = cache.getConfiguration(MutableConfiguration.class);
		mutableConfiguration.setCacheWriterFactory(null);
		cache = cacheManager.createCache(NAME + "broken", mutableConfiguration);
		cache.put(0L, "0");
		assertThat(backingStore.get(0L)).isNull();
		assertThat(cache.get(0L)).isEqualTo("0");
		cache.remove(0L);
		assertThat(cache.get(0L)).isNull();
	}
}
