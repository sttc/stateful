/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link ErrorRs}.
 *
 * @since 0.1
 */
final class ErrorRsITCase {

    /**
     * Tomcat home.
     */
    private static final String HOME = System.getProperty("tomcat.home");

    /**
     * IndexRs can hit not-found pages.
     * @throws Exception If some problem inside
     * @todo #1:30min The test doesn't work due to a bug in rexsl 1.1 Let's
     *  do something about it or maybe just remove the test, since we are
     *  planning to migrate to Takes anyway.
     */
    @Test
    @Disabled
    void hitsAbsentPages() throws Exception {
        final String[] pages = {
            "/page-doesnt-exist",
            "/xsl/xsl-stylesheet-doesnt-exist.xsl",
            "/css/stylesheet-is-absent.css",
        };
        for (final String page : pages) {
            new JdkRequest(ErrorRsITCase.HOME)
                .uri().path(page).back()
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_NOT_FOUND)
                .as(XmlResponse.class)
                .assertXPath("//xhtml:h1[contains(.,'Page not found')]");
        }
    }

}
