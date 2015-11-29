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

package com.github.nwillc.simplecache.managment;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SCacheStatisticsMXBeanTest {
	private SCacheStatisticsMXBean statistics;

	@Before
	public void setUp() throws Exception {
		statistics = new SCacheStatisticsMXBean();
	}

	@Test
	public void shouldNotHaveHitStats() throws Exception {
		assertThat(statistics.getCacheHitPercentage()).isEqualTo(0);
	}

	@Test
	public void shouldNotCacheMissPercentage() throws Exception {
		assertThat(statistics.getCacheMissPercentage()).isEqualTo(0);
	}

	@Test
	public void shouldNotGetAverageGetTime() throws Exception {
		assertThat(statistics.getAverageGetTime()).isEqualTo(0);
	}

	@Test
	public void shouldNotGetAveragePutTime() throws Exception {
		assertThat(statistics.getAveragePutTime()).isEqualTo(0);
	}

	@Test
	public void shouldNotGetAverageRemoveTime() throws Exception {
		assertThat(statistics.getAverageRemoveTime()).isEqualTo(0);
	}

	@Test
	public void shouldClear() throws Exception {
		assertThat(statistics.hit()).isEqualTo(1L);
		assertThat(statistics.miss()).isEqualTo(1L);
		assertThat(statistics.get()).isEqualTo(1L);
		assertThat(statistics.put()).isEqualTo(1L);
		assertThat(statistics.remove()).isEqualTo(1L);
		assertThat(statistics.eviction()).isEqualTo(1L);
		statistics.clear();
		assertThat(statistics.getCacheHits()).isEqualTo(0L);
		assertThat(statistics.getCacheMisses()).isEqualTo(0L);
		assertThat(statistics.getCacheGets()).isEqualTo(0L);
		assertThat(statistics.getCachePuts()).isEqualTo(0L);
		assertThat(statistics.getCacheRemovals()).isEqualTo(0L);
		assertThat(statistics.getCacheEvictions()).isEqualTo(0L);
	}

	@Test
	public void shouldHit() throws Exception {
		assertThat(statistics.getCacheHits()).isEqualTo(0L);
		assertThat(statistics.hit()).isEqualTo(1L);
		assertThat(statistics.getCacheHits()).isEqualTo(1L);
	}

	@Test
	public void shouldMiss() throws Exception {
		assertThat(statistics.getCacheMisses()).isEqualTo(0L);
		assertThat(statistics.miss()).isEqualTo(1L);
		assertThat(statistics.getCacheMisses()).isEqualTo(1L);
	}

	@Test
	public void shouldGet() throws Exception {
		assertThat(statistics.getCacheGets()).isEqualTo(0L);
		assertThat(statistics.get()).isEqualTo(1L);
		assertThat(statistics.getCacheGets()).isEqualTo(1L);
	}

	@Test
	public void shouldPut() throws Exception {
		assertThat(statistics.getCachePuts()).isEqualTo(0L);
		assertThat(statistics.put()).isEqualTo(1L);
		assertThat(statistics.getCachePuts()).isEqualTo(1L);
	}

	@Test
	public void shouldRemove() throws Exception {
		assertThat(statistics.getCacheRemovals()).isEqualTo(0L);
		assertThat(statistics.remove()).isEqualTo(1L);
		assertThat(statistics.getCacheRemovals()).isEqualTo(1L);
	}

	@Test
	public void shouldEviction() throws Exception {
		assertThat(statistics.getCacheEvictions()).isEqualTo(0L);
		assertThat(statistics.eviction()).isEqualTo(1L);
		assertThat(statistics.getCacheEvictions()).isEqualTo(1L);
	}

	@Test
	public void shouldToString() throws Exception {
		assertThat(statistics.toString()).startsWith("SCacheStatisticsMXBean{");
		assertThat(statistics.toString()).endsWith("}");
	}
}