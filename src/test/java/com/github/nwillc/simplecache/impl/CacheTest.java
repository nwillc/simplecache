package com.github.nwillc.simplecache.impl;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class CacheTest {
	private com.github.nwillc.simplecache.Cache<Long, String> cache;

	@Before
	public void setUp() throws Exception {
		cache = new Cache<>();
	}

	@Test
	public void shouldClear() throws Exception {
		cache.put(0L, "foo");
		assertThat(cache.stream().count()).isGreaterThan(0L);
		cache.clear();
		assertThat(cache.stream().count()).isEqualTo(0L);
	}

	@Test
	public void shouldContainsKey() throws Exception {
		assertThat(cache.containsKey(0L)).isFalse();
		cache.put(0L, "foo");
		assertThat(cache.containsKey(0L)).isTrue();
	}

	@Test
	public void shouldStream() throws Exception {
	   	assertThat(cache.stream().count()).isEqualTo(0L);
		cache.put(0L, "foo");
		assertThat(cache.stream().count()).isEqualTo(1L);
		assertThat(cache.stream().anyMatch(e -> e.getKey().equals(0L))).isTrue();
	}

	@Test
	public void shouldPutIfAbsent() throws Exception {
	   	cache.put(0L, "foo");
		assertThat(cache.get(0L)).isEqualTo("foo");
		assertThat(cache.putIfAbsent(0L, "bar")).isFalse();
		assertThat(cache.putIfAbsent(1L, "bar")).isTrue();
		assertThat(cache.get(0L)).isEqualTo("foo");
		assertThat(cache.get(1L)).isEqualTo("bar");
	}

	@Test
	public void shouldRemove() throws Exception {
		cache.put(0L, "foo");
		assertThat(cache.containsKey(0L)).isTrue();
		assertThat(cache.remove(0L)).isTrue();
		assertThat(cache.containsKey(0L)).isFalse();
	}

}