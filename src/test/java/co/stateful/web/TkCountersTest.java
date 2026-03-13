/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.fake.RqAuth;
import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.urn.URN;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkCounters}.
 *
 * @since 2.0
 */
final class TkCountersTest {

    @Test
    void rendersCountersPage() throws Exception {
        MatcherAssert.assertThat(
            "TkCounters did not render counters menu",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkCounters(new FkBase()).act(
                            new RqAuth(
                                new RqFake(),
                                "urn:test:1",
                                "Tëst-Üsér-αβγ"
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/page/menu[.='counters']")
        );
    }

    @Test
    void includesDocumentation() throws Exception {
        MatcherAssert.assertThat(
            "TkCounters did not include documentation",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkCounters(new FkBase()).act(
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
    void listsUserCounters() throws Exception {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:3");
        base.user(urn).counters().create("счётчик-αβγ");
        MatcherAssert.assertThat(
            "TkCounters did not list user counters",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkCounters(base).act(
                            new RqAuth(
                                new RqFake(),
                                urn.toString(),
                                "Námé"
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/page/counters/counter/name[.='счётчик-αβγ']")
        );
    }

    @Test
    void includesCounterLinks() throws Exception {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:4");
        base.user(urn).counters().create("cnt");
        MatcherAssert.assertThat(
            "TkCounters did not include counter operation links",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkCounters(base).act(
                            new RqAuth(
                                new RqFake(),
                                urn.toString(),
                                "Üsér"
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/counters/counter/links/link[@rel='set']",
                "/page/counters/counter/links/link[@rel='increment']",
                "/page/counters/counter/links/link[@rel='delete']"
            )
        );
    }
}
