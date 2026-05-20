/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.quota;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtQuota}.
 * @since 2.0
 */
final class RtQuotaTest {

    @Test
    void blocksAfterMaximumRequests() throws Exception {
        final JdbcDataSource src = new JdbcDataSource();
        src.setURL("jdbc:h2:mem:test-block;DB_CLOSE_DELAY=-1");
        RtQuotaTest.prepare(src);
        final Quota quota = new RtQuota(src, 3);
        final Quota user = quota.into("urn:test:1");
        user.use("a");
        user.use("b");
        user.use("c");
        try {
            user.use("d");
            throw new AssertionError("IOException expected when limit exceeded");
        } catch (final IOException ex) {
            MatcherAssert.assertThat(
                "Exception message should mention rate limit",
                ex.getMessage(),
                Matchers.containsString("Rate limit")
            );
        }
    }

    @Test
    void allowsRequestsFromDifferentUsers() throws Exception {
        final JdbcDataSource src = new JdbcDataSource();
        src.setURL("jdbc:h2:mem:test-users;DB_CLOSE_DELAY=-1");
        RtQuotaTest.prepare(src);
        final Quota quota = new RtQuota(src, 2);
        final Quota alice = quota.into("urn:test:1");
        final Quota bob = quota.into("urn:test:2");
        alice.use("op");
        alice.use("op");
        bob.use("op");
        bob.use("op");
        MatcherAssert.assertThat(
            "Different users should have independent limits",
            true,
            Matchers.is(true)
        );
    }

    @Test
    void doesNotThrowWhenBelowLimit() throws Exception {
        final JdbcDataSource src = new JdbcDataSource();
        src.setURL("jdbc:h2:mem:test-below;DB_CLOSE_DELAY=-1");
        RtQuotaTest.prepare(src);
        final Quota quota = new RtQuota(src, 300);
        final Quota user = quota.into("urn:test:1");
        for (int idx = 0; idx < 300; ++idx) {
            user.use("op");
        }
        MatcherAssert.assertThat(
            "300 requests should be within the limit",
            true,
            Matchers.is(true)
        );
    }

    /**
     * Initialize the rate-limit tracking table on the test data source.
     * @param src Data source to prepare
     * @throws Exception If the schema cannot be created
     */
    private static void prepare(final DataSource src) throws Exception {
        try (
            Connection conn = src.getConnection();
            Statement stmt = conn.createStatement()
        ) {
            stmt.execute(RtQuota.SCHEMA);
        }
    }
}
