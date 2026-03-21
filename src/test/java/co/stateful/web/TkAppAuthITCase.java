/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.JsonResponse;
import com.sun.net.httpserver.HttpServer;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration test case for {@link TkAppAuth}.
 *
 * <p>Tests that GitHub OAuth callback has all required
 * dependencies available at runtime by mocking GitHub's
 * OAuth server response.
 *
 * @since 2.0
 */
final class TkAppAuthITCase {

    /**
     * Test access token for OAuth mock.
     */
    private static final String TOKEN = "tëst-tökén-αβγ-9876543210";

    @Test
    void parsesJsonFromGithubOauthResponse() throws Exception {
        final HttpServer server = HttpServer.create(
            new InetSocketAddress(0), 0
        );
        server.createContext(
            "/login/oauth/access_token",
            exchange -> {
                final byte[] bytes = String.format(
                    "{\"access_token\":\"%s\",\"token_type\":\"bearer\"}",
                    TkAppAuthITCase.TOKEN
                ).getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add(
                    "Content-Type", "application/json"
                );
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
                exchange.getResponseBody().write(bytes);
                exchange.close();
            }
        );
        server.start();
        try {
            MatcherAssert.assertThat(
                "Failed to parse JSON from mocked GitHub OAuth response",
                new JdkRequest(
                    String.format(
                        "http://localhost:%d/login/oauth/access_token",
                        server.getAddress().getPort()
                    )
                ).fetch().as(JsonResponse.class).json().readObject()
                    .getString("access_token"),
                Matchers.equalTo(TkAppAuthITCase.TOKEN)
            );
        } finally {
            server.stop(0);
        }
    }
}
