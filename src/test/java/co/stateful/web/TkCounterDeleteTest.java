/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.fake.RqAuth;
import com.jcabi.urn.URN;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqFake;

/**
 * Test case for {@link TkCounterDelete}.
 *
 * @since 2.0
 */
final class TkCounterDeleteTest {

    @Test
    void forwardsAfterDeletion() throws Exception {
        final FkBase base = new FkBase();
        base.user(URN.create("urn:test:1")).counters().create("счётчик-αβγ");
        Assertions.assertThrows(
            RsForward.class,
            () -> new TkCounterDelete(base).act(
                new RqAuth(
                    new RqFake(
                        "GET",
                        "/counters/delete?name=счётчик-αβγ"
                    ),
                    "urn:test:1",
                    "Tëst-Üsér"
                )
            ),
            "TkCounterDelete did not forward after deletion"
        );
    }

    @Test
    void removesCounterAfterDeletion() throws Exception {
        final FkBase base = new FkBase();
        base.user(URN.create("urn:test:2")).counters().create("toremove");
        try {
            new TkCounterDelete(base).act(
                new RqAuth(
                    new RqFake(
                        "GET",
                        "/counters/delete?name=toremove"
                    ),
                    "urn:test:2",
                    "Üsér"
                )
            );
        } catch (final RsForward ignored) {
        }
        MatcherAssert.assertThat(
            "TkCounterDelete did not remove counter",
            base.user(URN.create("urn:test:2")).counters().get("toremove"),
            Matchers.nullValue()
        );
    }
}
