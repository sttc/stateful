/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;

/**
 * Home page take.
 *
 * <p>Renders the front page with documentation.
 * Usage example:
 * <pre>{@code
 * new TkHome(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkHome implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkHome(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        return new RsPage(
            "/xsl/index.xsl",
            new XeAppend("menu", "home"),
            new XeAppend(
                "documentation",
                IOUtils.toString(
                    this.getClass().getResourceAsStream(
                        "/co/stateful/rest/doc-index.html"
                    ),
                    StandardCharsets.UTF_8
                )
            ),
            new XeSource() {
                @Override
                public Iterable<org.xembly.Directive> toXembly()
                    throws IOException {
                    return new XeChain(
                        new TkAuthenticated(TkHome.this.base).source(req),
                        new XeLink("takes:facebook", "/?PsFacebook"),
                        new XeLink("takes:google", "/?PsGoogle"),
                        new XeLink("takes:github", "/?PsGithub"),
                        new XeLink("takes:logout", "/?PsLogout")
                    ).toXembly();
                }
            }
        );
    }
}
