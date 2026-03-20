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
import org.takes.rs.xe.XeDirectives;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Counters page take.
 *
 * <p>Renders the list of user's counters.
 * Usage example:
 * <pre>{@code
 * new TkCounters(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkCounters implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkCounters(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        return new RsPage(
            "/webapp/xsl/counters.xsl",
            req,
            new XeAppend(
                "documentation",
                IOUtils.toString(
                    this.getClass().getResourceAsStream(
                        "/co/stateful/rest/doc-counters.html"
                    ),
                    StandardCharsets.UTF_8
                )
            ),
            this.list(req),
            new XeAppend("menu", "counters"),
            new XeLink("add", "./add"),
            new XeSource() {
                @Override
                public Iterable<Directive> toXembly()
                    throws IOException {
                    return new XeChain(
                        new TkAuthenticated(TkCounters.this.base).source(req),
                        new XeLink("takes:github", "/?PsByFlag=PsGithub"),
                        new XeLink("takes:logout", "/?PsByFlag=PsLogout")
                    ).toXembly();
                }
            }
        );
    }

    /**
     * List counters.
     * @param req Request
     * @return XeSource
     * @throws IOException If fails
     */
    private XeSource list(final Request req) throws IOException {
        final Directives dirs = new Directives().add("counters");
        for (final String name : new RqUser(req, this.base).user().counters().names()) {
            dirs.add("counter")
                .add("name").set(name).up()
                .add("links")
                .add("link")
                .attr("rel", "set")
                .attr("href", String.format("/c/%s/set", name))
                .up()
                .add("link")
                .attr("rel", "increment")
                .attr("href", String.format("/c/%s/inc", name))
                .up()
                .add("link")
                .attr("rel", "delete")
                .attr("href", String.format("/counters/delete?name=%s", name))
                .up()
                .up()
                .up();
        }
        return new XeDirectives(dirs);
    }
}
