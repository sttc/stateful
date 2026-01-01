/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import co.stateful.spi.Base;
import co.stateful.spi.User;
import com.jcabi.urn.URN;
import com.rexsl.page.BaseResource;
import com.rexsl.page.auth.Identity;
import com.rexsl.page.auth.Provider;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

/**
 * Authentication.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
final class Auth implements Provider {

    /**
     * URN header.
     */
    private static final String HEADER_URN = "X-Sttc-URN";

    /**
     * Token header.
     */
    private static final String HEADER_TOKEN = "X-Sttc-Token";

    /**
     * Resource.
     */
    private final transient BaseResource resource;

    /**
     * Base.
     */
    private final transient Base base;

    /**
     * Ctor.
     * @param res Resource
     * @param bse Base
     */
    Auth(final BaseResource res, final Base bse) {
        this.resource = res;
        this.base = bse;
    }

    @Override
    public Identity identity() throws IOException {
        final MultivaluedMap<String, String> headers =
            this.resource.httpHeaders().getRequestHeaders();
        Identity identity = Identity.ANONYMOUS;
        if (headers.containsKey(Auth.HEADER_URN)
            && headers.containsKey(Auth.HEADER_TOKEN)) {
            final URN urn = URN.create(headers.getFirst(Auth.HEADER_URN));
            final User user = this.base.user(urn);
            if (!user.exists()) {
                throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).build()
                );
            }
            final String token = headers.getFirst(Auth.HEADER_TOKEN);
            if (!user.token().equals(token)) {
                throw new WebApplicationException(
                    Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).build()
                );
            }
            identity = new Identity.Simple(urn, "http", URI.create("#"));
        }
        return identity;
    }
}
