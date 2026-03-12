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
 * Test case for {@link TkCounterSet}.
 *
 * @since 2.0
 */
final class TkCounterSetTest {

    @Test
    void instantiatesWithBaseAndName() {
        MatcherAssert.assertThat(
            "TkCounterSet cannot be instantiated with base and name",
            new TkCounterSet(new DefaultBase(), "test-counter"),
            Matchers.notNullValue()
        );
    }
}
