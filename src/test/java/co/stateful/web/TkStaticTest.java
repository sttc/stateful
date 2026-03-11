/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link TkStatic}.
 *
 * @since 2.0
 */
final class TkStaticTest {

    @Test
    void instantiatesWithResourcePath() {
        MatcherAssert.assertThat(
            "TkStatic cannot be instantiated with resource path",
            new TkStatic("/webapp/robots.txt"),
            Matchers.notNullValue()
        );
    }

    @Test
    void instantiatesWithDirectoryPath() {
        MatcherAssert.assertThat(
            "TkStatic cannot be instantiated as directory",
            new TkStatic("/webapp", true),
            Matchers.notNullValue()
        );
    }
}
