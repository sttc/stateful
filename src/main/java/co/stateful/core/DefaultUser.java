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

import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Credentials;
import com.jcabi.dynamo.Region;
import com.jcabi.dynamo.Table;
import com.jcabi.manifests.Manifests;
import com.jcabi.urn.URN;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Default user.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 */
@Immutable
@ToString
@EqualsAndHashCode
@Loggable(Loggable.DEBUG)
final class DefaultUser implements User {

    /**
     * Name of the user.
     */
    private final transient URN name;

    /**
     * Counters table.
     */
    private final transient Table cntrs;

    /**
     * Ctor.
     * @param urn Name of it
     */
    DefaultUser(final URN urn) {
        this.name = urn;
        final String key = Manifests.read("Stateful-DynamoKey");
        Credentials creds = new Credentials.Simple(
            key,
            Manifests.read("Stateful-DynamoSecret")
        );
        if ("AAAAABBBBBAAAAABBBBB".equals(key)) {
            creds = new Credentials.Direct(
                creds, Integer.parseInt(System.getProperty("dynamo.port"))
            );
        }
        this.cntrs = new Region.Prefixed(
            new Region.Simple(creds),
            Manifests.read("Stateful-DynamoPrefix")
        ).table(DyCounters.TBL);
    }

    @Override
    public String token() {
        return "ABCD-EFGH-IGHY";
    }

    @Override
    public Counters counters() {
        return new DyCounters(this.cntrs, this.name);
    }
}
