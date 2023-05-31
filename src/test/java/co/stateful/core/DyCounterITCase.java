/*
 * Copyright (c) 2014-2023, Stateful.co
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the stateful.co nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
