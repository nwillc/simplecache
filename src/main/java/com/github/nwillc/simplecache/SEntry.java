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

import javax.cache.Cache;
import java.util.Map;

/**
 * A simple key/value pair implementing Cache.Entry.
 *
 * @param <K> cache's key type
 * @param <V> cache's value type
 */
public class SEntry<K, V> implements Cache.Entry<K, V> {
    private final K key;
    protected V value;

    public SEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    SEntry(Map.Entry<K, V> entry) {
        this(entry.getKey(), entry.getValue());
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        if (clazz.isAssignableFrom(this.getClass())) {
            return clazz.cast(this);
        }

        throw new IllegalArgumentException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SEntry)) return false;

        SEntry<?, ?> sEntry = (SEntry<?, ?>) o;

        return getKey().equals(sEntry.getKey());

    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}
