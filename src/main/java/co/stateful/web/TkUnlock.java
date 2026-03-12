/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqHref;

/**
 * Unlock take.
 *
 * <p>Handles GET request to remove a lock.
 * Usage example:
 * <pre>{@code
 * new TkUnlock(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkUnlock implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkUnlock(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final RqHref.Base href = new RqHref.Base(req);
        final String name = href.href().param("name").iterator().next();
        final Iterator<String> labels = href.href().param("label").iterator();
        final RqUser user = new RqUser(req, this.base);
        if (labels.hasNext()) {
            final String match = user.user().locks().unlock(name, labels.next());
            if (!match.isEmpty()) {
                throw new HttpException(
                    HttpURLConnection.HTTP_CONFLICT,
                    String.format("label doesn't match: %s", match)
                );
            }
        } else {
            user.user().locks().unlock(name);
        }
        throw new RsForward(
            new RsFlash(String.format("%s lock removed", name)),
            "/k"
        );
    }
}
