/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import lombok.EqualsAndHashCode;
import org.takes.Request;
import org.takes.rs.RsWrap;
import org.takes.rs.xe.XeSource;

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
     */
    public RsPage(final String xsl, final Request req,
        final XeSource... sources) {
        super(new Negotiated(xsl, req, sources));
    }
}
