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

package com.github.nwillc.simplecache.integration;

import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheLoaderException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.StreamSupport.stream;

/**
 * A CacheLoader implementation that accepts a loading Function as an argument to the constructor.
 *
 * @param <K> cache's key type
 * @param <V> cache's value type
 */
public class SCacheLoader<K, V> implements CacheLoader<K, V> {
    private final Function<K, V> loader;

    public SCacheLoader(Function<K, V> loader) {
        this.loader = loader;
    }

    @Override
    public V load(K key) throws CacheLoaderException {
        return loader.apply(key);
    }

    @Override
    public Map<K, V> loadAll(Iterable<? extends K> keys) throws CacheLoaderException {
        // Collectors.toMap has issues with some versions of jdk8 so doing this w/ forEach
        Map<K, V> map = new HashMap<>();
        stream(keys.spliterator(), false).forEach(k -> map.put(k, load(k)));
        return map;
    }
}
