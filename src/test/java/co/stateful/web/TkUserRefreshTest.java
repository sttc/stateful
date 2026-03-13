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
 * Test case for {@link TkUserRefresh}.
 *
 * @since 2.0
 */
final class TkUserRefreshTest {

    @Test
    void forwardsAfterRefresh() {
        Assertions.assertThrows(
            RsForward.class,
            () -> new TkUserRefresh(new FkBase()).act(
                new RqAuth(
                    new RqFake(),
                    "urn:test:1",
                    "Tëst-Üsér-αβγ"
                )
            ),
            "TkUserRefresh did not forward after refresh"
        );
    }

    @Test
    @SuppressWarnings("PMD.UnnecessaryLocalRule")
    void changesTokenAfterRefresh() throws Exception {
        final FkBase base = new FkBase();
        final String before = base.user(URN.create("urn:test:2")).token();
        try {
            new TkUserRefresh(base).act(
                new RqAuth(
                    new RqFake(),
                    "urn:test:2",
                    "Üsér"
                )
            );
        } catch (final RsForward ignored) {
        }
        MatcherAssert.assertThat(
            "TkUserRefresh did not change token",
            base.user(URN.create("urn:test:2")).token(),
            Matchers.not(Matchers.equalTo(before))
        );
    }
}
