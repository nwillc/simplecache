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

package com.github.nwillc.simplecache.integration;

import com.github.nwillc.simplecache.SEntry;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.integration.CacheWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.data.MapEntry.entry;

public class SCacheWriterTest {
	private final Map<Long, String> map = new HashMap<>();
	private final SCacheWriter<Long, String> cacheWriter = new SCacheWriter<>(map::remove,
			entry -> map.put(entry.getKey(), entry.getValue()));

	@Before
	public void setUp() throws Exception {
		map.clear();
		assertThat(cacheWriter).isInstanceOf(CacheWriter.class);
	}

	@Test
	public void shouldDelete() throws Exception {
		map.put(0L, "foo");
		assertThat(map).containsOnly(entry(0L, "foo"));
		cacheWriter.delete(0L);
		assertThat(map).doesNotContain(entry(0L, "foo"));
	}

	@Test
	public void shouldDeleteAll() throws Exception {
		map.put(0L, "foo");
		map.put(1L, "bar");
		assertThat(map).containsOnly(entry(0L, "foo"), entry(1L, "bar"));
		List<Long> keys = new ArrayList<>();
		keys.add(0L);
		keys.add(1L);
		cacheWriter.deleteAll(keys);
		assertThat(map).doesNotContain(entry(0L, "foo"), entry(1L, "bar"));
	}

	@Test
	public void shouldWrite() throws Exception {
		assertThat(map).isEmpty();
		SEntry<Long, String> entry = new SEntry<>(0L, "foo");
		cacheWriter.write(entry);
		assertThat(map).containsOnly(entry(0L, "foo"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldWriteAll() throws Exception {
		assertThat(map).isEmpty();
		List<Cache.Entry<Long, String>> entries = new ArrayList<>();
		entries.add(new SEntry<>(0L, "foo"));
		entries.add(new SEntry<>(1L, "bar"));
		cacheWriter.writeAll((Collection) entries);
		assertThat(map).containsOnly(entry(0L, "foo"), entry(1L, "bar"));
	}
}