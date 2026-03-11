/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import co.stateful.spi.User;
import com.jcabi.urn.URN;
import java.io.IOException;
import org.takes.Request;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.RqAuth;
import org.takes.rq.RqWrap;

/**
 * Request with user access.
 *
 * <p>Provides access to the authenticated user from the request.
 * Usage example:
 * <pre>{@code
 * User user = new RqUser(request, base).user();
 * }</pre>
 *
 * @since 2.0
 */
public final class RqUser extends RqWrap {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param req Request
     * @param bse Base
     */
    public RqUser(final Request req, final Base bse) {
        super(req);
        this.base = bse;
    }

    /**
     * Get user.
     * @return User
     * @throws IOException If fails
     */
    public User user() throws IOException {
        final Identity identity = new RqAuth(this).identity();
        return this.base.user(URN.create(identity.urn()));
    }

    /**
     * Check if user is authenticated.
     * @return True if authenticated
     * @throws IOException If fails
     */
    public boolean exists() throws IOException {
        return !new RqAuth(this).identity().equals(Identity.ANONYMOUS);
    }
}
