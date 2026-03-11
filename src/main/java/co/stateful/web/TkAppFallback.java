/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import com.jcabi.log.Logger;
import java.io.IOException;
import java.net.HttpURLConnection;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fallback.Fallback;
import org.takes.facets.fallback.FbChain;
import org.takes.facets.fallback.FbStatus;
import org.takes.facets.fallback.RqFallback;
import org.takes.facets.fallback.TkFallback;
import org.takes.misc.Opt;
import org.takes.rs.RsText;
import org.takes.rs.RsWithStatus;

/**
 * Error handling wrapper.
 *
 * <p>Wraps the application with fallback error handling.
 * Usage example:
 * <pre>{@code
 * new TkAppFallback(origin)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkAppFallback implements Take {

    /**
     * Origin take.
     */
    private final Take take;

    /**
     * Ctor.
     * @param origin Origin take
     */
    public TkAppFallback(final Take origin) {
        this.take = TkAppFallback.wrap(origin);
    }

    @Override
    public Response act(final Request req) throws Exception {
        return this.take.act(req);
    }

    /**
     * Wrap with fallback.
     * @param origin Origin take
     * @return Wrapped take
     */
    private static Take wrap(final Take origin) {
        return new TkFallback(
            origin,
            new FbChain(
                new FbStatus(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    (Fallback) req -> new Opt.Single<>(TkAppFallback.notfound())
                ),
                new FbStatus(
                    HttpURLConnection.HTTP_UNAUTHORIZED,
                    (Fallback) req -> new Opt.Single<>(TkAppFallback.unauthorized())
                ),
                req -> {
                    Logger.error(
                        TkAppFallback.class,
                        "%[exception]s",
                        req.throwable()
                    );
                    return new Opt.Single<>(TkAppFallback.error(req));
                }
            )
        );
    }

    /**
     * Not found response.
     * @return Response
     */
    private static Response notfound() {
        return new RsWithStatus(
            new RsText("Page not found"),
            HttpURLConnection.HTTP_NOT_FOUND
        );
    }

    /**
     * Unauthorized response.
     * @return Response
     */
    private static Response unauthorized() {
        return new RsWithStatus(
            new RsText("Authentication required"),
            HttpURLConnection.HTTP_UNAUTHORIZED
        );
    }

    /**
     * Error response.
     * @param req Fallback request
     * @return Response
     */
    private static Response error(final RqFallback req) {
        return new RsWithStatus(
            new RsText(
                String.format(
                    "Internal error: %s",
                    req.throwable().getMessage()
                )
            ),
            HttpURLConnection.HTTP_INTERNAL_ERROR
        );
    }
}
