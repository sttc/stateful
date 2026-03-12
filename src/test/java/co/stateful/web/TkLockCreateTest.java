/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.core.DefaultBase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TkLockCreate}.
 *
 * @since 2.0
 */
final class TkLockCreateTest {

    @Test
    void instantiatesWithBase() {
        MatcherAssert.assertThat(
            "TkLockCreate cannot be instantiated with base",
            new TkLockCreate(new DefaultBase()),
            Matchers.notNullValue()
        );
    }
}
