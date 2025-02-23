/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.spi;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;

/**
 * Locks.
 *
 * @since 1.1
 */
@Immutable
public interface Locks {

    /**
     * Maximum allowed per account.
     */
    int MAX = 4096;

    /**
     * Get list of them all, and their labels.
     * @return List of locks
     * @throws IOException If fails
     */
    Map<String, String> names() throws IOException;

    /**
     * Lock it.
     * @param name Unique name of the lock
     * @param label Label to attach
     * @return Empty if success or a label of a current lock
     * @throws IOException If fails
     */
    String lock(String name, String label) throws IOException;

    /**
     * Read label.
     * @param name Unique name of the lock
     * @return Empty if it doesn't exist, or a label
     * @throws IOException If fails
     */
    String label(String name) throws IOException;

    /**
     * Unlock it.
     * @param name Unique name of the lock
     * @throws IOException If fails
     */
    void unlock(String name) throws IOException;

    /**
     * Unlock only if label matches.
     * @param name Unique name of the lock
     * @param label Label to match
     * @return Empty if success or label of current lock
     * @throws IOException If fails
     * @since 1.6
     */
    String unlock(String name, String label) throws IOException;

}
