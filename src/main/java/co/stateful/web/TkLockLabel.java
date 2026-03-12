/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.RsText;

/**
 * Lock label take.
 *
 * <p>Handles GET request to read a lock label.
 * Usage example:
 * <pre>{@code
 * new TkLockLabel(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkLockLabel implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkLockLabel(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        return new RsText(
            new RqUser(req, this.base).user().locks().label(
                new RqHref.Base(req).href().param("name").iterator().next()
            )
        );
    }
}
