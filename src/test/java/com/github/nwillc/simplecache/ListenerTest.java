/*
 * Copyright 2019 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.simplecache;

import com.github.nwillc.contracts.EqualsContract;
import org.junit.Before;

import javax.cache.configuration.CacheEntryListenerConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
public class ListenerTest extends EqualsContract<SCacheListenerDispatcher.Listener> {
	private CacheEntryListenerConfiguration configuration1;
	private CacheEntryListenerConfiguration configuration2;

	@Before
	public void setUp() throws Exception {
		configuration1 = mock(CacheEntryListenerConfiguration.class);
		configuration2 = mock(CacheEntryListenerConfiguration.class);
	}

    @Override
    protected List<SCacheListenerDispatcher.Listener> getEquals() {
        List<SCacheListenerDispatcher.Listener> instances = new ArrayList<>();
        instances.add(new SCacheListenerDispatcher.Listener(configuration1));
        instances.add(new SCacheListenerDispatcher.Listener(configuration1));
        return instances;
    }

    @Override
    protected List<SCacheListenerDispatcher.Listener> getNotEquals() {
        List<SCacheListenerDispatcher.Listener> instances = new ArrayList<>();
        instances.add(new SCacheListenerDispatcher.Listener(configuration1));
        instances.add(new SCacheListenerDispatcher.Listener(configuration2));
        instances.add(new SubListener(configuration1));
        return instances;
    }

    private class SubListener extends SCacheListenerDispatcher.Listener {
        public SubListener(CacheEntryListenerConfiguration configuration) {
            super(configuration);
        }
    }
}
