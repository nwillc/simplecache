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

import org.junit.Before;
import org.junit.Test;

import java.util.function.Supplier;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ExpiryDataTest {
    private TimeSource clock;
    private ExpiryData expiryData;

    @Before
    public void setUp() throws Exception {
        clock = new TimeSource();
        expiryData = new ExpiryData(clock::get);
    }

    @Test
    public void testCreated() throws Exception {
        clock.time = 5L;
        expiryData = new ExpiryData(clock::get);
        assertThat(expiryData.created).isEqualTo(5L);
        assertThat(expiryData.accessed).isEqualTo(ExpiryData.NEVER);
        assertThat(expiryData.updated).isEqualTo(ExpiryData.NEVER);
    }

    @Test
    public void testAccess() throws Exception {
        assertThat(expiryData.created).isEqualTo(TimeSource.GENESIS);
        clock.time = 2L;
        assertThat(expiryData.access().accessed).isEqualTo(2L);
        assertThat(expiryData.updated).isEqualTo(ExpiryData.NEVER);
    }

    @Test
    public void testUpdate() throws Exception {
        assertThat(expiryData.created).isEqualTo(TimeSource.GENESIS);
        clock.time = 2L;
        assertThat(expiryData.update().updated).isEqualTo(2L);
        assertThat(expiryData.accessed).isEqualTo(ExpiryData.NEVER);
    }

    private class TimeSource {
        static final long GENESIS = 0L;
        long time = GENESIS;

        Long get() {
            return time;
        }
    }
}