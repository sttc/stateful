/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import co.stateful.spi.Counter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Counter of a user.
 *
 * @since 0.1
 */
@Path("/c/{cnt}")
public final class CounterRs extends BaseRs {

    /**
     * The counter we're working with.
     */
    private transient Counter counter;

    /**
     * Set counter.
     * @param name Counter name
     */
    @PathParam("cnt")
    public void setCounter(final String name) {
        this.counter = this.user().counters().get(name);
    }

    /**
     * Set counter.
     * @param value Value to set
     * @return The JAX-RS response
     * @throws IOException If fails due to IO problem
     */
    @PUT
    @Path("/set")
    public Response set(@QueryParam("value") final String value)
        throws IOException {
        this.counter.set(CounterRs.decimal(value));
        return Response.ok().build();
    }

    /**
     * Add counter.
     * @param value Value to add
     * @return The JAX-RS response
     * @throws IOException If fails due to IO problem
     */
    @GET
    @Path("/inc")
    @Produces(MediaType.TEXT_PLAIN)
    public Response increment(@QueryParam("value") final String value)
        throws IOException {
        return Response.ok()
            .entity(this.counter.increment(CounterRs.decimal(value)).toString())
            .build();
    }

    /**
     * Convert string to decimal.
     * @param text Text to convert
     * @return Decimal
     */
    private static BigDecimal decimal(final String text) {
        if (!text.matches("-?[0-9]{1,32}")) {
            throw new WebApplicationException(
                Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity("1-32 integer allowed as a value")
                    .build()
            );
        }
        return new BigDecimal(text);
    }

}
