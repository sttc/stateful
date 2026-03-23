/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import java.net.HttpURLConnection;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkAppFallback}.
 *
 * @since 2.0
 */
final class TkAppFallbackTest {

    @Test
    void passesRequestToOrigin() throws Exception {
        MatcherAssert.assertThat(
            "TkAppFallback did not pass request to origin take",
            new TextOf(
                new RsPrint(
                    new TkAppFallback(
                        req -> new org.takes.rs.RsText("résponse-αβγ")
                    ).act(new RqFake())
                ).body()
            ).asString(),
            Matchers.containsString("résponse-αβγ")
        );
    }

    @Test
    void handlesNotFoundStatus() throws Exception {
        MatcherAssert.assertThat(
            "TkAppFallback did not return 404 status",
            new RsPrint(
                new TkAppFallback(
                    req -> {
                        throw new HttpException(HttpURLConnection.HTTP_NOT_FOUND);
                    }
                ).act(new RqFake())
            ).printHead(),
            Matchers.containsString("404")
        );
    }

    @Test
    void handlesUnauthorizedStatus() throws Exception {
        MatcherAssert.assertThat(
            "TkAppFallback did not return 401 status",
            new RsPrint(
                new TkAppFallback(
                    req -> {
                        throw new HttpException(HttpURLConnection.HTTP_UNAUTHORIZED);
                    }
                ).act(new RqFake())
            ).printHead(),
            Matchers.containsString("401")
        );
    }

    @Test
    void handlesConflictStatus() throws Exception {
        MatcherAssert.assertThat(
            "TkAppFallback did not return 409 status",
            new RsPrint(
                new TkAppFallback(
                    req -> {
                        throw new HttpException(
                            HttpURLConnection.HTTP_CONFLICT,
                            "läbel-mïsmatch-αβγ"
                        );
                    }
                ).act(new RqFake())
            ).printHead(),
            Matchers.containsString("409")
        );
    }

    @Test
    void handlesInternalError() throws Exception {
        MatcherAssert.assertThat(
            "TkAppFallback did not return 500 status",
            new RsPrint(
                new TkAppFallback(
                    req -> {
                        throw new IllegalStateException("tëst-ërrör-αβγ");
                    }
                ).act(new RqFake())
            ).printHead(),
            Matchers.containsString("500")
        );
    }
}
