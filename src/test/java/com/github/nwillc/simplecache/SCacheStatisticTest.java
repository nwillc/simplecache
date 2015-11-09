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

package com.github.nwillc.simplecache;

import com.github.nwillc.simplecache.managment.SCacheStatisticsMXBean;
import com.github.nwillc.simplecache.spi.SCachingProvider;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Before;
import org.junit.Test;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SCacheStatisticTest {
    private SCache<Long, String> cache;
    private CacheManager cacheManager;
    private SCacheStatisticsMXBean statistics;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        CachingProvider cachingProvider = Caching.getCachingProvider(SCachingProvider.class.getCanonicalName());
        cacheManager = cachingProvider.getCacheManager();
        MutableConfiguration configuration = new MutableConfiguration<>();
        configuration.setStatisticsEnabled(true);
        configuration.setExpiryPolicyFactory(() -> new CreatedExpiryPolicy(new Duration(TimeUnit.MINUTES,1)));
        Cache cache = cacheManager.createCache(this.getClass().getSimpleName(), configuration);
        this.cache = (SCache<Long,String>)cache.unwrap(SCache.class);
        statistics = this.cache.getStatistics();
    }

    @Test
    public void testIsEnabled() throws Exception {
        assertThat(cache.getConfiguration(MutableConfiguration.class).isStatisticsEnabled()).isTrue();
        assertThat(statistics).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testIsNotEnabled() throws Exception {
        Cache cache2 = cacheManager.createCache(this.getClass().getSimpleName() + "-noStats", new MutableConfiguration<>());
        cache = (SCache<Long,String>)cache2.unwrap(SCache.class);
        AssertionsForClassTypes.assertThat(cache.getConfiguration(MutableConfiguration.class).isStatisticsEnabled()).isFalse();
        AssertionsForClassTypes.assertThat(cache.getStatistics()).isNull();
    }

    @Test
    public void testGetStat() throws Exception {
        assertThat(statistics.getCacheGets()).isEqualTo(0L);
        cache.get(0L);
        assertThat(statistics.getCacheGets()).isEqualTo(1L);
        cache.getAndPut(0L,"foo");
        assertThat(statistics.getCacheGets()).isEqualTo(2L);
        cache.getAndReplace(0L, "bar");
        assertThat(statistics.getCacheGets()).isEqualTo(3L);
        cache.getAndRemove(0L);
        assertThat(statistics.getCacheGets()).isEqualTo(4L);
    }

    @Test
    public void testPutStat() throws Exception {
        assertThat(statistics.getCachePuts()).isEqualTo(0L);
        cache.put(0L, "foo");
        assertThat(statistics.getCachePuts()).isEqualTo(1L);
        cache.getAndPut(0L, "bar");
        assertThat(statistics.getCachePuts()).isEqualTo(2L);
        cache.putIfAbsent(1L, "baz");
        assertThat(statistics.getCachePuts()).isEqualTo(3L);
    }

    @Test
    public void testRemoveStats() throws Exception {
        assertThat(statistics.getCacheRemovals()).isEqualTo(0L);
        cache.remove(0L);
        assertThat(statistics.getCacheRemovals()).isEqualTo(0L);
        cache.put(0L, "foo");
        cache.remove(0L);
        assertThat(statistics.getCacheRemovals()).isEqualTo(1L);
        cache.put(0L, "foo");
        cache.remove(0L, "foo");
        assertThat(statistics.getCacheRemovals()).isEqualTo(2L);
        cache.getAndRemove(0L);
        assertThat(statistics.getCacheRemovals()).isEqualTo(3L);
    }

    @Test
    public void testHit() throws Exception {
        assertThat(statistics.getCacheHits()).isEqualTo(0L);
        cache.put(0L, "foo");
        cache.get(0L);
        assertThat(statistics.getCacheHits()).isEqualTo(1L);
    }

    @Test
    public void testMiss() throws Exception {
        assertThat(statistics.getCacheMisses()).isEqualTo(0L);
        cache.get(0L);
        assertThat(statistics.getCacheMisses()).isEqualTo(1L);
    }

    @Test
    public void testEviction() throws Exception {
        final AtomicLong clock = new AtomicLong(0L);
        cache.setClock(clock::get);
        cache.put(0L,"foo");
        cache.get(0L);
        assertThat(statistics.getCacheEvictions()).isEqualTo(0L);
        clock.set(TimeUnit.MINUTES.toNanos(2));
        cache.get(0L);
        assertThat(statistics.getCacheEvictions()).isEqualTo(1L);
    }
}
