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
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.*;
import javax.cache.spi.CachingProvider;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class SCacheListenerTest {
    private CacheManager cacheManager;
    private Factory<CacheEntryEventFilter<String,Long>> filterFactory =
            () -> (CacheEntryEventFilter<String,Long>) evaluate -> true;

    @Before
    public void setUp() throws Exception {
        CachingProvider cachingProvider = Caching.getCachingProvider(SCachingProvider.class.getCanonicalName());
        cacheManager = cachingProvider.getCacheManager();
    }

    @Test
    public void testNoListenerConfig() throws Exception {
        Cache<String, Long> cache = cacheManager.createCache("no listeners",new MutableConfiguration<>());
        assertThat(cache.getConfiguration(MutableConfiguration.class).getCacheEntryListenerConfigurations()).hasSize(0);
    }

    @Test
	public void testCreatedListener() throws Exception {
        final Semaphore semaphore = new Semaphore(1);

        semaphore.acquire();

		Factory<CacheEntryListener<String,Long>> listenerFactory =
                () -> (CacheEntryCreatedListener<String, Long>) cacheEntryEvents -> semaphore.release();

		MutableCacheEntryListenerConfiguration<String, Long> listenerConfig
				= new MutableCacheEntryListenerConfiguration<>(listenerFactory, filterFactory, false, true);

		assertThat(listenerConfig).isNotNull();
		MutableConfiguration<String, Long> configuration = new MutableConfiguration<>();
		assertThat(configuration).isNotNull();
		configuration.addCacheEntryListenerConfiguration(listenerConfig);
		AssertionsForInterfaceTypes.assertThat(configuration.getCacheEntryListenerConfigurations()).hasSize(1);

        Cache<String, Long> cache = cacheManager.createCache(this.getClass().getSimpleName(), configuration);
        cache.put("foo", 0l);
        if (!semaphore.tryAcquire(1, 5, TimeUnit.SECONDS)) {
            fail("never notified");
        }
	}

    @Test
    public void testRemovedListener() throws Exception {
        final Semaphore semaphore = new Semaphore(1);

        semaphore.acquire();

        Factory<CacheEntryListener<String,Long>> listenerFactory =
                () -> (CacheEntryRemovedListener<String, Long>) cacheEntryEvents -> semaphore.release();

        MutableCacheEntryListenerConfiguration<String, Long> listenerConfig
                = new MutableCacheEntryListenerConfiguration<>(listenerFactory, filterFactory, false, true);

        assertThat(listenerConfig).isNotNull();
        MutableConfiguration<String, Long> configuration = new MutableConfiguration<>();
        assertThat(configuration).isNotNull();
        configuration.addCacheEntryListenerConfiguration(listenerConfig);
        AssertionsForInterfaceTypes.assertThat(configuration.getCacheEntryListenerConfigurations()).hasSize(1);

        Cache<String, Long> cache = cacheManager.createCache("REMOVE", configuration);
        cache.put("foo", 0l);
        cache.remove("foo");

        if (!semaphore.tryAcquire(1, 5, TimeUnit.SECONDS)) {
            fail("never notified of removed");
        }
    }
}
