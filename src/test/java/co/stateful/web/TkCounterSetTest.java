/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.fake.RqAuth;
import com.jcabi.urn.URN;
import java.net.HttpURLConnection;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkCounterSet}.
 *
 * @since 2.0
 */
final class TkCounterSetTest {

    @Test
    void setsCounterValue() throws Exception {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:1");
        base.user(urn).counters().create("счётчик-αβγ");
        MatcherAssert.assertThat(
            "TkCounterSet did not return empty response",
            new TextOf(
                new RsPrint(
                    new TkCounterSet(base, "счётчик-αβγ").act(
                        new RqAuth(
                            new RqFake(
                                "PUT",
                                "/c/счётчик-αβγ/set?value=42"
                            ),
                            urn.toString(),
                            "Tëst-Üsér"
                        )
                    )
                ).body()
            ).asString(),
            Matchers.emptyString()
        );
    }

    @Test
    void setsNegativeValue() throws Exception {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:2");
        base.user(urn).counters().create("cnt");
        MatcherAssert.assertThat(
            "TkCounterSet did not accept negative value",
            new TextOf(
                new RsPrint(
                    new TkCounterSet(base, "cnt").act(
                        new RqAuth(
                            new RqFake(
                                "PUT",
                                "/c/cnt/set?value=-999"
                            ),
                            urn.toString(),
                            "Üsér"
                        )
                    )
                ).body()
            ).asString(),
            Matchers.emptyString()
        );
    }

    @Test
    void rejectsInvalidValue() throws Exception {
        try {
            new TkCounterSet(new FkBase(), "test").act(
                new RqAuth(
                    new RqFake(
                        "PUT",
                        "/c/test/set?value=not-a-number"
                    ),
                    "urn:test:3",
                    "Námé"
                )
            );
            throw new AssertionError("HttpException expected");
        } catch (final HttpException ex) {
            MatcherAssert.assertThat(
                "TkCounterSet did not return 400 status",
                ex.code(),
                Matchers.equalTo(HttpURLConnection.HTTP_BAD_REQUEST)
            );
        }
    }

    @Test
    void rejectsValueWithDecimal() throws Exception {
        try {
            new TkCounterSet(new FkBase(), "test").act(
                new RqAuth(
                    new RqFake(
                        "PUT",
                        "/c/test/set?value=12.5"
                    ),
                    "urn:test:4",
                    "Üsér"
                )
            );
            throw new AssertionError("HttpException expected");
        } catch (final HttpException ex) {
            MatcherAssert.assertThat(
                "TkCounterSet did not return 400 for decimal",
                ex.code(),
                Matchers.equalTo(HttpURLConnection.HTTP_BAD_REQUEST)
            );
        }
    }
}
