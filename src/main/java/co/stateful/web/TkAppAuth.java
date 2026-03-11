/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import com.jcabi.manifests.Manifests;
import java.io.IOException;
import java.util.regex.Pattern;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.auth.PsByFlag;
import org.takes.facets.auth.PsChain;
import org.takes.facets.auth.PsCookie;
import org.takes.facets.auth.PsFake;
import org.takes.facets.auth.PsLogout;
import org.takes.facets.auth.TkAuth;
import org.takes.facets.auth.codecs.CcHex;
import org.takes.facets.auth.codecs.CcPlain;
import org.takes.facets.auth.codecs.CcSafe;
import org.takes.facets.auth.codecs.CcSalted;
import org.takes.facets.auth.codecs.CcXor;
import org.takes.facets.auth.social.PsFacebook;
import org.takes.facets.auth.social.PsGithub;
import org.takes.facets.auth.social.PsGoogle;

/**
 * Authentication wrapper.
 *
 * <p>Wraps the application with authentication via cookies and OAuth providers.
 * Usage example:
 * <pre>{@code
 * new TkAppAuth(base, origin)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkAppAuth implements Take {

    /**
     * Origin take.
     */
    private final Take take;

    /**
     * Ctor.
     * @param base Base
     * @param origin Origin take
     */
    public TkAppAuth(final Base base, final Take origin) {
        this.take = TkAppAuth.auth(base, origin);
    }

    @Override
    public Response act(final Request req) throws Exception {
        return this.take.act(req);
    }

    /**
     * Create authenticated take.
     * @param base Base
     * @param origin Origin take
     * @return Authenticated take
     */
    private static Take auth(final Base base, final Take origin) {
        return new TkAuth(
            origin,
            new PsChain(
                new PsByFlag(
                    new PsByFlag.Pair(
                        PsLogout.class.getSimpleName(),
                        new PsLogout()
                    ),
                    new PsByFlag.Pair(
                        PsFacebook.class.getSimpleName(),
                        TkAppAuth.facebook()
                    ),
                    new PsByFlag.Pair(
                        PsGoogle.class.getSimpleName(),
                        TkAppAuth.google()
                    ),
                    new PsByFlag.Pair(
                        PsGithub.class.getSimpleName(),
                        TkAppAuth.github()
                    )
                ),
                new PsHeader(base),
                new PsCookie(TkAppAuth.codec()),
                TkAppAuth.tester()
            )
        );
    }

    /**
     * Cookie codec.
     * @return Codec
     */
    private static CcSafe codec() {
        return new CcSafe(
            new CcHex(
                new CcXor(
                    new CcSalted(new CcPlain()),
                    Manifests.read("Stateful-SecurityKey")
                )
            )
        );
    }

    /**
     * Facebook provider.
     * @return Pass
     */
    private static PsFacebook facebook() {
        return new PsFacebook(
            Manifests.read("Stateful-FbId"),
            Manifests.read("Stateful-FbSecret")
        );
    }

    /**
     * Google provider.
     * @return Pass
     */
    private static PsGoogle google() {
        return new PsGoogle(
            Manifests.read("Stateful-GoogleId"),
            Manifests.read("Stateful-GoogleSecret"),
            "https://www.stateful.co/?PsGoogle"
        );
    }

    /**
     * Github provider.
     * @return Pass
     */
    private static PsGithub github() {
        return new PsGithub(
            Manifests.read("Stateful-GithubId"),
            Manifests.read("Stateful-GithubSecret")
        );
    }

    /**
     * Test authentication provider.
     * @return Pass
     */
    private static PsFake tester() {
        return new PsFake(
            "1234567".equals(Manifests.read("Stateful-Revision"))
                && !"-".equals(Manifests.read("Stateful-DynamoKey"))
        );
    }
}
