/**
 * Copyright (c) 2014, stateful.co
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

import co.stateful.spi.Locks;
import com.jcabi.aspects.Parallel;
import com.jcabi.aspects.Tv;
import com.jcabi.urn.URN;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link DyLocks}.
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
public final class DyLocksITCase {

    /**
     * DyLocks can lock/unlock in parallel threads.
     * @throws Exception If some problem inside
     */
    @Test
    public void locksAndUnlocksInThreads() throws Exception {
        final Locks locks = new DefaultUser(
            new URN("urn:test:787009")
        ).locks();
        final String name = "lock-9033";
        final AtomicInteger done = new AtomicInteger();
        new Callable<Void>() {
            @Override
            @Parallel(threads = Tv.TWENTY)
            public Void call() throws Exception {
                if (locks.lock(name, "nothing special").isEmpty()) {
                    done.incrementAndGet();
                }
                return null;
            }
        } .call();
        MatcherAssert.assertThat(
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
    public void locksAndUnlocksWithLabel() throws Exception {
        final Locks locks = new DefaultUser(
            new URN("urn:test:78119")
        ).locks();
        final String name = "lock-980";
        final String label = "some label \u20ac";
        locks.lock(name, label);
        MatcherAssert.assertThat(
            locks.unlock(name, "wrong label"),
            Matchers.not(Matchers.equalTo(""))
        );
        MatcherAssert.assertThat(
            locks.unlock(name, label),
            Matchers.equalTo("")
        );
        MatcherAssert.assertThat(
            locks.lock(name, "new label"),
            Matchers.equalTo("")
        );
    }

}
