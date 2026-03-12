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
 * Test case for {@link TkError}.
 *
 * @since 2.0
 */
final class TkErrorTest {

    @Test
    void instantiatesWithBase() {
        MatcherAssert.assertThat(
            "TkError cannot be instantiated with base",
            new TkError(new DefaultBase()),
            Matchers.notNullValue()
        );
    }
}
