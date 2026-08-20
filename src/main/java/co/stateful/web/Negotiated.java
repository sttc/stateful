/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.social.XeGithubLink;
import org.takes.rq.RqHeaders;
import org.takes.rs.RsWithType;
import org.takes.rs.RsXslt;
import org.takes.rs.xe.RsXembly;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeDate;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeLocalhost;
import org.takes.rs.xe.XeMillis;
import org.takes.rs.xe.XeSla;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeStylesheet;

/**
 * Response wrapper that picks the content type based on the request's
 * Accept header and lazily delegates to either raw XML or XSL-transformed
 * HTML.
 * @since 2.0
 */
final class Negotiated implements Response {

    /**
     * Pre-loaded GitHub app id from the manifest.
     */
    private static final String GITHUB_ID =
        Manifests.read("Stateful-GithubId");

    /**
     * Pre-loaded version string in {@code version/revision} format.
     */
    private static final String VERSION = String.format(
        "%s/%s",
        Manifests.read("Stateful-Version"),
        Manifests.read("Stateful-Revision")
    );

    /**
     * XSL stylesheet path applied when the client wants HTML.
     */
    private final String xsl;

    /**
     * Original request, used for content negotiation.
     */
    private final Request req;

    /**
     * Extra XE sources merged into the page.
     */
    private final XeSource[] sources;

    /**
     * Ctor.
     * @param sheet XSL stylesheet path
     * @param request Original HTTP request
     * @param srcs Extra XE sources to merge into the page
     */
    Negotiated(final String sheet, final Request request,
        final XeSource... srcs) {
        this.xsl = sheet;
        this.req = request;
        this.sources = srcs.clone();
    }

    @Override
    public Iterable<String> head() throws IOException {
        return this.choose().head();
    }

    @Override
    public InputStream body() throws IOException {
        return this.choose().body();
    }

    private Response choose() throws IOException {
        final Response raw = new RsXembly(
            new XeStylesheet(this.xsl),
            new XeAppend(
                "page",
                new XeChain(this.sources),
                new XeMillis(),
                new XeSla(),
                new XeDate(),
                new XeLocalhost(),
                new XeLink("home", "/"),
                new XeGithubLink(this.req, Negotiated.GITHUB_ID),
                new XeAppend(
                    "version",
                    new XeAppend("name", Negotiated.VERSION)
                )
            )
        );
        final Collection<String> headers = new HashSet<>(
            new RqHeaders.Base(this.req).header("Accept")
        );
        final Response result;
        if (headers.contains("application/xml")
            || headers.contains("text/xml")) {
            result = new RsWithType(raw, "text/xml");
        } else {
            result = new RsXslt(new RsWithType(raw, "text/html"));
        }
        return result;
    }
}
