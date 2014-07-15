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
package co.stateful.quota;

import co.stateful.spi.Locks;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import java.io.IOException;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Quota on Locks.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.4
 */
@Immutable
@ToString
@EqualsAndHashCode(of = { "origin", "quota" })
@Loggable(Loggable.DEBUG)
public final class QtLocks implements Locks {

    /**
     * Original object.
     */
    private final transient Locks origin;

    /**
     * Quota to use.
     */
    private final transient Quota quota;

    /**
     * Ctor.
     * @param org Original object
     * @param qta Quota
     */
    public QtLocks(final Locks org, final Quota qta) {
        this.origin = org;
        this.quota = qta;
    }

    @Override
    public Map<String, String> names() throws IOException {
        this.quota.use("names");
        return this.origin.names();
    }

    @Override
    public String lock(final String name, final String label)
        throws IOException {
        this.quota.use("lock");
        return this.origin.lock(name, label);
    }

    @Override
    public void unlock(final String name) throws IOException {
        this.quota.use("unlock");
        this.origin.unlock(name);
    }

    @Override
    public String unlock(final String name, final String label)
        throws IOException {
        this.quota.use("unlock-if");
        return this.origin.unlock(name, label);
    }
}
