/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import com.jcabi.manifests.Manifests;
import java.nio.charset.StandardCharsets;
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
import org.takes.facets.auth.social.PsGithub;

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
     * Pattern matching the PsLogout flag value.
     */
    private static final Pattern PTN_LOGOUT =
        Pattern.compile(Pattern.quote(PsLogout.class.getSimpleName()));

    /**
     * Pattern matching the PsGithub flag value.
     */
    private static final Pattern PTN_GITHUB =
        Pattern.compile(Pattern.quote(PsGithub.class.getSimpleName()));

    /**
     * Shared cookie codec, derived from the manifest security key.
     */
    private static final CcSafe COOKIE_CODEC = new CcSafe(
        new CcHex(
            new CcXor(
                new CcSalted(new CcPlain()),
                Manifests.read("Stateful-SecurityKey")
                    .getBytes(StandardCharsets.UTF_8)
            )
        )
    );

    /**
     * Github OAuth provider.
     */
    private static final PsGithub GITHUB = new PsGithub(
        Manifests.read("Stateful-GithubId"),
        Manifests.read("Stateful-GithubSecret")
    );

    /**
     * Test authentication provider, enabled only in CI dev builds.
     */
    private static final PsFake TESTER = new PsFake(
        "1234567".equals(Manifests.read("Stateful-Revision"))
            && !"-".equals(Manifests.read("Stateful-DynamoKey"))
    );

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
        this.take = new TkAuth(
            origin,
            new PsChain(
                new PsByFlag(
                    new PsByFlag.Pair(TkAppAuth.PTN_LOGOUT, new PsLogout()),
                    new PsByFlag.Pair(TkAppAuth.PTN_GITHUB, TkAppAuth.GITHUB)
                ),
                new PsHeader(base),
                new PsCookie(TkAppAuth.COOKIE_CODEC),
                TkAppAuth.TESTER
            )
        );
    }

    @Override
    public Response act(final Request req) throws Exception {
        return this.take.act(req);
    }
}
