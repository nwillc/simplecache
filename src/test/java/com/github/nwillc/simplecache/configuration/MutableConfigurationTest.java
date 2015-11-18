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

package com.github.nwillc.simplecache.configuration;

import org.junit.Test;

import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.expiry.ExpiryPolicy;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class MutableConfigurationTest {
    @Test
    public void shouldHaveDefaultExpiry() throws Exception {
        MutableConfiguration<Long, String> configuration = new MutableConfiguration<>();
        assertThat(configuration).isNotNull();
        Factory<ExpiryPolicy> expiryPolicyFactory = configuration.getExpiryPolicyFactory();
        assertThat(expiryPolicyFactory).isNotNull();
        ExpiryPolicy expiryPolicy = expiryPolicyFactory.create();
        assertThat(expiryPolicy).isNotNull();
        assertThat(expiryPolicy).isInstanceOf(EternalExpiryPolicy.class);
    }
}
