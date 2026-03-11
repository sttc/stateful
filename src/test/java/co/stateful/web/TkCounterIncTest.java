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
 * Test case for {@link TkCounterInc}.
 *
 * @since 2.0
 */
final class TkCounterIncTest {

    @Test
    void instantiatesWithBaseAndName() {
        final Base base = new DefaultBase();
        MatcherAssert.assertThat(
            "TkCounterInc cannot be instantiated with base and name",
            new TkCounterInc(base, "test-counter"),
            Matchers.notNullValue()
        );
    }
}
