/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.core.DefaultBase;
import co.stateful.quota.QtBase;
import co.stateful.quota.Quota;
import co.stateful.spi.Base;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.Test;
import org.takes.http.FtRemote;

/**
 * Integration test case for {@link TkApp}.
 *
 * @since 2.0
 */
final class TkAppITCase {

    @Test
    void rendersHomePageWithDocumentation() throws Exception {
        final Base base = new QtBase(new DefaultBase(), Quota.UNLIMITED);
        new FtRemote(new TkApp(base)).exec(
            home -> {
                new JdkRequest(home)
                    .header("Accept", "application/xml")
                    .fetch()
                    .as(RestResponse.class)
                    .assertStatus(HttpURLConnection.HTTP_OK)
                    .as(XmlResponse.class)
                    .assertXPath("/page/version")
                    .assertXPath("/page/documentation");
            }
        );
    }

    @Test
    void servesStaticResources() throws Exception {
        final Base base = new QtBase(new DefaultBase(), Quota.UNLIMITED);
        new FtRemote(new TkApp(base)).exec(
            home -> {
                final String[] pages = {
                    "/robots.txt",
                    "/xsl/layout.xsl",
                    "/xsl/index.xsl",
                };
                for (final String page : pages) {
                    new JdkRequest(home)
                        .uri().path(page).back()
                        .header("Accept", "text/plain")
                        .fetch()
                        .as(RestResponse.class)
                        .assertStatus(HttpURLConnection.HTTP_OK);
                }
            }
        );
    }
}
