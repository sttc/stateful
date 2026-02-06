/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.core;

import co.stateful.spi.Locks;
import com.jcabi.aspects.Parallel;
import com.jcabi.urn.URN;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Integration case for {@link DyLocks}.
 *
 * @since 0.1
 */
final class DyLocksITCase {

    /**
     * DyLocks can lock/unlock in parallel threads.
     * @throws Exception If some problem inside
     */
    @Test
    void locksAndUnlocksInThreads() throws Exception {
        final Locks locks = new DefaultUser(
            new URN("urn:test:787009")
        ).locks();
        final String name = "lock-9033";
        final AtomicInteger done = new AtomicInteger();
        new Callable<Void>() {
            @Override
            @Parallel(threads = 20)
            public Void call() throws Exception {
                if (locks.lock(name, "nothing special").isEmpty()) {
                    done.incrementAndGet();
                }
                return null;
            }
        } .call();
        MatcherAssert.assertThat(
            "parallel locking allowed more than one thread to acquire lock",
            done.get(),
            Matchers.equalTo(1)
        );
    }

    /**
     * DyLocks can lock/unlock with mandatory label.
     * @throws Exception If some problem inside
     * @since 1.6
     */
    @Test
    void locksAndUnlocksWithLabel() throws Exception {
        final Locks locks = new DefaultUser(
            new URN("urn:test:78119")
        ).locks();
        final String name = "lock-980";
        final String label = "some label \u20ac";
        locks.lock(name, label);
        MatcherAssert.assertThat(
            "unlock with wrong label should fail and return non-empty string",
            locks.unlock(name, "wrong label"),
            Matchers.not(Matchers.equalTo(""))
        );
        MatcherAssert.assertThat(
            "unlock with correct label should succeed and return empty string",
            locks.unlock(name, label),
            Matchers.equalTo("")
        );
        MatcherAssert.assertThat(
            "locking after unlock should succeed and return empty string",
            locks.lock(name, "new label"),
            Matchers.equalTo("")
        );
    }

}
