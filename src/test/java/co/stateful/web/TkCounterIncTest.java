/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.fake.RqAuth;
import com.jcabi.urn.URN;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkCounterInc}.
 *
 * @since 2.0
 */
final class TkCounterIncTest {

    @Test
    void incrementsCounter() throws Exception {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:1");
        base.user(urn).counters().create("счётчик-αβγ");
        base.user(urn).counters().get("счётчик-αβγ").set(BigDecimal.TEN);
        MatcherAssert.assertThat(
            "TkCounterInc did not return incremented value",
            new TextOf(
                new RsPrint(
                    new TkCounterInc(base, "счётчик-αβγ").act(
                        new RqAuth(
                            new RqFake(
                                "GET",
                                "/c/счётчик-αβγ/inc?value=5"
                            ),
                            urn.toString(),
                            "Tëst-Üsér"
                        )
                    )
                ).body()
            ).asString(),
            Matchers.equalTo("15")
        );
    }

    @Test
    void incrementsWithNegativeValue() throws Exception {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:2");
        base.user(urn).counters().create("cnt");
        base.user(urn).counters().get("cnt").set(new BigDecimal("100"));
        MatcherAssert.assertThat(
            "TkCounterInc did not decrement with negative value",
            new TextOf(
                new RsPrint(
                    new TkCounterInc(base, "cnt").act(
                        new RqAuth(
                            new RqFake(
                                "GET",
                                "/c/cnt/inc?value=-25"
                            ),
                            urn.toString(),
                            "Üsér"
                        )
                    )
                ).body()
            ).asString(),
            Matchers.equalTo("75")
        );
    }

    @Test
    void rejectsInvalidValue() throws Exception {
        try {
            new TkCounterInc(new FkBase(), "test").act(
                new RqAuth(
                    new RqFake(
                        "GET",
                        "/c/test/inc?value=invalid"
                    ),
                    "urn:test:3",
                    "Námé"
                )
            );
            throw new AssertionError("HttpException expected");
        } catch (final HttpException ex) {
            MatcherAssert.assertThat(
                "TkCounterInc did not return 400 status",
                ex.code(),
                Matchers.equalTo(HttpURLConnection.HTTP_BAD_REQUEST)
            );
        }
    }
}
