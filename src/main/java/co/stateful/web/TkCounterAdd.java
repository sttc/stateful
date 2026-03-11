/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import co.stateful.spi.Counters;
import com.google.common.collect.Iterables;
import java.io.IOException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.rq.form.RqFormBase;

/**
 * Add counter take.
 *
 * <p>Handles POST request to create a new counter.
 * Usage example:
 * <pre>{@code
 * new TkCounterAdd(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkCounterAdd implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkCounterAdd(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final String name = new RqFormBase(req).param("name").iterator().next();
        if (!name.matches("[0-9a-zA-Z\\-]{1,32}")) {
            throw new RsForward(
                new RsFlash("1-32 letters, numbers or dashes"),
                "/counters"
            );
        }
        final RqUser user = new RqUser(req, this.base);
        if (Iterables.size(user.user().counters().names()) > Counters.MAX) {
            throw new RsForward(
                new RsFlash("too many counters in your account"),
                "/counters"
            );
        }
        user.user().counters().create(name);
        throw new RsForward(
            new RsFlash(String.format("counter %s created successfully", name)),
            "/counters"
        );
    }
}
