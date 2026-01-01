/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import com.rexsl.page.JaxbBundle;
import com.rexsl.page.PageBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;

/**
 * Index resource, front page of the website.
 *
 * @since 0.1
 */
@Path("/")
public final class IndexRs extends BaseRs {

    /**
     * Get entrance page JAX-RS response.
     * @return The JAX-RS response
     * @throws IOException If fails
     */
    @GET
    @Path("/")
    public Response index() throws IOException {
        return new PageBuilder()
            .stylesheet("/xsl/index.xsl")
            .build(StPage.class)
            .init(this)
            .append(new JaxbBundle("menu", "home"))
            .append(
                new JaxbBundle(
                    "documentation",
                    IOUtils.toString(
                        this.getClass().getResourceAsStream(
                            "doc-index.html"
                        ),
                        StandardCharsets.UTF_8
                    )
                )
            )
            .render()
            .build();
    }

}
