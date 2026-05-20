/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import co.stateful.core.DefaultBase;
import co.stateful.quota.QtBase;
import co.stateful.quota.RtQuota;
import co.stateful.web.TkApp;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.jdbcx.JdbcDataSource;
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
     * Maximum requests per user per minute.
     */
    private static final int MAX_RPM = 300;

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
        final JdbcDataSource src = new JdbcDataSource();
        src.setURL("jdbc:h2:mem:rate-limit;DB_CLOSE_DELAY=-1");
        try (
            Connection conn = src.getConnection();
            Statement stmt = conn.createStatement()
        ) {
            stmt.execute(RtQuota.SCHEMA);
        } catch (final SQLException ex) {
            throw new IOException("Failed to initialize rate-limit table", ex);
        }
        new FtCli(
            new TkApp(new QtBase(new DefaultBase(), new RtQuota(src, Entry.MAX_RPM))),
            args
        ).start(Exit.NEVER);
    }
}
