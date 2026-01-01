/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import java.io.IOException;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * User manipulations.
 *
 * @since 0.1
 */
@Path("/u")
public final class UserRs extends BaseRs {

    /**
     * Refresh the token.
     * @throws IOException If fails due to IO problem
     */
    @GET
    @Path("/refresh")
    public void index() throws IOException {
        this.user().refresh();
        throw this.flash().redirect(
            this.uriInfo().getBaseUriBuilder()
                .clone()
                .path(IndexRs.class)
                .build(),
            "Security token successfully refreshed",
            Level.INFO
        );
    }

}
