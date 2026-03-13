/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.fake;

import co.stateful.spi.Counter;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fake counter for testing.
 *
 * <p>In-memory counter that stores a single value.
 * Usage example:
 * <pre>{@code
 * Counter counter = new FkCounter();
 * counter.set(BigDecimal.TEN);
 * counter.increment(BigDecimal.ONE);
 * }</pre>
 *
 * @since 2.0
 */
public final class FkCounter implements Counter {

    /**
     * Value.
     */
    private final AtomicReference<BigDecimal> value;

    /**
     * Ctor.
     */
    public FkCounter() {
        this(BigDecimal.ZERO);
    }

    /**
     * Ctor.
     * @param initial Initial value
     */
    public FkCounter(final BigDecimal initial) {
        this.value = new AtomicReference<>(initial);
    }

    @Override
    public void set(final BigDecimal val) {
        this.value.set(val);
    }

    @Override
    public BigDecimal increment(final BigDecimal delta) {
        return this.value.updateAndGet(current -> current.add(delta));
    }
}
