/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import co.stateful.spi.User;
import com.jcabi.urn.URN;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Iterator;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.facets.auth.Identity;
import org.takes.facets.auth.Pass;
import org.takes.misc.Opt;
import org.takes.rq.RqHeaders;

/**
 * Pass for header-based authentication.
 *
 * <p>Authenticates users via X-Sttc-URN and X-Sttc-Token headers.
 * Usage example:
 * <pre>{@code
 * new PsChain(
 *     new PsHeader(base),
 *     new PsCookie(...)
 * )
 * }</pre>
 *
 * @since 2.0
 */
public final class PsHeader implements Pass {

    /**
     * URN header.
     */
    private static final String HEADER_URN = "X-Sttc-URN";

    /**
     * Token header.
     */
    private static final String HEADER_TOKEN = "X-Sttc-Token";

    /**
     * Base.
     */
    private final Base base;

    /**
     * Ctor.
     * @param bse Base
     */
    public PsHeader(final Base bse) {
        this.base = bse;
    }

    @Override
    public Opt<Identity> enter(final Request req) throws IOException {
        final RqHeaders headers = new RqHeaders.Base(req);
        final Iterator<String> urn = headers.header(PsHeader.HEADER_URN).iterator();
        final Iterator<String> token = headers.header(PsHeader.HEADER_TOKEN).iterator();
        Opt<Identity> result = new Opt.Empty<>();
        if (urn.hasNext() && token.hasNext()) {
            result = this.auth(
                URN.create(urn.next()),
                token.next()
            );
        }
        return result;
    }

    @Override
    public Response exit(final Response res, final Identity identity)
        throws IOException {
        return res;
    }

    /**
     * Authenticate with URN and token.
     * @param urn User URN
     * @param token User token
     * @return Identity option
     * @throws IOException If fails
     */
    private Opt<Identity> auth(final URN urn, final String token)
        throws IOException {
        final User user = this.base.user(urn);
        if (!user.exists()) {
            throw new HttpException(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        if (!user.token().equals(token)) {
            throw new HttpException(HttpURLConnection.HTTP_UNAUTHORIZED);
        }
        return new Opt.Single<>(
            new Identity.Simple(urn.toString())
        );
    }
}
