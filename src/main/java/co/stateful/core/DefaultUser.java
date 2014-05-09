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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.aspects.Tv;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.Credentials;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Region;
import com.jcabi.manifests.Manifests;
import com.jcabi.urn.URN;
import java.util.Iterator;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

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
     * Table name.
     */
    public static final String TOKENS = "tokens";

    /**
     * Hash.
     */
    public static final String HASH = "urn";

    /**
     * Token attribute.
     */
    public static final String ATTR_TOKEN = "token";

    /**
     * Name of the user.
     */
    private final transient URN name;

    /**
     * Region.
     */
    private final transient Region region;

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
        this.region = new Region.Prefixed(
            new Region.Simple(creds),
            Manifests.read("Stateful-DynamoPrefix")
        );
    }

    @Override
    public boolean exists() {
        return this.region.table(DefaultUser.TOKENS)
            .frame().through(new QueryValve())
            .where(DefaultUser.HASH, Conditions.equalTo(this.name))
            .iterator().hasNext();
    }

    @Override
    public String token() {
        final Iterator<Item> items = this.region.table(DefaultUser.TOKENS)
            .frame().through(new QueryValve())
            .where(DefaultUser.HASH, Conditions.equalTo(this.name))
            .iterator();
        final String token;
        if (items.hasNext()) {
            token = items.next().get(DefaultUser.ATTR_TOKEN).getS();
        } else {
            this.refresh();
            token = this.token();
        }
        return token;
    }

    @Override
    public void refresh() {
        this.region.table(DefaultUser.TOKENS).put(
            new Attributes()
                .with(DefaultUser.HASH, this.name)
                .with(
                    DefaultUser.ATTR_TOKEN,
                    Joiner.on('-').join(
                        Iterables.limit(
                            Splitter.fixedLength(Tv.FOUR).split(
                                DigestUtils.md5Hex(
                                    RandomStringUtils.random(Tv.TEN)
                                ).toUpperCase(Locale.ENGLISH)
                            ),
                            Tv.FOUR
                        )
                    )
                )
        );
    }

    @Override
    public Counters counters() {
        return new DyCounters(this.region.table(DyCounters.TBL), this.name);
    }
}
