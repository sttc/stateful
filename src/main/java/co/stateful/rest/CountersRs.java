/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import co.stateful.spi.Counters;
import com.google.common.collect.Iterables;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;

/**
 * Counters of a user.
 *
 * @since 0.1
 */
@Path("/counters")
public final class CountersRs extends BaseRs {

    /**
     * Query param.
     */
    private static final String PARAM = "name";

    /**
     * Get entrance page JAX-RS response.
     * @return The JAX-RS response
     * @throws Exception If some problem inside
     */
    @GET
    @Path("/")
    public Response index() throws Exception {
        return new PageBuilder()
            .stylesheet("/xsl/counters.xsl")
            .build(StPage.class)
            .init(this)
            .append(
                new JaxbBundle(
                    "documentation",
                    IOUtils.toString(
                        this.getClass().getResourceAsStream(
                            "doc-counters.html"
                        ),
                        StandardCharsets.UTF_8
                    )
                )
            )
            .append(this.list())
            .append(new JaxbBundle("menu", "counters"))
            .link(new Link("add", "./add"))
            .render()
            .build();
    }

    /**
     * Add a new counter.
     * @param name Name of the counter
     * @throws IOException If fails due to IO problem
     */
    @POST
    @Path("/add")
    public void add(@FormParam(CountersRs.PARAM) final String name)
        throws IOException {
        if (!name.matches("[0-9a-zA-Z\\-]{1,32}")) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUriBuilder()
                    .clone()
                    .path(CountersRs.class)
                    .build(),
                "1-32 letters, numbers or dashes",
                Level.WARNING
            );
        }
        if (Iterables.size(this.user().counters().names()) > Counters.MAX) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUriBuilder()
                    .clone()
                    .path(CountersRs.class)
                    .build(),
                "too many counters in your account",
                Level.SEVERE
            );
        }
        this.user().counters().create(name);
        throw this.flash().redirect(
            this.uriInfo().getBaseUriBuilder()
                .clone()
                .path(CountersRs.class)
                .build(),
            String.format("counter %s created successfully", name),
            Level.INFO
        );
    }

    /**
     * Delete a counter.
     * @param name Name of the counter
     * @throws IOException If fails due to IO problem
     */
    @GET
    @Path("/delete")
    public void delete(@QueryParam(CountersRs.PARAM) final String name)
        throws IOException {
        this.user().counters().delete(name);
        throw this.flash().redirect(
            this.uriInfo().getBaseUriBuilder()
                .clone()
                .path(CountersRs.class)
                .build(),
            String.format("counter %s deleted successfully", name),
            Level.INFO
        );
    }

    /**
     * Get all counters of a user.
     * @return Counters
     * @throws IOException If fails due to IO problem
     */
    private JaxbBundle list() throws IOException {
        return new JaxbBundle("counters").add(
            // @checkstyle AnonInnerLengthCheck (50 lines)
            new JaxbBundle.Group<String>(this.user().counters().names()) {
                @Override
                public JaxbBundle bundle(final String name) {
                    return new JaxbBundle("counter")
                        .add("name", name)
                        .up()
                        .link(
                            new Link(
                                "set",
                                CountersRs.this.uriInfo().getBaseUriBuilder()
                                    .clone()
                                    .path(CounterRs.class)
                                    .path(CounterRs.class, "set")
                                    .build(name)
                            )
                        )
                        .link(
                            new Link(
                                "increment",
                                CountersRs.this.uriInfo().getBaseUriBuilder()
                                    .clone()
                                    .path(CounterRs.class)
                                    .path(CounterRs.class, "increment")
                                    .build(name)
                            )
                        )
                        .link(
                            new Link(
                                "delete",
                                CountersRs.this.uriInfo().getBaseUriBuilder()
                                    .clone()
                                    .path(CountersRs.class)
                                    .path(CountersRs.class, "delete")
                                    .queryParam(CountersRs.PARAM, "{x}")
                                    .build(name)
                            )
                        );
                }
            }
        );
    }

}
