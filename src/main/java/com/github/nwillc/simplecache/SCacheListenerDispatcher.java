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

import com.github.nwillc.simplecache.event.SCacheEntryEvent;

import javax.cache.Cache;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.event.CacheEntryCreatedListener;
import javax.cache.event.CacheEntryEvent;
import javax.cache.event.CacheEntryExpiredListener;
import javax.cache.event.CacheEntryListener;
import javax.cache.event.CacheEntryRemovedListener;
import javax.cache.event.CacheEntryUpdatedListener;
import javax.cache.event.EventType;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * The event listening code needed by the Cache is largely found here.
 *
 * @param <K> key type
 * @param <V> value type
 */
class SCacheListenerDispatcher<K, V> implements SListenerList<K, V> {
    private static final long DISPATCH_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(1) / 2;
    private final EnumMap<EventType, Deque<CacheEntryEvent>> eventMap = new EnumMap<>(EventType.class);
    private final Set<Listener<K, V>> listeners = new HashSet<>();
    private final Cache cache;

    @SuppressWarnings("unchecked")
    public SCacheListenerDispatcher(Cache<K, V> cache) {
        this.cache = cache;
        CompleteConfiguration<K, V> configuration = cache.getConfiguration(CompleteConfiguration.class);
        Iterable<CacheEntryListenerConfiguration<K, V>> listenerConfigurations = configuration.getCacheEntryListenerConfigurations();
        listenerConfigurations.forEach(this::registerCacheEntryListener);

        new Timer(this.getClass().getSimpleName(), false).schedule(new TimerTask() {
            @Override
            public void run() {
                dispatch();
            }
        }, DISPATCH_PERIOD_MILLIS, DISPATCH_PERIOD_MILLIS);
    }

    // Visible for testing
    static <E> List<E> transfer(Deque<E> deque) {
        List<E> list = new ArrayList<>(deque.size() + 10);

        E element;
        while ((element = deque.pollLast()) != null) {
            list.add(element);
        }
        return list;
    }

    // Visible for testing
    static EventType typeOf(CacheEntryListener cacheEntryListener) {
        if (cacheEntryListener instanceof CacheEntryCreatedListener) {
            return EventType.CREATED;
        } else if (cacheEntryListener instanceof CacheEntryExpiredListener) {
            return EventType.EXPIRED;
        } else if (cacheEntryListener instanceof CacheEntryRemovedListener) {
            return EventType.REMOVED;
        } else if (cacheEntryListener instanceof CacheEntryUpdatedListener) {
            return EventType.UPDATED;
        }

        throw new IllegalArgumentException("Unknown CacheEntryListener subclass: " + cacheEntryListener.getClass().getSimpleName());
    }

    // Visible for testing
    @SuppressWarnings("unchecked")
    static Consumer<Iterable<CacheEntryEvent>> toConsumer(EventType eventType, CacheEntryListener listener) {
        switch (eventType) {
            case CREATED:
                return ((CacheEntryCreatedListener) listener)::onCreated;
            case EXPIRED:
                return ((CacheEntryExpiredListener) listener)::onExpired;
            case REMOVED:
                return ((CacheEntryRemovedListener) listener)::onRemoved;
            case UPDATED:
                return ((CacheEntryUpdatedListener) listener)::onUpdated;
            default:
                return null;
        }
    }

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        Listener<K, V> listener = new Listener<>(cacheEntryListenerConfiguration);
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException("Attempting to register same listener twice");
        }
        CacheEntryListener<? super K, ? super V> cacheEntryListener = cacheEntryListenerConfiguration.getCacheEntryListenerFactory().create();
        EventType eventType = SCacheListenerDispatcher.typeOf(cacheEntryListener);
        listener.type = eventType;
        listener.consumer = SCacheListenerDispatcher.toConsumer(eventType, cacheEntryListener);
        listeners.add(listener);
        if (!eventMap.containsKey(eventType)) {
            eventMap.put(eventType, new ConcurrentLinkedDeque<>());
        }
    }

    @Override
    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        Listener<K, V> listener = new Listener<>(cacheEntryListenerConfiguration);
        listeners.remove(listener);
    }

    public void event(EventType type, K key, V value, V old) {
        Deque<CacheEntryEvent> events = eventMap.get(type);
        if (events != null) {
            events.addFirst(new SCacheEntryEvent<>(cache, type, key, value, old));
        }
    }

    private void dispatch() {
        final EnumMap<EventType, List<CacheEntryEvent>> snapshot = new EnumMap<>(EventType.class);
        eventMap.keySet().forEach(eventType -> snapshot.put(eventType, transfer(eventMap.get(eventType))));

        listeners.forEach(l -> {
            List<CacheEntryEvent> list = snapshot.get(l.type);
            if (list != null && list.size() > 0) {
                l.consumer.accept(list);
            }
        });
    }

    // Visible for testing
    static class Listener<K2, V2> {
        final CacheEntryListenerConfiguration<K2, V2> configuration;
        Consumer<Iterable<CacheEntryEvent>> consumer;
        EventType type;

        public Listener(CacheEntryListenerConfiguration<K2, V2> configuration) {
            this.configuration = configuration;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Listener<?, ?> listener = (Listener<?, ?>) o;

            return configuration.equals(listener.configuration);

        }

        @Override
        public int hashCode() {
            return configuration.hashCode();
        }
    }
}
