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

package com.github.nwillc.simplecache.event;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.EventType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.mock;

public class SCacheEntryEventTest {
    private Cache cache = mock(Cache.class);
    private CacheEntryEvent<String, Long> entryEvent;

    @Before
    public void setUp() throws Exception {
        entryEvent = new SCacheEntryEvent<>(cache, EventType.CREATED, "foo", 1L, 0L);

    }

    @Test
    public void testGetOldValueNotAvailable() throws Exception {
        entryEvent = new SCacheEntryEvent<>(cache, EventType.CREATED, "foo", null, 0L);

        assertThat(entryEvent.isOldValueAvailable()).isFalse();
        assertThat(entryEvent.getOldValue()).isNull();
    }

    @Test
    public void testGetOldValueAvailable() throws Exception {
        assertThat(entryEvent.isOldValueAvailable()).isTrue();
        assertThat(entryEvent.getOldValue()).isEqualTo(1L);
    }

    @Test
    public void testGetKey() throws Exception {
        assertThat(entryEvent.getKey()).isEqualTo("foo");
    }

    @Test
    public void testGetValue() throws Exception {
        assertThat(entryEvent.getValue()).isEqualTo(0L);
    }


    @Test
    public void testUnwrap() throws Exception {
        SCacheEntryEvent sCacheEntryEvent = entryEvent.unwrap(SCacheEntryEvent.class);
        AssertionsForClassTypes.assertThat(sCacheEntryEvent).isNotNull();
        AssertionsForClassTypes.assertThat(sCacheEntryEvent).isInstanceOf(SCacheEntryEvent.class);
    }

    @Test
    public void testUnwrapFail() throws Exception {
        assertThatThrownBy(() -> entryEvent.unwrap(String.class)).isInstanceOf(IllegalArgumentException.class);
    }
}
