/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import co.stateful.spi.Base;
import co.stateful.spi.User;
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
import java.io.IOException;
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
 * @since 0.1
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
            Identity identity = Identity.ANONYMOUS;
            if ("1234567".equals(Manifests.read("Stateful-Revision"))
                && !"-".equals(Manifests.read("Stateful-DynamoKey"))) {
                identity = new Identity.Simple(
                    URN.create("urn:test:123456"),
                    "Localhost",
                    URI.create("http://img.stateful.com/unknown.png")
                );
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
     * @checkstyle NonStaticMethodCheck (5 lines)
     */
    @Inset.Runtime
    public final Inset supplementary() {
        return new Inset() {
            @Override
            public void render(final BasePage<?, ?> page,
                final Response.ResponseBuilder builder) {
                builder.header("X-Sttc-Version", BaseRs.VERSION_LABEL);
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
                    page.link(new Link("menu:home", "/"));
                    page.link(new Link("menu:counters", "/counters"));
                    page.link(new Link("menu:locks", "/k"));
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
                    try {
                        page.append(
                            new JaxbBundle("token", BaseRs.this.user().token())
                        );
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                    page.link(new Link("user:refresh", "/u/refresh"));
                }
            }
        };
    }

    /**
     * Version inset.
     * @return The inset
     * @checkstyle NonStaticMethodCheck (5 lines)
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
        return new AuthInset(this, Manifests.read("Stateful-SecurityKey"))
            .with(new Auth(this, this.base()))
            // @checkstyle LineLength (3 lines)
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
                "Please login first",
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
        Validate.notNull(base, "SPI is not initialized");
        return base;
    }

}
