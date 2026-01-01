/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.quota;

import co.stateful.spi.Counters;
import co.stateful.spi.Locks;
import co.stateful.spi.User;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Quota on a User.
 *
 * @since 1.4
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "origin", "quota" })
@Loggable(Loggable.DEBUG)
public final class QtUser implements User {

    /**
     * Original object.
     */
    private final transient User origin;

    /**
     * Quota to use.
     */
    private final transient Quota quota;

    /**
     * Ctor.
     * @param org Original object
     * @param qta Quota
     */
    public QtUser(final User org, final Quota qta) {
        this.origin = org;
        this.quota = qta;
    }

    @Override
    public boolean exists() {
        return this.origin.exists();
    }

    @Override
    public String token() throws IOException {
        return this.origin.token();
    }

    @Override
    public void refresh() throws IOException {
        this.origin.refresh();
    }

    @Override
    public Counters counters() {
        return new QtCounters(this.origin.counters(), this.quota.into("c"));
    }

    @Override
    public Locks locks() {
        return new QtLocks(this.origin.locks(), this.quota.into("k"));
    }
}
