/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.flash.TkFlash;
import org.takes.facets.fork.FkMethods;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.facets.forward.TkForward;
import org.takes.tk.TkWithType;
import org.takes.tk.TkWrap;

/**
 * Main application with routing.
 *
 * <p>Defines all routes and wraps with authentication, flash, and fallback.
 * Usage example:
 * <pre>{@code
 * new TkApp(base).act(request)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkApp extends TkWrap {

    /**
     * Pattern for counter name in URL.
     */
    private static final Pattern PTN_COUNTER = Pattern.compile("/c/([^/]+)/.*");

    /**
     * Webapp resources path.
     */
    private static final String WEBAPP = "/webapp";

    /**
     * Robots.txt path.
     */
    private static final String ROBOTS = "/webapp/robots.txt";

    /**
     * Ctor.
     * @param base Base
     */
    public TkApp(final Base base) {
        super(
            new TkAppFallback(
                new TkAppAuth(
                    base,
                    new TkForward(
                        new TkFlash(
                            new TkFork(
                                new FkRegex(
                                    "/robots.txt",
                                    new TkStatic(TkApp.ROBOTS)
                                ),
                                new FkRegex(
                                    "/css/.*",
                                    new TkWithType(
                                        new TkStatic(TkApp.WEBAPP, true),
                                        "text/css"
                                    )
                                ),
                                new FkRegex(
                                    "/js/.*",
                                    new TkStatic(TkApp.WEBAPP, true)
                                ),
                                new FkRegex(
                                    "/images/.*\\.svg",
                                    new TkWithType(
                                        new TkStatic(TkApp.WEBAPP, true),
                                        "image/svg+xml"
                                    )
                                ),
                                new FkRegex(
                                    "/images/.*",
                                    new TkStatic(TkApp.WEBAPP, true)
                                ),
                                new FkRegex(
                                    "/xsl/.*",
                                    new TkWithType(
                                        new TkStatic(TkApp.WEBAPP, true),
                                        "text/xsl"
                                    )
                                ),
                                new FkRegex("/", new TkHome(base)),
                                new FkRegex("/error", new TkError(base)),
                                new FkRegex("/counters", new TkCounters(base)),
                                new FkRegex(
                                    "/counters/add",
                                    new TkFork(
                                        new FkMethods(
                                            "POST",
                                            new TkCounterAdd(base)
                                        )
                                    )
                                ),
                                new FkRegex(
                                    "/counters/delete",
                                    new TkCounterDelete(base)
                                ),
                                new FkRegex(
                                    "/c/(?<name>[^/]+)/set",
                                    new TkFork(
                                        new FkMethods(
                                            "PUT",
                                            (Take) req -> TkApp.counter(
                                                base, req, true
                                            )
                                        ),
                                        new FkMethods(
                                            "GET",
                                            (Take) req -> TkApp.counter(
                                                base, req, true
                                            )
                                        )
                                    )
                                ),
                                new FkRegex(
                                    "/c/(?<name>[^/]+)/inc",
                                    (Take) req -> TkApp.counter(base, req, false)
                                ),
                                new FkRegex("/k", new TkLocks(base)),
                                new FkRegex(
                                    "/k/lock",
                                    new TkFork(
                                        new FkMethods(
                                            "POST",
                                            new TkLockCreate(base)
                                        )
                                    )
                                ),
                                new FkRegex("/k/unlock", new TkUnlock(base)),
                                new FkRegex("/k/label", new TkLockLabel(base)),
                                new FkRegex(
                                    "/u/refresh",
                                    new TkUserRefresh(base)
                                )
                            )
                        )
                    )
                )
            )
        );
    }

    /**
     * Dispatch a counter-set or counter-increment request.
     *
     * <p>Extracts the counter name from the URL, then delegates to the
     * appropriate take.
     *
     * @param base Base
     * @param req Incoming request
     * @param set True for set, false for increment
     * @return Response from the dispatched take
     * @throws Exception If the take fails
     */
    private static Response counter(final Base base, final Request req,
        final boolean set) throws Exception {
        final Matcher matcher = TkApp.PTN_COUNTER.matcher(
            req.head().iterator().next().split(" ")[1]
        );
        if (!matcher.matches()) {
            throw new IllegalStateException("Invalid counter URL");
        }
        final Take take;
        if (set) {
            take = new TkCounterSet(base, matcher.group(1));
        } else {
            take = new TkCounterInc(base, matcher.group(1));
        }
        return take.act(req);
    }
}
