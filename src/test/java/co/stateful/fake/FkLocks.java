/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.fake;

import co.stateful.spi.Locks;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Fake locks for testing.
 *
 * <p>In-memory locks collection.
 * Usage example:
 * <pre>{@code
 * Locks locks = new FkLocks();
 * locks.lock("test", "label");
 * locks.unlock("test");
 * }</pre>
 *
 * @since 2.0
 */
public final class FkLocks implements Locks {

    /**
     * Locks map.
     */
    private final ConcurrentMap<String, String> map;

    /**
     * Ctor.
     */
    public FkLocks() {
        this.map = new ConcurrentHashMap<>(16);
    }

    @Override
    public Map<String, String> names() {
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public String lock(final String name, final String label) {
        final String existing = this.map.putIfAbsent(name, label);
        final String result;
        if (existing == null) {
            result = "";
        } else {
            result = existing;
        }
        return result;
    }

    @Override
    public String label(final String name) {
        final String value = this.map.get(name);
        final String result;
        if (value == null) {
            result = "";
        } else {
            result = value;
        }
        return result;
    }

    @Override
    public void unlock(final String name) {
        this.map.remove(name);
    }

    @Override
    public String unlock(final String name, final String label) {
        final String current = this.map.get(name);
        final String result;
        if (current == null) {
            result = "";
        } else if (current.equals(label)) {
            this.map.remove(name);
            result = "";
        } else {
            result = current;
        }
        return result;
    }
}
