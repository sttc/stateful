/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.fake.RqAuth;
import com.jcabi.matchers.XhtmlMatchers;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkHome}.
 *
 * @since 2.0
 */
final class TkHomeTest {

    @Test
    void rendersHomePage() throws Exception {
        MatcherAssert.assertThat(
            "TkHome did not render home page with menu element",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkHome(new FkBase()).act(
                            new RqAuth(
                                new RqFake(),
                                "urn:test:1",
                                "Tëst-Üsér-αβγ"
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/page/menu[.='home']")
        );
    }

    @Test
    void includesDocumentation() throws Exception {
        MatcherAssert.assertThat(
            "TkHome did not include documentation element",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkHome(new FkBase()).act(
                            new RqAuth(
                                new RqFake(),
                                "urn:test:2",
                                "Üsér"
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/page/documentation")
        );
    }

    @Test
    void includesAuthLinks() throws Exception {
        MatcherAssert.assertThat(
            "TkHome did not include authentication links",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkHome(new FkBase()).act(
                            new RqAuth(
                                new RqFake(),
                                "urn:test:3",
                                "Námé"
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/links/link[@rel='takes:github']",
                "/page/links/link[@rel='takes:facebook']"
            )
        );
    }
}
