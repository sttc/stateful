/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.core;

import co.stateful.spi.Counter;
import co.stateful.spi.Counters;
import com.jcabi.aspects.Parallel;
import com.jcabi.urn.URN;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link DyCounter}.
 *
 * @since 0.1
 */
final class DyCounterITCase {

    /**
     * DyCounter can increment and set.
     * @throws Exception If some problem inside
     */
    @Test
    void incrementAndSet() throws Exception {
        final Counters counters = new DefaultUser(
            new URN("urn:test:7889978")
        ).counters();
        final String name = "test-78";
        counters.create(name);
        final Counter counter = counters.get(name);
        final BigDecimal start = new BigDecimal(new SecureRandom().nextLong());
        counter.set(start);
        MatcherAssert.assertThat(
            counter.increment(new BigDecimal(0L)),
            Matchers.equalTo(start)
        );
        final BigDecimal delta = new BigDecimal(new SecureRandom().nextLong());
        MatcherAssert.assertThat(
            counter.increment(delta),
            Matchers.equalTo(start.add(delta))
        );
    }

    /**
     * DyCounter can increment and set in parallel threads.
     * @throws Exception If some problem inside
     */
    @Test
    void incrementAndSetInThreads() throws Exception {
        final Counters counters = new DefaultUser(
            new URN("urn:test:78833")
        ).counters();
        final String name = "test-9990";
        counters.create(name);
        final Counter counter = counters.get(name);
        final BigDecimal start = new BigDecimal(new SecureRandom().nextLong());
        counter.set(start);
        final Set<BigDecimal> values = new ConcurrentSkipListSet<>();
        new Callable<Void>() {
            @Override
            @Parallel(threads = 20)
            public Void call() throws Exception {
                values.add(counter.increment(new BigDecimal(1L)));
                return null;
            }
        } .call();
        MatcherAssert.assertThat(
            values,
            Matchers.hasSize(20)
        );
    }

}
