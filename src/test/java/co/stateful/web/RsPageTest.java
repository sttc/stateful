/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rs.xe.XeAppend;

/**
 * Test case for {@link RsPage}.
 *
 * @since 2.0
 */
final class RsPageTest {

    @Test
    void instantiatesWithXslPath() {
        MatcherAssert.assertThat(
            "RsPage cannot be instantiated with XSL path",
            new RsPage("/xsl/index.xsl"),
            Matchers.notNullValue()
        );
    }

    @Test
    void instantiatesWithXslPathAndSources() {
        MatcherAssert.assertThat(
            "RsPage cannot be instantiated with XSL path and sources",
            new RsPage(
                "/xsl/index.xsl",
                new XeAppend("menu", "home"),
                new XeAppend("content", "test")
            ),
            Matchers.notNullValue()
        );
    }
}
