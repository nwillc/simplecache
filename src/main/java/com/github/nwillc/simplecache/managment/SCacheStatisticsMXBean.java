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

import javax.cache.management.CacheStatisticsMXBean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A bean that stores statics of a running cache is statistics are enabled.
 */
public class SCacheStatisticsMXBean implements CacheStatisticsMXBean {
    private final AtomicLong cacheHits = new AtomicLong(0L);
    private final AtomicLong cacheMisses = new AtomicLong(0L);
    private final AtomicLong cacheGets = new AtomicLong(0L);
    private final AtomicLong cachePuts = new AtomicLong(0L);
    private final AtomicLong cacheRemovals = new AtomicLong(0L);
    private final AtomicLong cacheEvictions = new AtomicLong(0L);
    private final AtomicLong readThrough = new AtomicLong(0L);
    private final AtomicLong writeThrough = new AtomicLong(0L);
    private final AtomicLong removeThrough = new AtomicLong(0L);

    @Override
    public void clear() {
        cacheHits.set(0L);
        cacheMisses.set(0L);
        cacheGets.set(0L);
        cachePuts.set(0L);
        cacheRemovals.set(0L);
        cacheEvictions.set(0L);
    }

    @Override
    public long getCacheHits() {
        return cacheHits.get();
    }

    public long hit() {
        return cacheHits.incrementAndGet();
    }

    @Override
    public float getCacheHitPercentage() {
        return 0;
    }

    @Override
    public long getCacheMisses() {
        return cacheMisses.get();
    }

    public long miss() {
        return cacheMisses.incrementAndGet();
    }

    @Override
    public float getCacheMissPercentage() {
        return 0;
    }

    @Override
    public long getCacheGets() {
        return cacheGets.get();
    }

    public Long get() {
        return cacheGets.incrementAndGet();
    }

    @Override
    public long getCachePuts() {
        return cachePuts.get();
    }

    public long put() {
        return cachePuts.incrementAndGet();
    }

    @Override
    public long getCacheRemovals() {
        return cacheRemovals.get();
    }

    public long remove() {
        return cacheRemovals.incrementAndGet();
    }

    @Override
    public long getCacheEvictions() {
        return cacheEvictions.get();
    }

    public long eviction() {
        return cacheEvictions.incrementAndGet();
    }

    @Override
    public float getAverageGetTime() {
        return 0;
    }

    @Override
    public float getAveragePutTime() {
        return 0;
    }

    @Override
    public float getAverageRemoveTime() {
        return 0;
    }

    public long getReadThrough() {
        return readThrough.get();
    }

    public long readThrough() {
        return readThrough.incrementAndGet();
    }

    public long getWriteThrough() {
        return writeThrough.get();
    }

    public long writeThrough() {
        return writeThrough.incrementAndGet();
    }

    public long getRemoveThrough() {
        return removeThrough.get();
    }

    public long removeThrough() {
        return removeThrough.incrementAndGet();
    }

    @Override
    public String toString() {
        return "SCacheStatisticsMXBean{" +
                "cacheEvictions=" + cacheEvictions.get() +
                ", cacheHits=" + cacheHits.get() +
                ", cacheMisses=" + cacheMisses.get() +
                ", cacheGets=" + cacheGets.get() +
                ", cachePuts=" + cachePuts.get() +
                ", cacheRemovals=" + cacheRemovals.get() +
                ", readThrough=" + readThrough.get() +
                ", writeThrough=" + writeThrough.get() +
                ", removeThrough=" + removeThrough.get() +
                '}';
    }
}
