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
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqHref;

/**
 * Delete counter take.
 *
 * <p>Handles GET request to delete a counter.
 * Usage example:
 * <pre>{@code
 * new TkCounterDelete(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkCounterDelete implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkCounterDelete(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final String name = new RqHref.Base(req).href().param("name").iterator().next();
        new RqUser(req, this.base).user().counters().delete(name);
        throw new RsForward(
            new RsFlash(String.format("counter %s deleted successfully", name)),
            "/counters"
        );
    }
}
