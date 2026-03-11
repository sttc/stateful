/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import co.stateful.core.DefaultBase;
import co.stateful.quota.QtBase;
import co.stateful.quota.Quota;
import co.stateful.spi.Base;
import co.stateful.web.TkApp;
import java.io.IOException;
import org.takes.http.Exit;
import org.takes.http.FtCli;

/**
 * Application entry point.
 *
 * <p>Starts the HTTP server using Takes framework with FtCli.
 * Usage example:
 * <pre>{@code
 * java -jar stateful.jar --port=8080
 * }</pre>
 *
 * @since 2.0
 */
public final class Entry {

    /**
     * Ctor.
     */
    private Entry() {
        // intentionally empty
    }

    /**
     * Entry point.
     * @param args Command line args
     * @throws IOException If fails
     */
    public static void main(final String... args) throws IOException {
        final Base base = new QtBase(new DefaultBase(), Quota.UNLIMITED);
        new FtCli(new TkApp(base), args).start(Exit.NEVER);
    }
}
