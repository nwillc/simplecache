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

import org.junit.Test;

import javax.cache.Cache;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class EntryTest {
	@Test
	public void shouldStore() throws Exception {
		final Long key = 42L;
		final String value = "The answer.";

		Entry<Long,String> kv = new Entry<>(key, value);
		assertThat(kv.getKey()).isEqualTo(key);
		assertThat(kv.getValue()).isEqualTo(value);
	}

    @Test
    public void testUnwrap() throws Exception {
        Cache.Entry<Long, String> entry = new Entry<>(0L, "foo");

        assertThat(entry.unwrap(Entry.class)).isEqualTo(entry);
    }

    @Test
    public void testUnwrapFail() throws Exception {
        Cache.Entry<Long, String> entry = new Entry<>(0L, "foo");
        assertThatThrownBy(() -> entry.unwrap(String.class)).isInstanceOf(IllegalArgumentException.class);
    }
}