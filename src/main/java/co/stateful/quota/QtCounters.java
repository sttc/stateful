/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.quota;

import co.stateful.spi.Counter;
import co.stateful.spi.Counters;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Quota on counters.
 *
 * @since 1.4
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "origin", "quota" })
@Loggable(Loggable.DEBUG)
public final class QtCounters implements Counters {

    /**
     * Original object.
     */
    private final transient Counters origin;

    /**
     * Quota to use.
     */
    private final transient Quota quota;

    /**
     * Ctor.
     * @param org Original object
     * @param qta Quota
     */
    public QtCounters(final Counters org, final Quota qta) {
        this.origin = org;
        this.quota = qta;
    }

    @Override
    public Iterable<String> names() throws IOException {
        this.quota.use("names");
        return this.origin.names();
    }

    @Override
    public void create(final String name) throws IOException {
        this.quota.use("create");
        this.origin.create(name);
    }

    @Override
    public void delete(final String name) throws IOException {
        this.quota.use("delete");
        this.origin.delete(name);
    }

    @Override
    public Counter get(final String name) {
        return new QtCounter(this.origin.get(name), this.quota.into(name));
    }
}
