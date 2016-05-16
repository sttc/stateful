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
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Table;
import com.jcabi.urn.URN;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Locks in DynamoDB.
 *
 * @author Yegor Bugayenko (yegor@tpc2.com)
 * @version $Id$
 * @since 1.1
 */
@Immutable
@ToString
@EqualsAndHashCode(of = "owner")
@Loggable(Loggable.DEBUG)
final class DyLocks implements Locks {

    /**
     * Table name.
     */
    public static final String TBL = "locks";

    /**
     * Hash.
     */
    public static final String HASH = "urn";

    /**
     * Range.
     */
    public static final String RANGE = "name";

    /**
     * Label.
     */
    public static final String ATTR_LABEL = "label";

    /**
     * Dynamo table.
     */
    private final transient Table table;

    /**
     * Name of the user.
     */
    private final transient URN owner;

    /**
     * Ctor.
     * @param tbl Dynamo table
     * @param urn Owner of them
     */
    DyLocks(final Table tbl, final URN urn) {
        this.table = tbl;
        this.owner = urn;
    }

    @Override
    public Map<String, String> names() {
        final ImmutableMap.Builder<String, String> map =
            new ImmutableMap.Builder<String, String>();
        Iterables.all(
            this.table.frame().through(
                new QueryValve().withAttributesToGet(
                    DyLocks.ATTR_LABEL
                )
            ).where(DyLocks.HASH, Conditions.equalTo(this.owner)),
            new Predicate<Item>() {
                @Override
                public boolean apply(final Item item) {
                    try {
                        map.put(
                            item.get(DyLocks.RANGE).getS(),
                            item.get(DyLocks.ATTR_LABEL).getS()
                        );
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                    return true;
                }
            }
        );
        return map.build();
    }

    @Override
    public String lock(final String name, final String label)
        throws IOException {
        final AmazonDynamoDB aws = this.table.region().aws();
        String msg = "";
        try {
            final PutItemRequest request = new PutItemRequest();
            request.setTableName(this.table.name());
            request.setItem(
                new Attributes()
                    .with(DyLocks.HASH, this.owner)
                    .with(DyLocks.RANGE, name)
                    .with(DyLocks.ATTR_LABEL, label)
            );
            request.setExpected(
                new ImmutableMap.Builder<String, ExpectedAttributeValue>().put(
                    DyLocks.ATTR_LABEL,
                    new ExpectedAttributeValue().withExists(false)
                ).build()
            );
            aws.putItem(request);
        } catch (final ConditionalCheckFailedException ex) {
            msg = ex.getLocalizedMessage();
        } catch (final AmazonClientException ex) {
            throw new IOException(ex);
        } finally {
            aws.shutdown();
        }
        return msg;
    }

    @Override
    public void unlock(final String name) {
        Iterables.removeIf(
            this.table.frame()
                .through(new QueryValve())
                .where(DyLocks.HASH, this.owner.toString())
                .where(DyLocks.RANGE, name),
            Predicates.alwaysTrue()
        );
    }

    @Override
    public String unlock(final String name, final String label)
        throws IOException {
        final String required = this.label(name);
        String msg = "";
        if (required.equals(label)) {
            this.unlock(name);
        } else {
            msg = required;
        }
        return msg;
    }

    @Override
    public String label(final String name) throws IOException {
        final Iterator<Item> items = this.table.frame()
            .through(new QueryValve())
            .where(DyLocks.HASH, this.owner.toString())
            .where(DyLocks.RANGE, name)
            .iterator();
        String msg = "";
        if (items.hasNext()) {
            msg = items.next().get(DyLocks.ATTR_LABEL).getS();
        }
        return msg;
    }
}
