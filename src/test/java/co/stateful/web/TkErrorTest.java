/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.fake.RqAuth;
import com.jcabi.matchers.XhtmlMatchers;
import java.net.HttpURLConnection;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkError}.
 *
 * @since 2.0
 */
final class TkErrorTest {

    @Test
    void rendersErrorPageWithNotFoundStatus() throws Exception {
        MatcherAssert.assertThat(
            "TkError did not return 404 status code",
            new RsPrint(
                new TkError(new FkBase()).act(
                    new RqAuth(
                        new RqFake(),
                        "urn:test:1",
                        "Tëst-Üsér-αβγ"
                    )
                )
            ).printHead(),
            Matchers.containsString(
                String.valueOf(HttpURLConnection.HTTP_NOT_FOUND)
            )
        );
    }

    @Test
    void rendersErrorPageWithXmlContent() throws Exception {
        MatcherAssert.assertThat(
            "TkError did not render XML page structure",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkError(new FkBase()).act(
                            new RqAuth(
                                new RqFake(),
                                "urn:test:2",
                                "Üsér"
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/page")
        );
    }

    @Test
    void includesAuthenticationLinks() throws Exception {
        MatcherAssert.assertThat(
            "TkError did not include authentication links",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkError(new FkBase()).act(
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
                "/page/links/link[@rel='takes:logout']"
            )
        );
    }
}
