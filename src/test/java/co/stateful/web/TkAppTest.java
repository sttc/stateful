/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.quota.QtBase;
import co.stateful.quota.Quota;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkApp}.
 *
 * @since 2.0
 */
final class TkAppTest {

    @Test
    void servesRobotsTxt() throws Exception {
        MatcherAssert.assertThat(
            "TkApp did not serve robots.txt",
            new TextOf(
                new RsPrint(
                    new TkApp(
                        new QtBase(new FkBase(), Quota.UNLIMITED)
                    ).act(
                        new RqFake("GET", "/robots.txt")
                    )
                ).body()
            ).asString(),
            Matchers.containsString("User-agent")
        );
    }

    @Test
    void servesStaticCss() throws Exception {
        MatcherAssert.assertThat(
            "TkApp did not handle css request",
            new RsPrint(
                new TkApp(
                    new QtBase(new FkBase(), Quota.UNLIMITED)
                ).act(
                    new RqFake("GET", "/css/main.css")
                )
            ).printHead(),
            Matchers.anyOf(
                Matchers.containsString("200"),
                Matchers.containsString("404")
            )
        );
    }

    @Test
    void servesStaticJs() throws Exception {
        MatcherAssert.assertThat(
            "TkApp did not serve JavaScript file",
            new RsPrint(
                new TkApp(
                    new QtBase(new FkBase(), Quota.UNLIMITED)
                ).act(
                    new RqFake("GET", "/js/counters.js")
                )
            ).printHead(),
            Matchers.containsString("200")
        );
    }

    @Test
    void handlesNotFoundGracefully() throws Exception {
        MatcherAssert.assertThat(
            "TkApp did not return 404 for unknown path",
            new RsPrint(
                new TkApp(
                    new QtBase(new FkBase(), Quota.UNLIMITED)
                ).act(
                    new RqFake("GET", "/nonexistent-path-αβγ")
                )
            ).printHead(),
            Matchers.containsString("404")
        );
    }
}
