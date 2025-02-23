/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import com.jcabi.http.Request;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import com.jcabi.http.response.XmlResponse;
import com.jcabi.matchers.W3CMatchers;
import java.net.HttpURLConnection;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link CountersRs}.
 *
 * @since 0.1
 */
final class CountersRsITCase {

    /**
     * Tomcat home.
     */
    private static final String HOME = System.getProperty("tomcat.home");

    /**
     * CountersRs can list counters.
     * @throws Exception If some problem inside
     */
    @Test
    void listsCounters() throws Exception {
        new JdkRequest(CountersRsITCase.HOME)
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .fetch()
            .as(XmlResponse.class)
            .rel("/page/links/link[@rel='menu:counters']/@href")
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_OK)
            .as(XmlResponse.class)
            .rel("/page/links/link[@rel='add']/@href")
            .body().formParam("name", "foo-15").back()
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .method(Request.POST)
            .fetch()
            .as(RestResponse.class)
            .assertStatus(HttpURLConnection.HTTP_SEE_OTHER)
            .follow()
            .header(HttpHeaders.ACCEPT, MediaType.TEXT_XML)
            .method(Request.GET)
            .fetch()
            .as(XmlResponse.class)
            .assertXPath("/page/token")
            .assertXPath("/page/flash")
            .assertXPath("/page/counters/counter[name='foo-15']");
    }

    /**
     * CountersRs can render valid HTML.
     * @throws Exception If some problem inside
     */
    @Test
    @Disabled
    void rendersValidHtml() throws Exception {
        new JdkRequest(CountersRsITCase.HOME)
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
