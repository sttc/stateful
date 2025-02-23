/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.core;

import co.stateful.spi.Counter;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Item;
import java.io.IOException;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Counter in DynamoDB.
 *
 * @since 0.1
 */
@Immutable
@ToString
@EqualsAndHashCode(of = "item")
@Loggable(Loggable.DEBUG)
final class DyCounter implements Counter {

    /**
     * Item we're working with.
     */
    private final transient Item item;

    /**
     * Ctor.
     * @param itm Item
     */
    DyCounter(final Item itm) {
        this.item = itm;
    }

    @Override
    public void set(final BigDecimal value) throws IOException {
        this.item.put(
            DyCounters.ATTR_VALUE,
            new AttributeValueUpdate(
                new AttributeValue().withN(value.toString()),
                AttributeAction.PUT
            )
        );
    }

    @Override
    public BigDecimal increment(final BigDecimal delta) throws IOException {
        return new BigDecimal(
            this.item.put(
                DyCounters.ATTR_VALUE,
                new AttributeValueUpdate(
                    new AttributeValue().withN(delta.toString()),
                    AttributeAction.ADD
                )
            ).get(DyCounters.ATTR_VALUE).getN()
        );
    }
}
