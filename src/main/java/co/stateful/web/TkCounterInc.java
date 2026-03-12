/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.RsText;

/**
 * Increment counter take.
 *
 * <p>Handles GET request to increment a counter value.
 * Usage example:
 * <pre>{@code
 * new TkCounterInc(base, "test").act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkCounterInc implements Take {

    /**
     * Base.
     */
    private final Base base;

    /**
     * Counter name.
     */
    private final String name;

    /**
     * Ctor.
     * @param bse Base
     * @param cnt Counter name
     */
    public TkCounterInc(final Base bse, final String cnt) {
        this.base = bse;
        this.name = cnt;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final String value = new RqHref.Base(req).href().param("value").iterator().next();
        if (!value.matches("-?[0-9]{1,32}")) {
            throw new HttpException(
                HttpURLConnection.HTTP_BAD_REQUEST,
                "1-32 integer allowed as a value"
            );
        }
        return new RsText(
            new RqUser(req, this.base).user().counters()
                .get(this.name).increment(new BigDecimal(value)).toString()
        );
    }
}
