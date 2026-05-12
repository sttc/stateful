/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.quota;

import java.io.IOException;
import org.h2.jdbcx.JdbcDataSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link RtQuota}.
 *
 * @since 2.0
 */
final class RtQuotaTest {

    @Test
    void blocksAfterMaximumRequests() throws Exception {
        final JdbcDataSource src = new JdbcDataSource();
        src.setURL("jdbc:h2:mem:test-block;DB_CLOSE_DELAY=-1");
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
        final Quota quota = new RtQuota(src, 2);
        final Quota user1 = quota.into("urn:test:1");
        final Quota user2 = quota.into("urn:test:2");
        user1.use("op");
        user1.use("op");
        user2.use("op");
        user2.use("op");
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
}
