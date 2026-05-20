/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.quota;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Rate-throttling Quota backed by an in-memory H2 database.
 *
 * <p>Limits each user to a configurable number of API calls per minute.
 * Usage example:
 * <pre>{@code
 * JdbcDataSource ds = new JdbcDataSource();
 * ds.setURL("jdbc:h2:mem:rate-limit;DB_CLOSE_DELAY=-1");
 * Quota quota = new RtQuota(ds, 300);
 * }</pre>
 *
 * @since 2.0
 */
@ToString
@EqualsAndHashCode(of = {"user", "maximum"})
public final class RtQuota implements Quota {

    /**
     * DDL that ensures the rate-limit tracking table exists.
     *
     * <p>Run this once on the configured data source before constructing any
     * {@link RtQuota}; the constructor does not create the schema itself.
     */
    public static final String SCHEMA =
        "CREATE TABLE IF NOT EXISTS requests (usr VARCHAR(512), ts BIGINT)";

    /**
     * SQL to insert a new request timestamp.
     */
    private static final String INSERT =
        "INSERT INTO requests (usr, ts) VALUES (?, ?)";

    /**
     * SQL to remove expired request records.
     */
    private static final String DELETE =
        "DELETE FROM requests WHERE usr = ? AND ts < ?";

    /**
     * SQL to count requests in the current window.
     */
    private static final String COUNT =
        "SELECT COUNT(*) FROM requests WHERE usr = ? AND ts >= ?";

    /**
     * Window duration in milliseconds (one minute).
     */
    private static final long WINDOW = 60_000L;

    /**
     * Shared data source for H2 in-memory database.
     */
    private final transient DataSource source;

    /**
     * User identifier (URN), empty string for root quota.
     */
    private final transient String user;

    /**
     * Maximum number of requests allowed per minute.
     */
    private final transient int maximum;

    /**
     * Public constructor.
     * @param src H2 in-memory data source
     * @param max Maximum requests allowed per minute
     */
    public RtQuota(final DataSource src, final int max) {
        this(src, "", max);
    }

    /**
     * Private constructor for child quotas created by {@code into()}.
     * @param src Data source
     * @param usr User identifier
     * @param max Maximum requests per minute
     */
    private RtQuota(final DataSource src, final String usr, final int max) {
        this.source = src;
        this.user = usr;
        this.maximum = max;
    }

    @Override
    public Quota into(final String segment) {
        final String key;
        if (this.user.isEmpty()) {
            key = segment;
        } else {
            key = this.user;
        }
        return new RtQuota(this.source, key, this.maximum);
    }

    @Override
    public void use(final String name) throws IOException {
        if (this.user.isEmpty()) {
            return;
        }
        final long now = System.currentTimeMillis();
        final long since = now - RtQuota.WINDOW;
        try (Connection conn = this.source.getConnection()) {
            this.insert(conn, now);
            this.purge(conn, since);
            final int count = this.count(conn, since);
            if (count > this.maximum) {
                throw new IOException(
                    String.format(
                        "Rate limit exceeded for %s: %d requests/min (max: %d)",
                        this.user, count, this.maximum
                    )
                );
            }
        } catch (final SQLException ex) {
            throw new IOException("Failed to check rate limit", ex);
        }
    }

    /**
     * Insert a request timestamp for the current user.
     * @param conn Database connection
     * @param when Timestamp of the request
     * @throws SQLException If a database error occurs
     */
    private void insert(final Connection conn, final long when)
        throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(RtQuota.INSERT)) {
            stmt.setString(1, this.user);
            stmt.setLong(2, when);
            stmt.executeUpdate();
        }
    }

    /**
     * Remove expired request records for the current user.
     * @param conn Database connection
     * @param since Cutoff timestamp; entries older than this are removed
     * @throws SQLException If a database error occurs
     */
    private void purge(final Connection conn, final long since)
        throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(RtQuota.DELETE)) {
            stmt.setString(1, this.user);
            stmt.setLong(2, since);
            stmt.executeUpdate();
        }
    }

    /**
     * Count the requests by the current user in the active window.
     * @param conn Database connection
     * @param since Window start timestamp
     * @return Number of requests within the window
     * @throws SQLException If a database error occurs
     */
    private int count(final Connection conn, final long since)
        throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(RtQuota.COUNT)) {
            stmt.setString(1, this.user);
            stmt.setLong(2, since);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Empty count result");
                }
                return rs.getInt(1);
            }
        }
    }
}
