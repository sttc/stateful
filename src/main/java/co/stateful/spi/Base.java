/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.spi;

import com.jcabi.aspects.Immutable;
import com.jcabi.urn.URN;

/**
 * Base.
 *
 * @since 0.1
 */
@Immutable
public interface Base {

    /**
     * Get one user.
     * @param urn URN of the user
     * @return User
     */
    User user(URN urn);

}
