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
 * Test case for {@link TkUnlock}.
 *
 * @since 2.0
 */
final class TkUnlockTest {

    @Test
    void instantiatesWithBase() {
        MatcherAssert.assertThat(
            "TkUnlock cannot be instantiated with base",
            new TkUnlock(new DefaultBase()),
            Matchers.notNullValue()
        );
    }
}
