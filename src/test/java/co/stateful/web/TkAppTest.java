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

    /**
     * HTTP GET method.
     */
    private static final String GET = "GET";

    @Test
    void servesRobotsTxt() throws Exception {
        MatcherAssert.assertThat(
            "TkApp did not serve robots.txt",
            new TextOf(
                new RsPrint(
                    new TkApp(
                        new QtBase(new FkBase(), Quota.UNLIMITED)
                    ).act(
                        new RqFake(TkAppTest.GET, "/robots.txt")
                    )
                ).body()
            ).asString(),
            Matchers.containsString("User-agent")
        );
    }

    @Test
    void servesCssWithCorrectContentType() throws Exception {
        MatcherAssert.assertThat(
            "TkApp did not set text/css for CSS file",
            new RsPrint(
                new TkApp(
                    new QtBase(new FkBase(), Quota.UNLIMITED)
                ).act(
                    new RqFake(TkAppTest.GET, "/css/test-αβγ.css")
                )
            ).printHead(),
            Matchers.containsString("Content-Type: text/css")
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
                    new RqFake(TkAppTest.GET, "/js/counters.js")
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
                    new RqFake(TkAppTest.GET, "/nonexistent-path-αβγ")
                )
            ).printHead(),
            Matchers.containsString("404")
        );
    }

    @Test
    void servesSvgWithCorrectContentType() throws Exception {
        MatcherAssert.assertThat(
            "TkApp did not set image/svg+xml for SVG file",
            new RsPrint(
                new TkApp(
                    new QtBase(new FkBase(), Quota.UNLIMITED)
                ).act(
                    new RqFake(TkAppTest.GET, "/images/pomegranate.svg")
                )
            ).printHead(),
            Matchers.containsString("Content-Type: image/svg+xml")
        );
    }
}
