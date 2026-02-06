/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.core;

import co.stateful.spi.Counters;
import com.jcabi.urn.URN;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link DyCounters}.
 *
 * @since 0.1
 */
final class DyCountersITCase {

    /**
     * DyCounters can manage counters.
     * @throws Exception If some problem inside
     */
    @Test
    void managesCounters() throws Exception {
        final Counters counters = new DefaultUser(
            new URN("urn:test:8900967")
        ).counters();
        final String name = "test-one";
        counters.create(name);
        MatcherAssert.assertThat(
            "created counter name not found in names list",
            counters.names(),
            Matchers.hasItem(name)
        );
        counters.delete(name);
        MatcherAssert.assertThat(
            "deleted counter still present in names list",
            counters.names(),
            Matchers.emptyIterable()
        );
    }

}
