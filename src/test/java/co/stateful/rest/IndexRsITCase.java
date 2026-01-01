/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.matchers.NoBrokenLinks;
import com.jcabi.matchers.W3CMatchers;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link IndexRs}.
 *
 * @since 0.1
 */
final class IndexRsITCase {

    /**
     * Tomcat home.
     */
    private static final String HOME = System.getProperty("tomcat.home");

    /**
     * IndexRs can hit public pages.
     * @throws Exception If some problem inside
     */
    @Test
    void hitsPublicPages() throws Exception {
        final String[] pages = {
            "/",
            "/robots.txt",
            "/xsl/layout.xsl",
            "/xsl/index.xsl",
            "/css/layout.css",
        };
        for (final String page : pages) {
            new JdkRequest(IndexRsITCase.HOME)
                .uri().path(page).back()
                .header("accept", "text/plain")
                .fetch()
                .as(RestResponse.class)
                .assertStatus(HttpURLConnection.HTTP_OK);
        }
    }

    /**
     * IndexRs can mandatory page elements.
     * @throws Exception If some problem inside
     */
    @Test
    void rendersPageElements() throws Exception {
        new JdkRequest(IndexRsITCase.HOME)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertThat(new NoBrokenLinks(URI.create(IndexRsITCase.HOME)))
            .as(XmlResponse.class)
            .assertXPath("/page/version")
            .assertXPath("/page/documentation")
            .assertXPath("/page/millis");
    }

    /**
     * IndexRs can render exception.
     * @throws Exception If some problem inside
     */
    @Test
    void rendersException() throws Exception {
        new JdkRequest(IndexRsITCase.HOME)
            .uri().path("/trap").back()
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .assertXPath("//xhtml:title[.='Internal application error']");
    }

    /**
     * IndexRs can render valid HTML.
     * @throws Exception If some problem inside
     */
    @Test
    @Disabled
    void rendersValidHtml() throws Exception {
        new JdkRequest(IndexRsITCase.HOME)
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .fetch()
            .as(XmlResponse.class)
            .rel("/page/links/link[@rel='menu:counters']/@href")
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_HTML)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .assertBody(W3CMatchers.validHtml());
    }

}
