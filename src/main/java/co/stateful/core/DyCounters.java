/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.core;

import co.stateful.spi.Counter;
import co.stateful.spi.Counters;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Attributes;
import com.jcabi.dynamo.Conditions;
import com.jcabi.dynamo.Item;
import com.jcabi.dynamo.QueryValve;
import com.jcabi.dynamo.Table;
import com.jcabi.urn.URN;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Counters in DynamoDB.
 *
 * @since 0.1
 */
@Immutable
@ToString
@EqualsAndHashCode(of = "owner")
@Loggable(Loggable.DEBUG)
final class DyCounters implements Counters {

    /**
     * Table name.
     */
    public static final String TBL = "counters";

    /**
     * Hash.
     */
    public static final String HASH = "urn";

    /**
     * Range.
     */
    public static final String RANGE = "name";

    /**
     * Value attribute.
     */
    public static final String ATTR_VALUE = "value";

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
    DyCounters(final Table tbl, final URN urn) {
        this.table = tbl;
        this.owner = urn;
    }

    @Override
    @Cacheable.FlushAfter
    public void create(final String name) throws IOException {
        this.table.put(
            new Attributes()
                .with(DyCounters.HASH, this.owner)
                .with(DyCounters.RANGE, name)
                .with(DyCounters.ATTR_VALUE, new AttributeValue().withN("0"))
        );
    }

    @Override
    @Cacheable.FlushAfter
    public void delete(final String name) {
        Iterators.removeIf(
            this.table.frame()
                .through(new QueryValve().withLimit(1))
                .where(DyCounters.HASH, Conditions.equalTo(this.owner))
                .where(DyCounters.RANGE, Conditions.equalTo(name))
                .iterator(),
            Predicates.alwaysTrue()
        );
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public Counter get(final String name) {
        return new DyCounter(
            this.table.frame()
                .through(new QueryValve().withLimit(1))
                .where(DyCounters.HASH, Conditions.equalTo(this.owner))
                .where(DyCounters.RANGE, Conditions.equalTo(name))
                .iterator()
                .next()
        );
    }

    @Override
    @Cacheable(lifetime = 1, unit = TimeUnit.HOURS)
    public Iterable<String> names() {
        return Iterables.transform(
            this.table.frame()
                .through(new QueryValve())
                .where(DyCounters.HASH, Conditions.equalTo(this.owner)),
            new Function<Item, String>() {
                @Override
                public String apply(final Item item) {
                    try {
                        return item.get(DyCounters.RANGE).getS();
                    } catch (final IOException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
            }
        );
    }
}
