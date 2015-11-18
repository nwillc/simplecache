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

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.Test;

import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SCacheListenerTest {
	@Test
	public void shouldSupportListener() throws Exception {
		Factory<CacheEntryListener<String,Long>> listenerFactory =
				(Factory<CacheEntryListener<String, Long>>) ()
						-> (CacheEntryCreatedListener<String, Long>) cacheEntryEvents -> {};

		Factory<CacheEntryEventFilter<String,Long>> filterFactory =
				(Factory<CacheEntryEventFilter<String,Long>>) ()
						-> (CacheEntryEventFilter<String,Long>) evaluate -> true;

		MutableCacheEntryListenerConfiguration<String, Long> listenerConfig
				= new MutableCacheEntryListenerConfiguration<>(listenerFactory, filterFactory, true, true);

		assertThat(listenerConfig).isNotNull();
		MutableConfiguration<String, Long> configuration = new MutableConfiguration<>();
		assertThat(configuration).isNotNull();
		configuration.addCacheEntryListenerConfiguration(listenerConfig);
		AssertionsForInterfaceTypes.assertThat(configuration.getCacheEntryListenerConfigurations()).hasSize(1);
	}
}
