/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.core.DefaultBase;
import co.stateful.quota.QtBase;
import co.stateful.quota.Quota;
import co.stateful.spi.Base;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TkApp}.
 *
 * @since 2.0
 */
final class TkAppTest {

    @Test
    void instantiatesWithBase() {
        final Base base = new QtBase(new DefaultBase(), Quota.UNLIMITED);
        MatcherAssert.assertThat(
            "TkApp cannot be instantiated with base",
            new TkApp(base),
            Matchers.notNullValue()
        );
    }
}
