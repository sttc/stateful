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

/**
 * User token refresh take.
 *
 * <p>Handles GET request to refresh the security token.
 * Usage example:
 * <pre>{@code
 * new TkUserRefresh(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkUserRefresh implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkUserRefresh(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        new RqUser(req, this.base).user().refresh();
        throw new RsForward(
            new RsFlash("Security token successfully refreshed"),
            "/"
        );
    }
}
