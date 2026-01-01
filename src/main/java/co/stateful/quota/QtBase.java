/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.quota;

import co.stateful.spi.Base;
import co.stateful.spi.User;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.urn.URN;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Quota on a Base.
 *
 * @since 1.4
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "origin", "quota" })
@Loggable(Loggable.DEBUG)
public final class QtBase implements Base {

    /**
     * Original object.
     */
    private final transient Base origin;

    /**
     * Quota.
     */
    private final transient Quota quota;

    /**
     * Ctor.
     * @param org Original object
     * @param qta Quota
     */
    public QtBase(final Base org, final Quota qta) {
        this.origin = org;
        this.quota = qta;
    }

    @Override
    public User user(final URN urn) {
        return new QtUser(
            this.origin.user(urn),
            this.quota.into(urn.toString())
        );
    }
}
