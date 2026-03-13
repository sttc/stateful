/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.fake;

import co.stateful.spi.Base;
import co.stateful.spi.User;
import com.jcabi.urn.URN;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Fake base for testing.
 *
 * <p>In-memory base with fake users.
 * Usage example:
 * <pre>{@code
 * Base base = new FkBase();
 * User user = base.user(URN.create("urn:test:1"));
 * }</pre>
 *
 * @since 2.0
 */
public final class FkBase implements Base {

    /**
     * Users map.
     */
    private final ConcurrentMap<URN, User> map;

    /**
     * Ctor.
     */
    public FkBase() {
        this.map = new ConcurrentHashMap<>(16);
    }

    @Override
    public User user(final URN urn) {
        return this.map.computeIfAbsent(urn, u -> new FkUser());
    }
}
