/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.Response;
import org.takes.rq.RqHeaders;
import org.takes.rs.RsWithType;
import org.takes.rs.RsWrap;
import org.takes.rs.RsXslt;
import org.takes.rs.xe.RsXembly;
import org.takes.facets.auth.social.XeGithubLink;
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
 * XSL page response builder with content negotiation.
 *
 * <p>Wraps content with XML structure and applies XSL transformation
 * based on Accept header. Returns raw XML for text/xml or application/xml,
 * otherwise transforms to HTML.
 * Usage example:
 * <pre>{@code
 * new RsPage(
 *     "/webapp/xsl/index.xsl",
 *     request,
 *     new XeAppend("menu", "home"),
 *     new XeAppend("documentation", content)
 * )
 * }</pre>
 *
 * @since 2.0
 */
@EqualsAndHashCode(callSuper = true)
public final class RsPage extends RsWrap {

    /**
     * Ctor.
     * @param xsl XSL stylesheet path
     * @param req Request
     * @param sources Extra sources
     * @throws IOException If fails
     */
    public RsPage(final String xsl, final Request req,
        final XeSource... sources) throws IOException {
        super(RsPage.make(xsl, req, sources));
    }

    /**
     * Build response with content negotiation.
     * @param xsl XSL stylesheet path
     * @param req Request
     * @param sources Extra sources
     * @return Response
     * @throws IOException If fails
     */
    private static Response make(final String xsl, final Request req,
        final XeSource... sources) throws IOException {
        final Response raw = new RsXembly(
            new XeStylesheet(xsl),
            new XeAppend(
                "page",
                new XeChain(sources),
                new XeMillis(),
                new XeSla(),
                new XeDate(),
                new XeLocalhost(),
                new XeLink("home", "/"),
                new XeGithubLink(req, Manifests.read("Stateful-GithubId")),
                new XeAppend(
                    "version",
                    new XeAppend("name", RsPage.version())
                )
            )
        );
        return RsPage.negotiate(raw, req);
    }

    /**
     * Content negotiation based on Accept header.
     * @param raw Raw XML response
     * @param req Request
     * @return Response with correct content type
     * @throws IOException If fails
     */
    private static Response negotiate(final Response raw, final Request req)
        throws IOException {
        final Response result;
        final Collection<String> headers = new HashSet<>(
            new RqHeaders.Base(req).header("Accept")
        );
        if (headers.contains("application/xml") || headers.contains("text/xml")) {
            result = new RsWithType(raw, "text/xml");
        } else {
            result = new RsXslt(new RsWithType(raw, "text/html"));
        }
        return result;
    }

    /**
     * Read version from manifest.
     * @return Version string
     */
    private static String version() {
        return String.format(
            "%s/%s",
            Manifests.read("Stateful-Version"),
            Manifests.read("Stateful-Revision")
        );
    }
}
