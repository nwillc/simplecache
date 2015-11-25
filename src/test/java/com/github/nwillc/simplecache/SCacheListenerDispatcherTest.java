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
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.spi.CachingProvider;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class SCacheListenerDispatcherTest {

	@Test
	public void testTransfer() throws Exception {
		final int SIZE = 5;

		Deque<Integer> deque = new ArrayDeque<>();
		for (int x = 0; x < SIZE; x++) {
			deque.addFirst(x);
		}
		assertThat(deque).hasSize(SIZE);
		List<Integer> list = SCacheListenerDispatcher.transfer(deque);
		assertThat(deque).hasSize(0);
		assertThat(list).hasSize(SIZE);
	}

    @SuppressWarnings("unchecked")
    @Test
    public void testListenerEquals() throws Exception {
        CacheEntryListenerConfiguration conf1 = mock(CacheEntryListenerConfiguration.class);
        CacheEntryListenerConfiguration conf2 = mock(CacheEntryListenerConfiguration.class);
        SCacheListenerDispatcher.Listener listener1 = new SCacheListenerDispatcher.Listener<>(conf1);
        SCacheListenerDispatcher.Listener listener2 = new SCacheListenerDispatcher.Listener<>(conf2);
        SCacheListenerDispatcher.Listener listener3 = new SCacheListenerDispatcher.Listener<>(conf1);

        assertThat(listener1).isEqualTo(listener1);
        assertThat(listener1).isEqualTo(listener3);
        assertThat(listener1).isNotEqualTo(listener2);
        assertThat(listener1).isNotEqualTo(null);
        assertThat(listener1).isNotEqualTo("foo");
    }

    @Test
    public void testToConsumerNullType() throws Exception {
        assertThatThrownBy(() -> SCacheListenerDispatcher.toConsumer(null, null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testTypeOfUnknown() throws Exception {
        CacheEntryListener weirdListener = new CacheEntryListener() {};

        assertThatThrownBy(() -> SCacheListenerDispatcher.typeOf(weirdListener)).isInstanceOf(IllegalArgumentException.class);
    }


}