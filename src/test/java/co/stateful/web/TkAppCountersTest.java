/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.spi.Counter;
import co.stateful.spi.Counters;
import com.jcabi.urn.URN;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.takes.Response;
import org.takes.rq.RqFake;
import org.takes.rs.RsStatus;

/**
 * Tests counter mutation methods in {@link TkApp}.
 *
 * @since 2.0
 */
final class TkAppCountersTest {

    /**
     * Test user.
     */
    private static final URN USER = URN.create("urn:test:methods");

    /**
     * Counter name.
     */
    private static final String NAME = "counter";

    /**
     * Delete endpoint.
     */
    private static final String DELETE = "/counters/delete?name=counter";

    /**
     * Set endpoint.
     */
    private static final String SET = "/c/counter/set?value=5";

    /**
     * Increment endpoint.
     */
    private static final String INC = "/c/counter/inc?value=5";

    @ParameterizedTest
    @ValueSource(
        strings = {
            TkAppCountersTest.DELETE, TkAppCountersTest.SET, TkAppCountersTest.INC
        }
    )
    void rejectsSafeMethods(final String path) throws Exception {
        for (final String method : new String[]{"GET", "HEAD", "OPTIONS"}) {
            final FkBase base = TkAppCountersTest.base();
            final Response response = TkAppCountersTest.request(base, method, path);
            MatcherAssert.assertThat(
                "Safe methods must be rejected and preserve the counter value",
                Arrays.asList(
                    new RsStatus.Base(response).status(),
                    TkAppCountersTest.value(base)
                ),
                Matchers.equalTo(
                    Arrays.asList(HttpURLConnection.HTTP_NOT_FOUND, BigDecimal.TEN)
                )
            );
        }
    }

    @Test
    void deletesWithPost() throws Exception {
        final FkBase base = TkAppCountersTest.base();
        MatcherAssert.assertThat(
            "POST should redirect after deleting the counter",
            Arrays.asList(
                new RsStatus.Base(
                    TkAppCountersTest.request(base, "POST", TkAppCountersTest.DELETE)
                ).status(),
                TkAppCountersTest.value(base)
            ),
            Matchers.equalTo(Arrays.asList(HttpURLConnection.HTTP_SEE_OTHER, null))
        );
    }

    @Test
    void setsWithPut() throws Exception {
        final FkBase base = TkAppCountersTest.base();
        MatcherAssert.assertThat(
            "PUT should set the counter",
            Arrays.asList(
                new RsStatus.Base(
                    TkAppCountersTest.request(base, "PUT", TkAppCountersTest.SET)
                ).status(),
                TkAppCountersTest.value(base)
            ),
            Matchers.equalTo(
                Arrays.asList(HttpURLConnection.HTTP_NO_CONTENT, new BigDecimal("5"))
            )
        );
    }

    @Test
    void incrementsWithPost() throws Exception {
        final FkBase base = TkAppCountersTest.base();
        MatcherAssert.assertThat(
            "POST should increment the counter",
            Arrays.asList(
                new RsStatus.Base(
                    TkAppCountersTest.request(base, "POST", TkAppCountersTest.INC)
                ).status(),
                TkAppCountersTest.value(base)
            ),
            Matchers.equalTo(
                Arrays.asList(HttpURLConnection.HTTP_OK, new BigDecimal("15"))
            )
        );
    }

    private static BigDecimal value(final FkBase base) throws Exception {
        final Counter counter = base.user(TkAppCountersTest.USER)
            .counters().get(TkAppCountersTest.NAME);
        final BigDecimal value;
        if (counter == null) {
            value = null;
        } else {
            value = counter.increment(BigDecimal.ZERO);
        }
        return value;
    }

    private static FkBase base() throws Exception {
        final FkBase base = new FkBase();
        final Counters counters = base.user(TkAppCountersTest.USER).counters();
        counters.create(TkAppCountersTest.NAME);
        counters.get(TkAppCountersTest.NAME).set(BigDecimal.TEN);
        return base;
    }

    private static Response request(final FkBase base, final String method,
        final String path) throws Exception {
        return new TkApp(base).act(
            new RqFake(
                Arrays.asList(
                    String.format("%s %s HTTP/1.1", method, path),
                    String.format("X-Sttc-URN: %s", TkAppCountersTest.USER),
                    String.format(
                        "X-Sttc-Token: %s",
                        base.user(TkAppCountersTest.USER).token()
                    )
                ),
                ""
            )
        );
    }
}
