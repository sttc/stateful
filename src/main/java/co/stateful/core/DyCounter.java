/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.core;

import co.stateful.spi.Counter;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.dynamo.Item;
import java.io.IOException;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;

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
            AttributeValueUpdate.builder()
                .value(AttributeValue.builder().n(value.toString()).build())
                .action(AttributeAction.PUT)
                .build()
        );
    }

    @Override
    public BigDecimal increment(final BigDecimal delta) throws IOException {
        return new BigDecimal(
            this.item.put(
                DyCounters.ATTR_VALUE,
                AttributeValueUpdate.builder()
                    .value(AttributeValue.builder().n(delta.toString()).build())
                    .action(AttributeAction.ADD)
                    .build()
            ).get(DyCounters.ATTR_VALUE).n()
        );
    }
}
