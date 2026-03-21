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
import org.xembly.Directive;

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
                "/webapp/xsl/error.xsl",
                req,
                new XeSource() {
                    @Override
                    public Iterable<Directive> toXembly()
                        throws IOException {
                        return new XeChain(
                            new TkAuthenticated(TkError.this.base).source(req),
                            new XeLink("takes:logout", "/?PsByFlag=PsLogout")
                        ).toXembly();
                    }
                }
            ),
            HttpURLConnection.HTTP_NOT_FOUND
        );
    }
}
