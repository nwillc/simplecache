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

package com.github.nwillc.simplecache.spi;

import com.github.nwillc.simplecache.SCacheManager;

import javax.cache.CacheManager;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class SCachingProvider implements CachingProvider {
    private final Properties properties = new Properties();
    private final AtomicReference<CacheManager> cacheManager = new AtomicReference<>();

    public SCachingProvider() {
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ClassLoader getDefaultClassLoader() {
        return ClassLoader.getSystemClassLoader();
    }

    @Override
    public URI getDefaultURI() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Properties getDefaultProperties() {
        return properties;
    }

    @Override
    public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CacheManager getCacheManager() {

        if (cacheManager.get() == null || cacheManager.get().isClosed()) {
            cacheManager.set(new SCacheManager(this));
        }

        return cacheManager.get();
    }

    @Override
    public void close() {
        CacheManager cm = cacheManager.getAndSet(null);
        if (cm != null) {
            cm.close();
        }
    }

    @Override
    public void close(ClassLoader classLoader) {
        close();
    }

    @Override
    public void close(URI uri, ClassLoader classLoader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSupported(OptionalFeature optionalFeature) {
        return false;
    }

    // for testing only
    void setCacheManager(CacheManager cm) {
        cacheManager.set(cm);
    }

    public void removeCacheManager(SCacheManager cm) {
        cacheManager.compareAndSet(cm, null);
    }
}
