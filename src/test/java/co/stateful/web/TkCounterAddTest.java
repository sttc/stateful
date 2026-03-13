/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.fake.RqAuth;
import com.jcabi.urn.URN;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithBody;

/**
 * Test case for {@link TkCounterAdd}.
 *
 * @since 2.0
 */
final class TkCounterAddTest {

    @Test
    void createsCounter() {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:1");
        Assertions.assertThrows(
            RsForward.class,
            () -> new TkCounterAdd(base).act(
                new RqAuth(
                    new RqWithBody(
                        new RqFake(
                            Arrays.asList(
                                "POST / HTTP/1.1",
                                "Host: localhost",
                                "Content-Type: application/x-www-form-urlencoded"
                            ),
                            ""
                        ),
                        "name=counter123"
                    ),
                    urn.toString(),
                    "Tëst-Üsér-αβγ"
                )
            ),
            "TkCounterAdd did not forward after counter creation"
        );
    }

    @Test
    void rejectsInvalidName() {
        Assertions.assertThrows(
            RsForward.class,
            () -> new TkCounterAdd(new FkBase()).act(
                new RqAuth(
                    new RqWithBody(
                        new RqFake(
                            Arrays.asList(
                                "POST / HTTP/1.1",
                                "Host: localhost",
                                "Content-Type: application/x-www-form-urlencoded"
                            ),
                            ""
                        ),
                        "name=invalid@name!"
                    ),
                    "urn:test:2",
                    "Üsér"
                )
            ),
            "TkCounterAdd did not reject invalid counter name"
        );
    }

    @Test
    void rejectsTooLongName() {
        final String name = "a".repeat(64);
        Assertions.assertThrows(
            RsForward.class,
            () -> new TkCounterAdd(new FkBase()).act(
                new RqAuth(
                    new RqWithBody(
                        new RqFake(
                            Arrays.asList(
                                "POST / HTTP/1.1",
                                "Host: localhost",
                                "Content-Type: application/x-www-form-urlencoded"
                            ),
                            ""
                        ),
                        String.format("name=%s", name)
                    ),
                    "urn:test:3",
                    "Námé"
                )
            ),
            "TkCounterAdd did not reject too long counter name"
        );
    }
}
