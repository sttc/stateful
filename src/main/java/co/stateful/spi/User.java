/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.spi;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * User.
 *
 * @since 0.1
 */
@Immutable
public interface User {

    /**
     * This user exists.
     * @return TRUE if this user logged in at least once through UI
     */
    boolean exists();

    /**
     * Get his security token.
     * @return Token
     * @throws IOException If fails due to IO problem
     */
    String token() throws IOException;

    /**
     * Refresh the token.
     * @throws IOException If fails due to IO problem
     */
    void refresh() throws IOException;

    /**
     * Get his counters.
     * @return Counters
     */
    Counters counters();

    /**
     * Get his locks.
     * @return Locks
     * @since 1.1
     */
    Locks locks();

}
