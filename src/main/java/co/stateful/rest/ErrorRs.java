/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import com.rexsl.page.PageBuilder;
import java.net.HttpURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Error-catching resource.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @since 0.1
 */
@Path("/error")
public final class ErrorRs extends BaseRs {

    /**
     * Show error, on GET.
     * @return The JAX-RS response
     */
    @GET
    @Path("/")
    public Response get() {
        return new PageBuilder()
            .stylesheet("/xsl/error.xsl")
            .build(StPage.class)
            .init(this)
            .render()
            .status(HttpURLConnection.HTTP_NOT_FOUND)
            .build();
    }

    /**
     * Show errror, on POST.
     * @return The JAX-RS response
     */
    @POST
    @Path("/")
    public Response post() {
        return Response.status(Response.Status.SEE_OTHER).location(
            this.uriInfo().getBaseUriBuilder().clone().path("/error").build()
        ).build();
    }

}
