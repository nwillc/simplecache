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

import javax.cache.Cache;
import javax.cache.integration.CacheWriter;
import javax.cache.integration.CacheWriterException;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * A CacheWriter implementation that accepts a delete and a write Consumer as arguments to the constructor.
 *
 * @param <K> cache's key type
 * @param <V> cache's value type
 */
public class SCacheWriter<K, V> implements CacheWriter<K, V> {
	private final Consumer<Object> deleter;
	private final Consumer<Cache.Entry<? extends K, ? extends V>> writer;

	public SCacheWriter(Consumer<Object> deleter, Consumer<Cache.Entry<? extends K, ? extends V>> writer) {
		this.deleter = deleter;
		this.writer = writer;
	}

	@Override
	public void delete(Object key) throws CacheWriterException {
		deleter.accept(key);
	}

	@Override
	public void deleteAll(Collection<?> keys) throws CacheWriterException {
		keys.stream().forEach(deleter);
	}

	@Override
	public void write(Cache.Entry<? extends K, ? extends V> entry) throws CacheWriterException {
		writer.accept(entry);
	}

	@Override
	public void writeAll(Collection<Cache.Entry<? extends K, ? extends V>> entries) throws CacheWriterException {
		entries.stream().forEach(writer);
	}

}
