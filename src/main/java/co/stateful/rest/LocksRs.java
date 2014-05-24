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

import co.stateful.core.Locks;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.PageBuilder;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.logging.Level;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;

/**
 * Locks of a user.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle MultipleStringLiteralsCheck (500 lines)
 * @since 1.1
 */
@Path("/k")
public final class LocksRs extends BaseRs {

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
            .stylesheet("/xsl/locks.xsl")
            .build(StPage.class)
            .init(this)
            .append(
                new JaxbBundle(
                    "documentation",
                    IOUtils.toString(
                        this.getClass().getResourceAsStream(
                            "doc-locks.html"
                        )
                    )
                )
            )
            .append(this.list())
            .append(new JaxbBundle("menu", "locks"))
            .link(new Link("lock", "./lock"))
            .link(new Link("unlock", "./unlock"))
            .render()
            .build();
    }

    /**
     * Lock.
     * @param name Name of the lock
     * @param label Label
     * @return Response
     * @throws IOException If fails due to IO problem
     */
    @POST
    @Path("/lock")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response lock(@FormParam(LocksRs.PARAM) final String name,
        @FormParam("label") @DefaultValue("none") final String label)
        throws IOException {
        if (!name.matches("[0-9a-zA-Z\\-\\._\\$]{1,256}")) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUriBuilder()
                    .clone()
                    .path(LocksRs.class)
                    .build(),
                "1-256 letters, numbers or dashes",
                Level.WARNING
            );
        }
        if (label.isEmpty()) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUriBuilder()
                    .clone()
                    .path(LocksRs.class)
                    .build(),
                "label can't be empty",
                Level.WARNING
            );
        }
        if (this.user().locks().names().size() > Locks.MAX) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUriBuilder()
                    .clone()
                    .path(LocksRs.class)
                    .build(),
                "too many locks in your account",
                Level.SEVERE
            );
        }
        if (this.user().locks().lock(name, label)) {
            throw this.flash().redirect(
                this.uriInfo().getBaseUriBuilder()
                    .clone()
                    .path(LocksRs.class)
                    .build(),
                String.format("lock %s added successfully", name),
                Level.INFO
            );
        }
        throw new WebApplicationException(
            HttpURLConnection.HTTP_CONFLICT
        );
    }

    /**
     * Unlock.
     * @param name Name of the lock
     */
    @GET
    @Path("/unlock")
    public void unlock(@QueryParam(LocksRs.PARAM) final String name) {
        this.user().locks().unlock(name);
        throw this.flash().redirect(
            this.uriInfo().getBaseUriBuilder()
                .clone()
                .path(LocksRs.class)
                .build(),
            String.format("%s lock removed", name),
            Level.INFO
        );
    }

    /**
     * Get all locks of a user.
     * @return Locks
     */
    private JaxbBundle list() {
        return new JaxbBundle("locks").add(
            new JaxbBundle.Group<Map.Entry<String, String>>(
                this.user().locks().names().entrySet()
            ) {
                @Override
                public JaxbBundle bundle(final Map.Entry<String, String> ent) {
                    return new JaxbBundle("lock")
                        .add("name", ent.getKey()).up()
                        .add("label", ent.getValue()).up();
                }
            }
        );
    }

}
