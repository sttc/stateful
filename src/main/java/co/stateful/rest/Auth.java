/**
 * Copyright (c) 2014, stateful.co
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the stateful.co nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
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
