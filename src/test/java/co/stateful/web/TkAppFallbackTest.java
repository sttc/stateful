/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.tk.TkEmpty;

/**
 * Test case for {@link TkAppFallback}.
 *
 * @since 2.0
 */
final class TkAppFallbackTest {

    @Test
    void instantiatesWithTake() {
        MatcherAssert.assertThat(
            "TkAppFallback cannot be instantiated with take",
            new TkAppFallback(new TkEmpty()),
            Matchers.notNullValue()
        );
    }
}
