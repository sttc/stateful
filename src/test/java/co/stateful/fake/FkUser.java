/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.fake;

import co.stateful.spi.Counters;
import co.stateful.spi.Locks;
import co.stateful.spi.User;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fake user for testing.
 *
 * <p>In-memory user with fake counters and locks.
 * Usage example:
 * <pre>{@code
 * User user = new FkUser();
 * user.counters().create("test");
 * }</pre>
 *
 * @since 2.0
 */
public final class FkUser implements User {

    /**
     * Token.
     */
    private final AtomicReference<String> tkn;

    /**
     * Counters.
     */
    private final Counters ctrs;

    /**
     * Locks.
     */
    private final Locks lcks;

    /**
     * Ctor.
     */
    public FkUser() {
        this(new FkCounters(), new FkLocks());
    }

    /**
     * Ctor.
     * @param counters Counters
     * @param locks Locks
     */
    public FkUser(final Counters counters, final Locks locks) {
        this.tkn = new AtomicReference<>(UUID.randomUUID().toString());
        this.ctrs = counters;
        this.lcks = locks;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public String token() {
        return this.tkn.get();
    }

    @Override
    public void refresh() {
        this.tkn.set(UUID.randomUUID().toString());
    }

    @Override
    public Counters counters() {
        return this.ctrs;
    }

    @Override
    public Locks locks() {
        return this.lcks;
    }
}
