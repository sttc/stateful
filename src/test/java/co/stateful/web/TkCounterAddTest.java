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
 * @since 2.0
 */
final class TkCounterAddTest {

    @Test
    void createsCounter() {
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
                        "name=counter123"
                    ),
                    URN.create("urn:test:1").toString(),
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
                        String.format("name=%s", "a".repeat(64))
                    ),
                    "urn:test:3",
                    "Námé"
                )
            ),
            "TkCounterAdd did not reject too long counter name"
        );
    }
}
