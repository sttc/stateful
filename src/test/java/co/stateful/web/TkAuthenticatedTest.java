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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;

/**
 * Test case for {@link TkAuthenticated}.
 *
 * @since 2.0
 */
final class TkAuthenticatedTest {

    @Test
    void createsSourceForAuthenticatedUser() throws Exception {
        MatcherAssert.assertThat(
            "TkAuthenticated did not create source with identity",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new RsXembly(
                            new XeAppend(
                                "page",
                                new XeChain(
                                    new TkAuthenticated(new FkBase()).source(
                                        new RqAuth(
                                            new RqFake(),
                                            "urn:test:123",
                                            "Tëst-Üsér-αβγ"
                                        )
                                    )
                                )
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/identity/urn[.='urn:test:123']",
                "/page/identity/name[.='Tëst-Üsér-αβγ']"
            )
        );
    }

    @Test
    void includesTokenInSource() throws Exception {
        MatcherAssert.assertThat(
            "TkAuthenticated did not include token element",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new RsXembly(
                            new XeAppend(
                                "page",
                                new XeChain(
                                    new TkAuthenticated(new FkBase()).source(
                                        new RqAuth(
                                            new RqFake(),
                                            "urn:test:456",
                                            "Üsér"
                                        )
                                    )
                                )
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/page/token")
        );
    }

    @Test
    void includesMenuLinks() throws Exception {
        MatcherAssert.assertThat(
            "TkAuthenticated did not include menu links",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new RsXembly(
                            new XeAppend(
                                "page",
                                new XeChain(
                                    new TkAuthenticated(new FkBase()).source(
                                        new RqAuth(
                                            new RqFake(),
                                            "urn:test:789",
                                            "Námé"
                                        )
                                    )
                                )
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/links/link[@rel='menu:home']",
                "/page/links/link[@rel='menu:counters']",
                "/page/links/link[@rel='menu:locks']"
            )
        );
    }

    @Test
    void returnsEmptyForAnonymousUser() throws Exception {
        MatcherAssert.assertThat(
            "TkAuthenticated returned identity for anonymous user",
            new TextOf(
                new RsPrint(
                    new RsXembly(
                        new XeAppend(
                            "page",
                            new XeChain(
                                new TkAuthenticated(new FkBase()).source(
                                    new RqFake()
                                )
                            )
                        )
                    )
                ).body()
            ).asString(),
            Matchers.not(Matchers.containsString("<identity>"))
        );
    }
}
