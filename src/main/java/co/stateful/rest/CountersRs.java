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

import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import java.util.logging.Level;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Counters of a user.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 */
@Path("/counters")
public final class CountersRs extends BaseRs {

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
            .append(this.list())
            .link(new Link("add", "./add"))
            .render()
            .build();
    }

    /**
     * Add a new counter.
     * @param name Name of the counter
     */
    @POST
    @Path("/add")
    public void add(@FormParam("name") final String name) {
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
     */
    @GET
    @Path("/delete")
    public void delete(@QueryParam("name") final String name) {
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
     */
    private JaxbBundle list() {
        return new JaxbBundle("counters").add(
            // @checkstyle AnonInnerLengthCheck (50 lines)
            new JaxbBundle.Group<String>(this.user().counters()) {
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
                                    .queryParam("name", "{x}")
                                    .build(name)
                            )
                        );
                }
            }
        );
    }

}
