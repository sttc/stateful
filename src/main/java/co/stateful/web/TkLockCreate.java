/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import co.stateful.spi.Locks;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.flash.RsFlash;
import org.takes.facets.forward.RsForward;
import org.takes.rq.form.RqFormBase;

/**
 * Create lock take.
 *
 * <p>Handles POST request to create a new lock.
 * Usage example:
 * <pre>{@code
 * new TkLockCreate(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkLockCreate implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public TkLockCreate(final Base bse) {
        this.base = bse;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final RqFormBase form = new RqFormBase(req);
        final Iterator<String> names = form.param("name").iterator();
        if (!names.hasNext()) {
            throw new RsForward(
                new RsFlash("name can't be empty"),
                "/k"
            );
        }
        final String name = names.next();
        if (!name.matches("[0-9a-zA-Z\\-\\._\\$]{1,256}")) {
            throw new RsForward(
                new RsFlash("1-256 letters, numbers or dashes"),
                "/k"
            );
        }
        final Iterator<String> labels = form.param("label").iterator();
        final String label;
        if (labels.hasNext()) {
            label = labels.next();
        } else {
            label = "none";
        }
        if (label.isEmpty()) {
            throw new RsForward(
                new RsFlash("label can't be empty"),
                "/k"
            );
        }
        final RqUser user = new RqUser(req, this.base);
        if (user.user().locks().names().size() > Locks.MAX) {
            throw new RsForward(
                new RsFlash("too many locks in your account"),
                "/k"
            );
        }
        final String msg = user.user().locks().lock(name, label);
        if (msg.isEmpty()) {
            throw new RsForward(
                new RsFlash(String.format("lock %s added successfully", name)),
                "/k"
            );
        }
        throw new HttpException(HttpURLConnection.HTTP_CONFLICT, msg);
    }
}
