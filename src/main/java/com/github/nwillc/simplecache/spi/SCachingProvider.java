/*
 * Copyright 2019 nwillc@gmail.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without fee is hereby granted, provided that the above copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.github.nwillc.simplecache.spi;

import com.github.nwillc.simplecache.SCacheManager;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.OptionalFeature;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.WeakHashMap;

public class SCachingProvider implements CachingProvider {
	private final WeakHashMap<CMKey, CacheManager> cacheManagers;
	private final Properties properties = new Properties();

	public SCachingProvider() {
		cacheManagers = new WeakHashMap<>();
	}

	@Override
	public synchronized CacheManager getCacheManager(URI uri, ClassLoader classLoader, Properties properties) {
		URI managerUri = uri == null ? getDefaultURI() : uri;
		ClassLoader managerClassLoader = classLoader == null ? getDefaultClassLoader() : classLoader;
		Properties managerProrperties = properties == null ? getDefaultProperties() : properties;

		CMKey key = new CMKey(managerClassLoader, managerUri, managerProrperties);
		CacheManager cacheManager = cacheManagers.get(key);
		if (cacheManager == null || cacheManager.isClosed()) {
			try {
				Class<?> cmClass = managerClassLoader.loadClass(SCacheManager.class.getCanonicalName());
				cacheManager = (CacheManager) cmClass.getDeclaredConstructor(SCachingProvider.class, Properties.class).newInstance(this, managerProrperties);
			} catch (Exception e) {
				throw new CacheException("ClassLoader can not load SCacheManager class.", e);
			}

			cacheManagers.put(key, cacheManager);
		}

		return cacheManager;
	}

	@Override
	public ClassLoader getDefaultClassLoader() {
		return getClass().getClassLoader();
	}

	@Override
	public URI getDefaultURI() {
		try {
			return new URI(this.getClass().getName());
		} catch (URISyntaxException e) {
			throw new CacheException("Failed to create the default URI", e);
		}
	}

	@Override
	public Properties getDefaultProperties() {
		return properties;
	}

	@Override
	public CacheManager getCacheManager(URI uri, ClassLoader classLoader) {
		return getCacheManager(uri, classLoader, null);
	}

	@Override
	public CacheManager getCacheManager() {
		return getCacheManager(null, null, null);
	}

	@Override
	public void close() {
		cacheManagers.values().forEach(CacheManager::close);
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

	// Exposed for testing
	static class CMKey {
		private final ClassLoader classLoader;
		private final URI uri;
		private final Properties properties;

		public CMKey(ClassLoader classLoader, URI uri, Properties properties) {
			this.classLoader = classLoader;
			this.uri = uri;
			this.properties = properties;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			CMKey cmKey = (CMKey) o;

			return classLoader.equals(cmKey.classLoader) &&
					uri.equals(cmKey.uri) &&
					properties.equals(cmKey.properties);

		}

		@Override
		public int hashCode() {
			int result = classLoader.hashCode();
			result = 31 * result + uri.hashCode();
			result = 31 * result + properties.hashCode();
			return result;
		}
	}
}
