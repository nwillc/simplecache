/*
 * Copyright 2019 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.simplecache;

import org.junit.Test;

import javax.cache.Cache;
import java.util.AbstractMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SEntryTest {

	@Test
	public void testKV() throws Exception {
		SEntry<Long, String> entry = new SEntry<>(0L, "foo");
		assertThat(entry.getKey()).isEqualTo(0L);
		assertThat(entry.getValue()).isEqualTo("foo");
	}

	@Test
	public void testHashCode() throws Exception {
		SEntry<Long, String> entry = new SEntry<>(0L, "foo");
		SEntry<Long, String> entry1 = new SEntry<>(0L, "bar");
		SEntry<Long, String> entry2 = new SEntry<>(1L, "foo");

		assertThat(entry.hashCode()).isEqualTo(entry1.hashCode());
		assertThat(entry.hashCode()).isNotEqualTo(entry2.hashCode());
	}

	@Test
	public void testEquals() throws Exception {
		SEntry<Long, String> entry = new SEntry<>(0L, "foo");
		SEntry<Long, String> entry1 = new SEntry<>(0L, "bar");
		SEntry<Long, String> entry2 = new SEntry<>(1L, "foo");

		assertThat(entry).isEqualTo(entry1);
		assertThat(entry).isNotEqualTo(entry2);

		AbstractMap.SimpleEntry<Long, String> simpleEntry = new AbstractMap.SimpleEntry<>(0L, "foo");
		assertThat(entry).isNotEqualTo(simpleEntry);
	}

	@Test
	public void shouldStore() throws Exception {
		final Long key = 42L;
		final String value = "The answer.";

		SEntry<Long, String> kv = new SEntry<>(key, value);
		assertThat(kv.getKey()).isEqualTo(key);
		assertThat(kv.getValue()).isEqualTo(value);
	}

	@Test
	public void testUnwrap() throws Exception {
		Cache.Entry<Long, String> entry = new SEntry<>(0L, "foo");

		assertThat(entry.unwrap(SEntry.class)).isEqualTo(entry);
	}

	@Test
	public void testUnwrapFail() throws Exception {
		Cache.Entry<Long, String> entry = new SEntry<>(0L, "foo");
		assertThatThrownBy(() -> entry.unwrap(String.class)).isInstanceOf(IllegalArgumentException.class);
	}
}
