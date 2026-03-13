/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.fake;

import co.stateful.spi.Counter;
import co.stateful.spi.Counters;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Fake counters for testing.
 *
 * <p>In-memory counters collection.
 * Usage example:
 * <pre>{@code
 * Counters counters = new FkCounters();
 * counters.create("test");
 * Counter counter = counters.get("test");
 * }</pre>
 *
 * @since 2.0
 */
public final class FkCounters implements Counters {

    /**
     * Counters map.
     */
    private final ConcurrentMap<String, Counter> map;

    /**
     * Ctor.
     */
    public FkCounters() {
        this.map = new ConcurrentHashMap<>(16);
    }

    @Override
    public Iterable<String> names() {
        return this.map.keySet();
    }

    @Override
    public void create(final String name) {
        this.map.put(name, new FkCounter());
    }

    @Override
    public void delete(final String name) {
        this.map.remove(name);
    }

    @Override
    public Counter get(final String name) {
        return this.map.get(name);
    }
}
