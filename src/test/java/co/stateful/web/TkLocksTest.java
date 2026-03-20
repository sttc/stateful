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
import org.takes.rq.RqWithHeader;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkLocks}.
 *
 * @since 2.0
 */
final class TkLocksTest {

    @Test
    void rendersLocksPage() throws Exception {
        MatcherAssert.assertThat(
            "TkLocks did not render locks menu",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkLocks(new FkBase()).act(
                            new RqAuth(
                                new RqWithHeader(new RqFake(), "Accept", "text/xml"),
                                "urn:test:1",
                                "Tëst-Üsér-αβγ"
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPath("/page/menu[.='locks']")
        );
    }

    @Test
    void includesDocumentation() throws Exception {
        MatcherAssert.assertThat(
            "TkLocks did not include documentation",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkLocks(new FkBase()).act(
                            new RqAuth(
                                new RqWithHeader(new RqFake(), "Accept", "text/xml"),
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
    void listsUserLocks() throws Exception {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:3");
        base.user(urn).locks().lock("замок-αβγ", "мітка");
        MatcherAssert.assertThat(
            "TkLocks did not list user locks",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkLocks(base).act(
                            new RqAuth(
                                new RqWithHeader(new RqFake(), "Accept", "text/xml"),
                                urn.toString(),
                                "Námé"
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/locks/lock/name[.='замок-αβγ']",
                "/page/locks/lock/label[.='мітка']"
            )
        );
    }

    @Test
    void includesLockOperationLinks() throws Exception {
        MatcherAssert.assertThat(
            "TkLocks did not include lock operation links",
            XhtmlMatchers.xhtml(
                new TextOf(
                    new RsPrint(
                        new TkLocks(new FkBase()).act(
                            new RqAuth(
                                new RqWithHeader(new RqFake(), "Accept", "text/xml"),
                                "urn:test:4",
                                "Üsér"
                            )
                        )
                    ).body()
                ).asString()
            ),
            XhtmlMatchers.hasXPaths(
                "/page/links/link[@rel='lock']",
                "/page/links/link[@rel='unlock']",
                "/page/links/link[@rel='label']"
            )
        );
    }
}
