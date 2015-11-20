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
import javax.cache.event.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

class SCacheListenerDispatcher<K, V> {
    private static final long DISPATCH_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(1) / 2;
    private final EnumMap<EventType, List<CacheEntryEvent>> eventMap = new EnumMap<>(EventType.class);
    private final EnumMap<EventType, List<Consumer<Iterable<CacheEntryEvent>>>> listenersMap = new EnumMap<>(EventType.class);
    private final Cache cache;

    @SuppressWarnings("unchecked")
    public SCacheListenerDispatcher(Cache<K, V> cache) {
        this.cache = cache;
        CompleteConfiguration<K,V> configuration = cache.getConfiguration(CompleteConfiguration.class);
        Iterable<CacheEntryListenerConfiguration<K,V>> listenerConfigurations = configuration.getCacheEntryListenerConfigurations();
        listenerConfigurations.forEach(c -> {
            CacheEntryListener<? super K, ? super V> cacheEntryListener = c.getCacheEntryListenerFactory().create();
            ListenerEventType listenerEventType = null;

            // Hate this cascading if, but the only way to identify the the listener type is by the interface it implements!
            if (cacheEntryListener instanceof CacheEntryCreatedListener) {
                listenerEventType = ListenerEventType.CREATED;
            } else if (cacheEntryListener instanceof CacheEntryExpiredListener) {
                listenerEventType = ListenerEventType.EXPIRED;
            } else if (cacheEntryListener instanceof CacheEntryRemovedListener) {
                listenerEventType = ListenerEventType.REMOVED;
            } else if (cacheEntryListener instanceof CacheEntryUpdatedListener) {
                listenerEventType = ListenerEventType.UPDATED;
            }

            if (listenerEventType != null) {
                if (!listenersMap.containsKey(listenerEventType.eventType)) {
                    listenersMap.put(listenerEventType.eventType, new ArrayList<>());
                    eventMap.put(listenerEventType.eventType, new ArrayList<>());
                }

                listenersMap.get(listenerEventType.eventType).add(listenerEventType.toConsumer(cacheEntryListener));
            }
        });

        if (listenersMap.size() > 0) {
            new Timer(this.getClass().getSimpleName(),false).schedule(new TimerTask() {
                @Override
                public void run() {
                    dispatch();
                }
            }, DISPATCH_PERIOD_MILLIS, DISPATCH_PERIOD_MILLIS);
        }
    }

    public void event(EventType type, K key, V value, V old) {
        List<CacheEntryEvent> events = eventMap.get(type);
        if (events != null) {
            events.add(new SCacheEntryEvent<>(cache, type, key, value, old));
        }
    }

    private void dispatch() {
        listenersMap.entrySet().forEach(kv -> {
            List<CacheEntryEvent> cacheEntryEvents = eventMap.get(kv.getKey());
            if (cacheEntryEvents.size() > 0) {
                kv.getValue().forEach(c -> c.accept(cacheEntryEvents));
                cacheEntryEvents.clear();
            }
        });
    }

    // Once we know the type, lets make the listeners polymorphic
    @SuppressWarnings("unchecked")
    enum ListenerEventType {
        CREATED(EventType.CREATED) {
            @Override
            Consumer<Iterable<CacheEntryEvent>> toConsumer(CacheEntryListener listener) {
                    return ((CacheEntryCreatedListener) listener)::onCreated;
            }
        },
        EXPIRED(EventType.EXPIRED) {
            @Override
            Consumer<Iterable<CacheEntryEvent>> toConsumer(CacheEntryListener listener) {
                    return ((CacheEntryExpiredListener) listener)::onExpired;
            }
        },
        REMOVED(EventType.REMOVED) {
            @Override
            Consumer<Iterable<CacheEntryEvent>> toConsumer(CacheEntryListener listener) {
                return ((CacheEntryRemovedListener) listener)::onRemoved;
            }
        },
        UPDATED(EventType.UPDATED) {
            @Override
            Consumer<Iterable<CacheEntryEvent>> toConsumer(CacheEntryListener listener) {
                return ((CacheEntryUpdatedListener) listener)::onUpdated;
            }
        };

        final EventType eventType;

        abstract Consumer<Iterable<CacheEntryEvent>> toConsumer(CacheEntryListener listener);

        ListenerEventType(EventType eventType) {
            this.eventType = eventType;
        }
    }
}
