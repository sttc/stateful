/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import com.jcabi.matchers.XhtmlMatchers;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.takes.rs.RsPrint;
import org.takes.rs.xe.XeAppend;

/**
 * Test case for {@link RsPage}.
 *
 * @since 2.0
 */
final class RsPageTest {

    @Test
    void rendersXmlPageWithStylesheet() throws Exception {
        MatcherAssert.assertThat(
            "RsPage did not render XML with stylesheet reference",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new RsPage("/xsl/index.xsl")
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/page")
        );
    }

    @Test
    void rendersXmlPageWithSources() throws Exception {
        MatcherAssert.assertThat(
            "RsPage did not include provided sources in output",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new RsPage(
                            "/xsl/index.xsl",
                            new XeAppend("menu", "főoldal"),
                            new XeAppend("content", "tartalom-αβγ")
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/menu[.='főoldal']",
                "/page/content[.='tartalom-αβγ']"
            )
        );
    }

    @Test
    void includesVersionInPage() throws Exception {
        MatcherAssert.assertThat(
            "RsPage did not include version element",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new RsPage("/xsl/index.xsl")
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/page/version/name")
        );
    }

    @Test
    void includesMillisInPage() throws Exception {
        MatcherAssert.assertThat(
            "RsPage did not include millis element",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new RsPage("/xsl/index.xsl")
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/page/millis")
        );
    }
}
