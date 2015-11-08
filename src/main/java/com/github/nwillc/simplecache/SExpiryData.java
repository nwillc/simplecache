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

import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import java.util.function.Supplier;

class SExpiryData {
    public static final long NEVER = -1L;
    private final Supplier<Long> timeSource;
    private final ExpiryPolicy expiryPolicy;
    private final ExpiredTest expiredTest;
    long created = NEVER;
    long accessed = NEVER;
    long updated = NEVER;

    SExpiryData() {
        this(System::currentTimeMillis);
    }

    public SExpiryData(Supplier<Long> timeSource) {
        this(timeSource, new EternalExpiryPolicy());
    }

    SExpiryData(Supplier<Long> timeSource, ExpiryPolicy expiryPolicy) {
        this.timeSource = timeSource;
        this.expiryPolicy = expiryPolicy;
        expiredTest = ExpiredTest.valueOf(expiryPolicy.getClass().getSimpleName());
        created = timeSource.get();
    }

    SExpiryData access() {
        accessed = timeSource.get();
        return this;
    }

    SExpiryData update() {
        updated = timeSource.get();
        return this;
    }

    boolean expired() {
        return expiredTest.test(this);
    }

    enum ExpiredTest {
        EternalExpiryPolicy {
            @Override
            boolean test(SExpiryData expiryData) {
                return false;
            }
        },
        CreatedExpiryPolicy {
            @Override
            boolean test(SExpiryData expiryData) {
                return test(expiryData.timeSource.get(), expiryData.expiryPolicy.getExpiryForCreation(), expiryData.created);
            }
        },
        AccessedExpiryPolicy {
            @Override
            boolean test(SExpiryData expiryData) {
                return test(expiryData.timeSource.get(), expiryData.expiryPolicy.getExpiryForAccess(), expiryData.accessed);
            }
        },
        ModifiedExpiryPolicy {
            @Override
            boolean test(SExpiryData expiryData) {
                return test(expiryData.timeSource.get(), expiryData.expiryPolicy.getExpiryForUpdate(), expiryData.created, expiryData.updated);
            }
        },
        TouchedExpiryPolicy {
            @Override
            boolean test(SExpiryData expiryData) {
                return test(expiryData.timeSource.get(), expiryData.expiryPolicy.getExpiryForCreation(), expiryData.created, expiryData.updated, expiryData.accessed);
            }
        };

        abstract boolean test(SExpiryData expiryData);

        boolean test(long now, Duration duration, long ... times) {
            long last = 0L;
            for (long time : times) {
                if (time > last) {
                    last = time;
                }
            }
            long millisAgo = now - last;
            return millisAgo > duration.getTimeUnit().toMillis(duration.getDurationAmount());
        }

    }
}