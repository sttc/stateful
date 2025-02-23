/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.core;

import co.stateful.spi.Base;
import co.stateful.spi.User;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.urn.URN;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Default base.
 * @since 0.1
 */
@Immutable
@ToString
@EqualsAndHashCode
@Loggable(Loggable.DEBUG)
public final class DefaultBase implements Base {

    @Override
    public User user(final URN urn) {
        return new DefaultUser(urn);
    }
}
