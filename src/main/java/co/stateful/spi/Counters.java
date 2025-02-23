/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.spi;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Counters.
 *
 * @since 0.1
 */
@Immutable
public interface Counters {

    /**
     * Maximum allowed per account.
     */
    int MAX = 64;

    /**
     * Get list of them all.
     * @return List of counter names
     * @throws IOException If fails
     */
    Iterable<String> names() throws IOException;

    /**
     * Create a counter.
     * @param name Name of it
     * @throws IOException If fails due to IO problem
     */
    void create(String name) throws IOException;

    /**
     * Delete a counter.
     * @param name Name of it
     * @throws IOException If fails
     */
    void delete(String name) throws IOException;

    /**
     * Get one counter by name.
     * @param name Name of it
     * @return Counter
     */
    Counter get(String name);

}
