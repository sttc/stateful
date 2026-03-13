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
 * Test case for {@link TkStatic}.
 *
 * @since 2.0
 */
final class TkStaticTest {

    @Test
    void servesFileFromClasspath() throws Exception {
        MatcherAssert.assertThat(
            "TkStatic did not serve file content from classpath",
            new TextOf(
                new RsPrint(
                    new TkStatic("/test-fixture.txt").act(new RqFake())
                ).body()
            ).asString(),
            Matchers.containsString("TkStatic test fixture")
        );
    }

    @Test
    void servesFileFromDirectoryPath() throws Exception {
        MatcherAssert.assertThat(
            "TkStatic did not serve file from directory path",
            new TextOf(
                new RsPrint(
                    new TkStatic("/webapp", true).act(
                        new RqFake("GET", "/robots.txt")
                    )
                ).body()
            ).asString(),
            Matchers.containsString("User-agent")
        );
    }

    @Test
    void throwsOnMissingResource() throws Exception {
        try {
            new TkStatic("/nonexistent-resource-αβγ.txt").act(new RqFake());
            throw new AssertionError("HttpException expected");
        } catch (final HttpException ex) {
            MatcherAssert.assertThat(
                "TkStatic did not return 404 status for missing resource",
                ex.code(),
                Matchers.equalTo(HttpURLConnection.HTTP_NOT_FOUND)
            );
        }
    }
}
