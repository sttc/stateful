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
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
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
        this.counter.set(this.decimal(value));
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
            .entity(this.counter.increment(this.decimal(value)).toString())
            .build();
    }

    /**
     * Convert string to decimal.
     * @param text Text to convert
     * @return Decimal
     */
    private BigDecimal decimal(final String text) {
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
