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

import org.junit.Before;
import org.junit.Test;

import javax.cache.integration.CacheLoader;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.data.MapEntry.entry;

public class SCacheLoaderTest {

    private SCacheLoader<Long,String> loader = new SCacheLoader<>(Object::toString);

    @Before
    public void setUp() throws Exception {
        assertThat(loader).isInstanceOf(CacheLoader.class);
    }

    @Test
    public void testLoad() throws Exception {
        assertThat(loader.load(0L)).isEqualTo("0");
    }

    @Test
    public void testLoadAll() throws Exception {
        List<Long> keys = new ArrayList<>();
        keys.add(0L);
        keys.add(1L);
        assertThat(loader.loadAll(keys)).containsExactly(entry(0L,"0"), entry(1L, "1"));
    }
}