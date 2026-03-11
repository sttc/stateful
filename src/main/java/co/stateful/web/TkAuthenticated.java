/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import java.io.IOException;
import java.net.URI;
import org.takes.Request;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.rs.xe.XeAppend;
import org.takes.rs.xe.XeChain;
import org.takes.rs.xe.XeDirectives;
import org.takes.rs.xe.XeLink;
import org.takes.rs.xe.XeSource;
import org.takes.rs.xe.XeWhen;
import org.xembly.Directives;

/**
 * Helper for authenticated page sources.
 *
 * <p>Provides common XeSource elements for authenticated users.
 * Usage example:
 * <pre>{@code
 * new TkAuthenticated(base).source(req)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkAuthenticated {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkAuthenticated(final Base bse) {
        this.base = bse;
    }

    /**
     * Create XeSource for authenticated user.
     * @param req Request
     * @return XeSource
     * @throws IOException If fails
     */
    public XeSource source(final Request req) throws IOException {
        final Identity identity = new RqAuth(req).identity();
        final boolean auth = !identity.equals(Identity.ANONYMOUS);
        return new XeWhen(
            auth,
            () -> new XeChain(
                new XeDirectives(
                    new Directives()
                        .add("identity")
                        .add("urn").set(identity.urn()).up()
                        .add("name").set(
                            identity.properties().getOrDefault("name", "unknown")
                        ).up()
                        .add("photo").set(
                            identity.properties().getOrDefault(
                                "picture",
                                "http://img.stateful.co/unknown.png"
                            )
                        ).up()
                        .up()
                ),
                new XeAppend(
                    "token",
                    this.base.user(
                        com.jcabi.urn.URN.create(identity.urn())
                    ).token()
                ),
                new XeLink("menu:home", "/"),
                new XeLink("menu:counters", "/counters"),
                new XeLink("menu:locks", "/k"),
                new XeLink("user:refresh", "/u/refresh")
            )
        );
    }
}
