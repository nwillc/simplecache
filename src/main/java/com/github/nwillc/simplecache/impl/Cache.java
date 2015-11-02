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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Cache<K,V> implements com.github.nwillc.simplecache.Cache<K,V> {
	private Map<K,V> map = new ConcurrentHashMap<>();

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public Stream<Entry<K, V>> stream() {
		return map.entrySet().stream().map(e -> new KeyValue<>(e.getKey(), e.getValue()));
	}

	@Override
	public void put(K key, V value) {
	   map.put(key, value);
	}

	@Override
	public boolean putIfAbsent(K key, V value) {
		if (containsKey(key)) {
			return false;
		}

		put(key, value);
		return true;
	}

	@Override
	public boolean remove(K key) {
		if (!containsKey(key)) {
			return false;
		}
		map.remove(key);
		return true;
	}

	@Override
	public V get(K key) {
		return map.get(key);
	}
}
