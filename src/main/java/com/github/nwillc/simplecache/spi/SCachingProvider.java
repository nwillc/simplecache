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
import java.net.URI;
import java.util.Properties;

public class SCachingProvider implements javax.cache.spi.CachingProvider {
    private final Properties properties = new Properties();

    public SCachingProvider() {
    }

    @Override
    public javax.cache.CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
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
        return new SCacheManager();
    }

    @Override
    public void close() {

    }

    @Override
    public void close(ClassLoader classLoader) {

    }

    @Override
    public void close(URI uri, ClassLoader classLoader) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSupported(OptionalFeature optionalFeature) {
        return false;
    }
}
