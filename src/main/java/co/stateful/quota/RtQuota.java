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
import java.sql.Statement;
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
     * DDL to create the tracking table.
     */
    private static final String CREATE =
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
    @SuppressWarnings("PMD.BeanMembersShouldSerialize")
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
     * Public constructor; initializes the H2 tracking table.
     * @param src H2 in-memory data source
     * @param max Maximum requests allowed per minute
     * @throws IOException If the tracking table cannot be created
     */
    public RtQuota(final DataSource src, final int max) throws IOException {
        this(src, "", max);
        try (
            Connection conn = this.source.getConnection();
            Statement stmt = conn.createStatement()
        ) {
            stmt.execute(RtQuota.CREATE);
        } catch (final SQLException ex) {
            throw new IOException("Failed to initialize rate-limit table", ex);
        }
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
        try {
            try (Connection conn = this.source.getConnection()) {
                try (PreparedStatement stmt = conn.prepareStatement(
                    RtQuota.INSERT
                )) {
                    stmt.setString(1, this.user);
                    stmt.setLong(2, now);
                    stmt.executeUpdate();
                }
                try (PreparedStatement stmt = conn.prepareStatement(
                    RtQuota.DELETE
                )) {
                    stmt.setString(1, this.user);
                    stmt.setLong(2, since);
                    stmt.executeUpdate();
                }
                final int count;
                try (PreparedStatement stmt = conn.prepareStatement(
                    RtQuota.COUNT
                )) {
                    stmt.setString(1, this.user);
                    stmt.setLong(2, since);
                    try (ResultSet rs = stmt.executeQuery()) {
                        rs.next();
                        count = rs.getInt(1);
                    }
                }
                if (count > this.maximum) {
                    throw new IOException(
                        String.format(
                            "Rate limit exceeded for %s: %d requests/min (max: %d)",
                            this.user, count, this.maximum
                        )
                    );
                }
            }
        } catch (final SQLException ex) {
            throw new IOException("Failed to check rate limit", ex);
        }
    }

}
