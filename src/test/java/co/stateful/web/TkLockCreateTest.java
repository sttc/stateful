/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.fake.RqAuth;
import com.jcabi.urn.URN;
import java.net.HttpURLConnection;
import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqFake;
import org.takes.rq.RqWithBody;

/**
 * Test case for {@link TkLockCreate}.
 *
 * @since 2.0
 */
final class TkLockCreateTest {

    @Test
    void createsLock() {
        final FkBase base = new FkBase();
        final URN urn = URN.create("urn:test:1");
        Assertions.assertThrows(
            RsForward.class,
            () -> new TkLockCreate(base).act(
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
                        "name=mylock&label=mylabel"
                    ),
                    urn.toString(),
                    "Tëst-Üsér"
                )
            ),
            "TkLockCreate did not forward after lock creation"
        );
    }

    @Test
    void createsLockWithDefaultLabel() {
        Assertions.assertThrows(
            RsForward.class,
            () -> new TkLockCreate(new FkBase()).act(
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
                        "name=mylock"
                    ),
                    "urn:test:2",
                    "Üsér"
                )
            ),
            "TkLockCreate did not accept lock without label"
        );
    }

    @Test
    void rejectsInvalidName() {
        Assertions.assertThrows(
            RsForward.class,
            () -> new TkLockCreate(new FkBase()).act(
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
                    "urn:test:3",
                    "Námé"
                )
            ),
            "TkLockCreate did not reject invalid lock name"
        );
    }

    @Test
    void rejectsConflictingLock() throws Exception {
        final FkBase base = new FkBase();
        base.user(URN.create("urn:test:4")).locks().lock("existing", "first");
        try {
            new TkLockCreate(base).act(
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
                        "name=existing&label=second"
                    ),
                    "urn:test:4",
                    "Üsér"
                )
            );
            throw new AssertionError("HttpException expected");
        } catch (final HttpException ex) {
            MatcherAssert.assertThat(
                "TkLockCreate did not return 409 status",
                ex.code(),
                Matchers.equalTo(HttpURLConnection.HTTP_CONFLICT)
            );
        }
    }
}
