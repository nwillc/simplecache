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

package com.github.nwillc.simplecache.impl;

import com.github.nwillc.simplecache.Cache;
import com.github.nwillc.simplecache.Configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager implements com.github.nwillc.simplecache.CacheManager {
	private final Map<String, Cache> caches = new ConcurrentHashMap<>();

	@Override
	public <K, V> Cache<K, V> createCache(String cacheName, Configuration<K, V> configuration) {
		com.github.nwillc.simplecache.impl.Cache<K,V> cache =
				new com.github.nwillc.simplecache.impl.Cache<>();
		caches.put(cacheName, cache);
		return cache;
	}

	@Override
	public void destroyCache(String cacheName) {
		caches.remove(cacheName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Cache<K, V> getCache(String cacheName) {
		return (Cache<K,V>)caches.get(cacheName);
	}
}
