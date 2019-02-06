/*
 * Copyright 2019 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.simplecache;

import org.junit.Before;
import org.junit.Test;

import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.expiry.ModifiedExpiryPolicy;
import javax.cache.expiry.TouchedExpiryPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SExpiryDataTest {
	private AtomicLong clock;
	private SExpiryData expiryData;

	@Before
	public void setUp() throws Exception {
		clock = new AtomicLong(0L);
		expiryData = new SExpiryData(clock::get);
	}

	@Test
	public void testCreated() throws Exception {
		clock.set(5L);
		expiryData = new SExpiryData(clock::get);
		assertThat(expiryData.created).isEqualTo(5L);
		assertThat(expiryData.accessed).isEqualTo(SExpiryData.NEVER);
		assertThat(expiryData.updated).isEqualTo(SExpiryData.NEVER);
	}

	@Test
	public void testAccess() throws Exception {
		assertThat(expiryData.created).isEqualTo(0L);
		clock.set(2L);
		assertThat(expiryData.access().accessed).isEqualTo(2L);
		assertThat(expiryData.updated).isEqualTo(SExpiryData.NEVER);
	}

	@Test
	public void testUpdate() throws Exception {
		assertThat(expiryData.created).isEqualTo(0L);
		clock.set(2L);
		assertThat(expiryData.update().updated).isEqualTo(2L);
		assertThat(expiryData.accessed).isEqualTo(SExpiryData.NEVER);
	}

	@Test
	public void testEternalExpiration() throws Exception {
		expiryData = new SExpiryData(clock::get, new EternalExpiryPolicy());
		clock.set(TimeUnit.DAYS.toNanos(1000));
		assertThat(expiryData.expired()).isFalse();
	}

	@Test
	public void testCreationExpiration() throws Exception {
		ExpiryPolicy expiryPolicy = new CreatedExpiryPolicy(Duration.ONE_MINUTE);
		expiryData = new SExpiryData(clock::get, expiryPolicy);
		clock.set(TimeUnit.SECONDS.toNanos(30));
		assertThat(expiryData.expired()).isFalse();
		clock.set(TimeUnit.MINUTES.toNanos(2));
		assertThat(expiryData.expired()).isTrue();
	}

	@Test
	public void testAccessExpiration() throws Exception {
		ExpiryPolicy expiryPolicy = new AccessedExpiryPolicy(Duration.ONE_MINUTE);
		expiryData = new SExpiryData(clock::get, expiryPolicy);
		clock.set(0L);
		expiryData.access();
		assertThat(expiryData.expired()).isFalse();
		clock.set(TimeUnit.MINUTES.toNanos(2));
		assertThat(expiryData.expired()).isTrue();
	}

	@Test
	public void testModifiedExpirationCreation() throws Exception {
		ExpiryPolicy expiryPolicy = new ModifiedExpiryPolicy(Duration.ONE_MINUTE);
		expiryData = new SExpiryData(clock::get, expiryPolicy);
		clock.set(TimeUnit.SECONDS.toNanos(30));
		assertThat(expiryData.expired()).isFalse();
		clock.set(TimeUnit.MINUTES.toNanos(2));
		assertThat(expiryData.expired()).isTrue();
	}

	@Test
	public void testModifiedExpirationUpdate() throws Exception {
		ExpiryPolicy expiryPolicy = new ModifiedExpiryPolicy(Duration.ONE_MINUTE);
		expiryData = new SExpiryData(clock::get, expiryPolicy);
		clock.set(TimeUnit.SECONDS.toNanos(30));
		expiryData.update();
		assertThat(expiryData.expired()).isFalse();
		clock.set(TimeUnit.MINUTES.toNanos(2));
		assertThat(expiryData.expired()).isTrue();
		expiryData.update();
		assertThat(expiryData.expired()).isFalse();
	}

	@Test
	public void testTouchedExpirationCreation() throws Exception {
		ExpiryPolicy expiryPolicy = new TouchedExpiryPolicy(Duration.ONE_MINUTE);
		expiryData = new SExpiryData(clock::get, expiryPolicy);
		clock.set(TimeUnit.SECONDS.toNanos(30));
		assertThat(expiryData.expired()).isFalse();
		clock.set(TimeUnit.MINUTES.toNanos(2));
		assertThat(expiryData.expired()).isTrue();
	}

	@Test
	public void testTouchedExpirationUpdate() throws Exception {
		ExpiryPolicy expiryPolicy = new TouchedExpiryPolicy(Duration.ONE_MINUTE);
		expiryData = new SExpiryData(clock::get, expiryPolicy);
		clock.set(TimeUnit.SECONDS.toNanos(30));
		expiryData.update();
		assertThat(expiryData.expired()).isFalse();
		clock.set(TimeUnit.MINUTES.toNanos(2));
		assertThat(expiryData.expired()).isTrue();
		expiryData.update();
		assertThat(expiryData.expired()).isFalse();
	}

	@Test
	public void testTouchedExpirationAccess() throws Exception {
		ExpiryPolicy expiryPolicy = new TouchedExpiryPolicy(Duration.ONE_MINUTE);
		expiryData = new SExpiryData(clock::get, expiryPolicy);
		clock.set(TimeUnit.SECONDS.toNanos(30));
		expiryData.access();
		assertThat(expiryData.expired()).isFalse();
		clock.set(TimeUnit.MINUTES.toNanos(2));
		assertThat(expiryData.expired()).isTrue();
		expiryData.access();
		assertThat(expiryData.expired()).isFalse();
	}

}
