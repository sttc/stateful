/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.quota;

import co.stateful.spi.Counter;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Quota on counter.
 *
 * @since 1.4
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "origin", "quota" })
@Loggable(Loggable.DEBUG)
public final class QtCounter implements Counter {

    /**
     * Original object.
     */
    private final transient Counter origin;

    /**
     * Quota to use.
     */
    private final transient Quota quota;

    /**
     * Ctor.
     * @param org Original object
     * @param qta Quota
     */
    public QtCounter(final Counter org, final Quota qta) {
        this.origin = org;
        this.quota = qta;
    }

    @Override
    public void set(final BigDecimal value) throws IOException {
        this.quota.use("set");
        this.origin.set(value);
    }

    @Override
    public BigDecimal increment(final BigDecimal delta) throws IOException {
        this.quota.use("inc");
        return this.origin.increment(delta);
    }
}
