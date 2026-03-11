/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeDirectives;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.xembly.Directives;

/**
 * Locks page take.
 *
 * <p>Renders the list of user's locks.
 * Usage example:
 * <pre>{@code
 * new TkLocks(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkLocks implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkLocks(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        return new RsPage(
            "/xsl/locks.xsl",
            new XeAppend(
                "documentation",
                IOUtils.toString(
                    this.getClass().getResourceAsStream(
                        "/co/stateful/rest/doc-locks.html"
                    ),
                    StandardCharsets.UTF_8
                )
            ),
            this.list(req),
            new XeAppend("menu", "locks"),
            new XeLink("lock", "./lock"),
            new XeLink("label", "./label"),
            new XeLink("unlock", "./unlock"),
            new XeSource() {
                @Override
                public Iterable<org.xembly.Directive> toXembly()
                    throws IOException {
                    return new XeChain(
                        new TkAuthenticated(TkLocks.this.base).source(req),
                        new XeLink("takes:facebook", "/?PsFacebook"),
                        new XeLink("takes:google", "/?PsGoogle"),
                        new XeLink("takes:github", "/?PsGithub"),
                        new XeLink("takes:logout", "/?PsLogout")
                    ).toXembly();
                }
            }
        );
    }

    /**
     * List locks.
     * @param req Request
     * @return XeSource
     * @throws IOException If fails
     */
    private XeSource list(final Request req) throws IOException {
        final Directives dirs = new Directives().add("locks");
        for (final Map.Entry<String, String> entry
            : new RqUser(req, this.base).user().locks().names().entrySet()) {
            dirs.add("lock")
                .add("name").set(entry.getKey()).up()
                .add("label").set(entry.getValue()).up()
                .up();
        }
        return new XeDirectives(dirs);
    }
}
