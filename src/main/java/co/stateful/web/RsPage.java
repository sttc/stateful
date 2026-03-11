/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import com.jcabi.manifests.Manifests;
import lombok.EqualsAndHashCode;
import org.takes.Response;
import org.takes.rs.RsWithType;
import org.takes.rs.RsWrap;
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
 * XSL page response builder.
 *
 * <p>Wraps content with XML structure and applies XSL transformation.
 * Usage example:
 * <pre>{@code
 * new RsPage(
 *     "/xsl/index.xsl",
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
     * @param sources Extra sources
     */
    public RsPage(final String xsl, final XeSource... sources) {
        super(RsPage.build(xsl, sources));
    }

    /**
     * Build response.
     * @param xsl XSL stylesheet path
     * @param sources Extra sources
     * @return Response
     */
    private static Response build(final String xsl,
        final XeSource... sources) {
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
                new XeAppend(
                    "version",
                    new XeAppend("name", RsPage.version())
                )
            )
        );
        return new RsWithType(raw, "text/xml");
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
