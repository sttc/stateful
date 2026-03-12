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
 * Test case for {@link TkCounters}.
 *
 * @since 2.0
 */
final class TkCountersTest {

    @Test
    void instantiatesWithBase() {
        MatcherAssert.assertThat(
            "TkCounters cannot be instantiated with base",
            new TkCounters(new DefaultBase()),
            Matchers.notNullValue()
        );
    }
}
