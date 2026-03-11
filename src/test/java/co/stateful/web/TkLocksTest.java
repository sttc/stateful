/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.core.DefaultBase;
import co.stateful.spi.Base;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TkLocks}.
 *
 * @since 2.0
 */
final class TkLocksTest {

    @Test
    void instantiatesWithBase() {
        final Base base = new DefaultBase();
        MatcherAssert.assertThat(
            "TkLocks cannot be instantiated with base",
            new TkLocks(base),
            Matchers.notNullValue()
        );
    }
}
