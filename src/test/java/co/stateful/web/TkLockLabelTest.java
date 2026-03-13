/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.fake.RqAuth;
import com.jcabi.urn.URN;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.rq.RqFake;
import org.takes.rs.RsPrint;

/**
 * Test case for {@link TkLockLabel}.
 *
 * @since 2.0
 */
final class TkLockLabelTest {

    @Test
    void readsExistingLockLabel() throws Exception {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:1");
        base.user(urn).locks().lock("замок-αβγ", "мітка-123");
        MatcherAssert.assertThat(
            "TkLockLabel did not return lock label",
            new TextOf(
                new RsPrint(
                    new TkLockLabel(base).act(
                        new RqAuth(
                            new RqFake(
                                "GET",
                                "/k/label?name=замок-αβγ"
                            ),
                            urn.toString(),
                            "Tëst-Üsér"
                        )
                    )
                ).body()
            ).asString(),
            Matchers.equalTo("мітка-123")
        );
    }

    @Test
    void returnsEmptyForNonExistentLock() throws Exception {
        MatcherAssert.assertThat(
            "TkLockLabel did not return empty for nonexistent lock",
            new TextOf(
                new RsPrint(
                    new TkLockLabel(new FkBase()).act(
                        new RqAuth(
                            new RqFake(
                                "GET",
                                "/k/label?name=nonexistent"
                            ),
                            "urn:test:2",
                            "Üsér"
                        )
                    )
                ).body()
            ).asString(),
            Matchers.emptyString()
        );
    }

    @Test
    void readsLabelWithSpecialChars() throws Exception {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:3");
        base.user(urn).locks().lock("special", "αβγδε-日本語");
        MatcherAssert.assertThat(
            "TkLockLabel did not handle special characters",
            new TextOf(
                new RsPrint(
                    new TkLockLabel(base).act(
                        new RqAuth(
                            new RqFake(
                                "GET",
                                "/k/label?name=special"
                            ),
                            urn.toString(),
                            "Námé"
                        )
                    )
                ).body()
            ).asString(),
            Matchers.equalTo("αβγδε-日本語")
        );
    }
}
