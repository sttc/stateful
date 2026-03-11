/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.RsWithStatus;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;

/**
 * Error page take.
 *
 * <p>Renders the error page with 404 status.
 * Usage example:
 * <pre>{@code
 * new TkError(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkError implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkError(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        return new RsWithStatus(
            new RsPage(
                "/xsl/error.xsl",
                req,
                new XeSource() {
                    @Override
                    public Iterable<org.xembly.Directive> toXembly()
                        throws IOException {
                        return new XeChain(
                            new TkAuthenticated(TkError.this.base).source(req),
                            new XeLink("takes:facebook", "/?PsFacebook"),
                            new XeLink("takes:google", "/?PsGoogle"),
                            new XeLink("takes:github", "/?PsGithub"),
                            new XeLink("takes:logout", "/?PsLogout")
                        ).toXembly();
                    }
                }
            ),
            HttpURLConnection.HTTP_NOT_FOUND
        );
    }
}
