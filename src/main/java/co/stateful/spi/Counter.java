/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.spi;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Counter.
 *
 * @since 0.1
 */
@Immutable
public interface Counter {

    /**
     * Set specific value.
     * @param value Value to set
     * @throws IOException If fails due to IO problem
     */
    void set(BigDecimal value) throws IOException;

    /**
     * Add value to it.
     * @param delta Delta to add
     * @return New value
     * @throws IOException If fails due to IO problem
     */
    BigDecimal increment(BigDecimal delta) throws IOException;

}
