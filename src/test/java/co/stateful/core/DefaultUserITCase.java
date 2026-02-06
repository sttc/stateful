/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.core;

import co.stateful.spi.User;
import com.jcabi.urn.URN;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link DefaultUser}.
 *
 * @since 0.1
 */
final class DefaultUserITCase {

    /**
     * DefaultUser can manage tokens.
     * @throws Exception If some problem inside
     */
    @Test
    void managesTokens() throws Exception {
        final User user = new DefaultUser(
            new URN("urn:test:8900967")
        );
        final String first = user.token();
        user.refresh();
        final String second = user.token();
        MatcherAssert.assertThat(
            "refreshed token should differ from first token",
            second,
            Matchers.not(Matchers.equalTo(first))
        );
        user.refresh();
        MatcherAssert.assertThat(
            "second refresh should produce new unique token",
            user.token(),
            Matchers.allOf(
                Matchers.not(Matchers.equalTo(first)),
                Matchers.not(Matchers.equalTo(second))
            )
        );
    }

}
