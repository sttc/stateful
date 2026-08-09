/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.fake;

import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.takes.Request;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.codecs.CcPlain;

/**
 * Request with authentication for testing.
 *
 * <p>Wraps a request with authentication identity header.
 * Usage example:
 * <pre>{@code
 * Request req = new RqAuth(new RqFake(), "urn:test:1", "Tëst Üsér");
 * }</pre>
 *
 * @since 2.0
 */
public final class RqAuth implements Request {

    /**
     * Origin request.
     */
    private final Request origin;

    /**
     * User URN.
     */
    private final String urn;

    /**
     * User name.
     */
    private final String name;

    /**
     * Ctor.
     * @param req Origin request
     * @param usr User URN
     * @param nme User name
     */
    public RqAuth(final Request req, final String usr, final String nme) {
        this.origin = req;
        this.urn = usr;
        this.name = nme;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return Iterables.concat(
            this.origin.head(),
            Collections.singleton(
                String.format(
                    "TkAuth: %s",
                    new String(
                        new CcPlain().encode(
                            new Identity.Simple(
                                this.urn,
                                Collections.singletonMap("name", this.name)
                            )
                        ),
                        StandardCharsets.UTF_8
                    )
                )
            )
        );
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }
}
