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

import co.stateful.core.Base;
import co.stateful.core.User;
import com.jcabi.manifests.Manifests;
import com.jcabi.urn.URN;
import com.rexsl.page.BasePage;
import com.rexsl.page.BaseResource;
import com.rexsl.page.Inset;
import com.rexsl.page.JaxbBundle;
import com.rexsl.page.Link;
import com.rexsl.page.Resource;
import com.rexsl.page.auth.AuthInset;
import com.rexsl.page.auth.Facebook;
import com.rexsl.page.auth.Github;
import com.rexsl.page.auth.Google;
import com.rexsl.page.auth.Identity;
import com.rexsl.page.auth.Provider;
import com.rexsl.page.inset.FlashInset;
import com.rexsl.page.inset.LinksInset;
import com.rexsl.page.inset.VersionInset;
import java.net.URI;
import java.util.logging.Level;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.Validate;

/**
 * Abstract RESTful resource.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@Resource.Forwarded
@Inset.Default(LinksInset.class)
@SuppressWarnings("PMD.TooManyMethods")
public class BaseRs extends BaseResource {

    /**
     * Version of the system, to show in header.
     */
    private static final String VERSION_LABEL = String.format(
        "%s/%s built on %s",
        // @checkstyle MultipleStringLiterals (3 lines)
        Manifests.read("Stateful-Version"),
        Manifests.read("Stateful-Revision"),
        Manifests.read("Stateful-Date")
    );

    /**
     * Test authentication provider.
     */
    private static final Provider TESTER = new Provider() {
        @Override
        public Identity identity() {
            final Identity identity;
            if ("1234567".equals(Manifests.read("Stateful-Revision"))) {
                identity = new Identity.Simple(
                    URN.create("urn:test:123456"),
                    "Localhost",
                    URI.create("http://img.stateful.com/unknown.png")
                );
            } else {
                identity = Identity.ANONYMOUS;
            }
            return identity;
        }
    };

    /**
     * Flash.
     * @return The inset with flash
     */
    @Inset.Runtime
    public final FlashInset flash() {
        return new FlashInset(this);
    }

    /**
     * Supplementary inset.
     * @return The inset
     */
    @Inset.Runtime
    public final Inset supplementary() {
        return new Inset() {
            @Override
            public void render(final BasePage<?, ?> page,
                final Response.ResponseBuilder builder) {
                builder.header("X-Stateful-Version", BaseRs.VERSION_LABEL);
                builder.type(MediaType.TEXT_XML);
                builder.header(HttpHeaders.VARY, "Cookie");
            }
        };
    }

    /**
     * Menu inset.
     * @return The inset
     */
    @Inset.Runtime
    public final Inset menu() {
        return new Inset() {
            @Override
            public void render(final BasePage<?, ?> page,
                final Response.ResponseBuilder builder) {
                if (!BaseRs.this.auth().identity().equals(Identity.ANONYMOUS)) {
                    page.link(new Link("menu:counters", "/counters"));
                }
            }
        };
    }

    /**
     * Token inset.
     * @return The inset
     */
    @Inset.Runtime
    public final Inset token() {
        return new Inset() {
            @Override
            public void render(final BasePage<?, ?> page,
                final Response.ResponseBuilder builder) {
                if (!BaseRs.this.auth().identity().equals(Identity.ANONYMOUS)) {
                    page.append(
                        new JaxbBundle("token", BaseRs.this.user().token())
                    );
                }
            }
        };
    }

    /**
     * Version inset.
     * @return The inset
     */
    @Inset.Runtime
    public final Inset version() {
        return new VersionInset(
            Manifests.read("Stateful-Version"),
            Manifests.read("Stateful-Revision"),
            Manifests.read("Stateful-Date")
        );
    }

    /**
     * Authentication inset.
     * @return The inset
     */
    @Inset.Runtime
    public final AuthInset auth() {
        // @checkstyle LineLength (4 lines)
        return new AuthInset(this, Manifests.read("Stateful-SecurityKey"))
            .with(new Facebook(this, Manifests.read("Stateful-FbId"), Manifests.read("Stateful-FbSecret")))
            .with(new Google(this, Manifests.read("Stateful-GoogleId"), Manifests.read("Stateful-GoogleSecret")))
            .with(new Github(this, Manifests.read("Stateful-GithubId"), Manifests.read("Stateful-GithubSecret")))
            .with(BaseRs.TESTER);
    }

    /**
     * Get current user.
     * @return User
     */
    protected final User user() {
        final Identity identity = this.auth().identity();
        if (identity.equals(Identity.ANONYMOUS)) {
            throw FlashInset.forward(
                this.uriInfo().getBaseUriBuilder().clone()
                    .path(IndexRs.class)
                    .build(),
                "please login first",
                Level.SEVERE
            );
        }
        return this.base().user(identity.urn());
    }

    /**
     * Get spi.
     * @return The spi
     */
    protected final Base base() {
        final Base base = Base.class.cast(
            this.servletContext().getAttribute(Base.class.getName())
        );
        Validate.notNull(base, "spi is not initialized");
        return base;
    }
}
