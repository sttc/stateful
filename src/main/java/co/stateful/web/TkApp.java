/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.spi.Base;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     * Ctor.
     * @param base Base
     */
    public TkApp(final Base base) {
        super(TkApp.make(base));
    }

    /**
     * Create main take.
     * @param base Base
     * @return Take
     */
    private static Take make(final Base base) {
        return new TkAppFallback(
            new TkAppAuth(
                base,
                new TkForward(
                    new TkFlash(
                        TkApp.routes(base)
                    )
                )
            )
        );
    }

    /**
     * Create routes.
     * @param base Base
     * @return Take
     */
    private static Take routes(final Base base) {
        return new TkFork(
            new FkRegex("/robots.txt", new TkStatic("/webapp/robots.txt")),
            new FkRegex(
                "/css/.*",
                new TkWithType(new TkStatic("/webapp", true), "text/css")
            ),
            new FkRegex("/js/.*", new TkStatic("/webapp", true)),
            new FkRegex(
                "/images/.*\\.svg",
                new TkWithType(new TkStatic("/webapp", true), "image/svg+xml")
            ),
            new FkRegex("/images/.*", new TkStatic("/webapp", true)),
            new FkRegex(
                "/xsl/.*",
                new TkWithType(new TkStatic("/webapp", true), "text/xsl")
            ),
            new FkRegex("/", new TkHome(base)),
            new FkRegex("/error", new TkError(base)),
            new FkRegex("/counters", new TkCounters(base)),
            new FkRegex(
                "/counters/add",
                new TkFork(
                    new FkMethods("POST", new TkCounterAdd(base))
                )
            ),
            new FkRegex("/counters/delete", new TkCounterDelete(base)),
            new FkRegex(
                "/c/(?<name>[^/]+)/set",
                new TkFork(
                    new FkMethods("PUT", TkApp.counterSet(base)),
                    new FkMethods("GET", TkApp.counterSet(base))
                )
            ),
            new FkRegex("/c/(?<name>[^/]+)/inc", TkApp.counterInc(base)),
            new FkRegex("/k", new TkLocks(base)),
            new FkRegex(
                "/k/lock",
                new TkFork(
                    new FkMethods("POST", new TkLockCreate(base))
                )
            ),
            new FkRegex("/k/unlock", new TkUnlock(base)),
            new FkRegex("/k/label", new TkLockLabel(base)),
            new FkRegex("/u/refresh", new TkUserRefresh(base))
        );
    }

    /**
     * Counter set take.
     * @param base Base
     * @return Take
     */
    private static Take counterSet(final Base base) {
        return req -> {
            final Matcher matcher = TkApp.PTN_COUNTER.matcher(
                req.head().iterator().next().split(" ")[1]
            );
            if (!matcher.matches()) {
                throw new IllegalStateException("Invalid counter URL");
            }
            return new TkCounterSet(base, matcher.group(1)).act(req);
        };
    }

    /**
     * Counter increment take.
     * @param base Base
     * @return Take
     */
    private static Take counterInc(final Base base) {
        return req -> {
            final Matcher matcher = TkApp.PTN_COUNTER.matcher(
                req.head().iterator().next().split(" ")[1]
            );
            if (!matcher.matches()) {
                throw new IllegalStateException("Invalid counter URL");
            }
            return new TkCounterInc(base, matcher.group(1)).act(req);
        };
    }
}
