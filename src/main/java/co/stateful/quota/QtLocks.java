/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.quota;

import co.stateful.spi.Locks;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Quota on Locks.
 *
 * @since 1.4
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "origin", "quota" })
@Loggable(Loggable.DEBUG)
public final class QtLocks implements Locks {

    /**
     * Original object.
     */
    private final transient Locks origin;

    /**
     * Quota to use.
     */
    private final transient Quota quota;

    /**
     * Ctor.
     * @param org Original object
     * @param qta Quota
     */
    public QtLocks(final Locks org, final Quota qta) {
        this.origin = org;
        this.quota = qta;
    }

    @Override
    public Map<String, String> names() throws IOException {
        this.quota.use("names");
        return this.origin.names();
    }

    @Override
    public String lock(final String name, final String label)
        throws IOException {
        this.quota.use("lock");
        return this.origin.lock(name, label);
    }

    @Override
    public String label(final String name) throws IOException {
        this.quota.use("label");
        return this.origin.label(name);
    }

    @Override
    public void unlock(final String name) throws IOException {
        this.quota.use("unlock");
        this.origin.unlock(name);
    }

    @Override
    public String unlock(final String name, final String label)
        throws IOException {
        this.quota.use("unlock-if");
        return this.origin.unlock(name, label);
    }
}
