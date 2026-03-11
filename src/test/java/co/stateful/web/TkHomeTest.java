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
 * Test case for {@link TkHome}.
 *
 * @since 2.0
 */
final class TkHomeTest {

    @Test
    void instantiatesWithBase() throws Exception {
        final Base base = new DefaultBase();
        MatcherAssert.assertThat(
            "TkHome cannot be instantiated with base",
            new TkHome(base),
            Matchers.notNullValue()
        );
    }
}
