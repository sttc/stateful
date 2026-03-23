/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import co.stateful.fake.RqAuth;
import com.jcabi.urn.URN;
import java.net.HttpURLConnection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.takes.HttpException;
import org.takes.facets.forward.RsForward;
import org.takes.rq.RqFake;
import org.takes.rq.RqMethod;

/**
 * Test case for {@link TkUnlock}.
 *
 * @since 2.0
 */
final class TkUnlockTest {

    @Test
    void forwardsAfterUnlock() throws Exception {
        final FkBase base = new FkBase();
        base.user(URN.create("urn:test:1")).locks().lock("mylock", "mylabel");
        Assertions.assertThrows(
            RsForward.class,
            () -> new TkUnlock(base).act(
                new RqAuth(
                    new RqFake(
                        RqMethod.GET,
                        "/k/unlock?name=mylock"
                    ),
                    "urn:test:1",
                    "Tëst-Üsér"
                )
            ),
            "TkUnlock did not forward after unlock"
        );
    }

    @Test
    void removesLockAfterUnlock() throws Exception {
        final FkBase base = new FkBase();
        base.user(URN.create("urn:test:2")).locks().lock("secret", "password");
        try {
            new TkUnlock(base).act(
                new RqAuth(
                    new RqFake(
                        RqMethod.GET,
                        "/k/unlock?name=secret&label=password"
                    ),
                    "urn:test:2",
                    "Üsér"
                )
            );
        } catch (final RsForward ignored) {
        }
        MatcherAssert.assertThat(
            "TkUnlock did not remove lock",
            base.user(URN.create("urn:test:2")).locks().label("secret"),
            Matchers.emptyString()
        );
    }

    @Test
    void rejectsMismatchedLabel() throws Exception {
        final FkBase base = new FkBase();
        base.user(URN.create("urn:test:3")).locks().lock("protected", "correct");
        try {
            new TkUnlock(base).act(
                new RqAuth(
                    new RqFake(
                        RqMethod.GET,
                        "/k/unlock?name=protected&label=wrong"
                    ),
                    "urn:test:3",
                    "Námé"
                )
            );
            throw new AssertionError("HttpException expected");
        } catch (final HttpException ex) {
            MatcherAssert.assertThat(
                "TkUnlock did not return 409 status",
                ex.code(),
                Matchers.equalTo(HttpURLConnection.HTTP_CONFLICT)
            );
        }
    }

    @Test
    void handlesUrlEncodedLabelWithHashAndSlash() throws Exception {
        final FkBase base = new FkBase();
        final String label = "objectionary/home#376";
        final String name = "rt-repo-objectionary-home";
        base.user(URN.create("urn:test:4")).locks().lock(name, label);
        try {
            new TkUnlock(base).act(
                new RqAuth(
                    new RqFake(
                        RqMethod.GET,
                        "/k/unlock?label=objectionary%2Fhome%23376&name=rt-repo-objectionary-home"
                    ),
                    "urn:test:4",
                    "Üsér"
                )
            );
        } catch (final RsForward ignored) {
        }
        MatcherAssert.assertThat(
            "TkUnlock did not remove lock with URL-encoded label containing hash and slash",
            base.user(URN.create("urn:test:4")).locks().label(name),
            Matchers.emptyString()
        );
    }

    @Test
    void handlesNonExistentLockWithLabel() throws Exception {
        final FkBase base = new FkBase();
        Assertions.assertThrows(
            RsForward.class,
            () -> new TkUnlock(base).act(
                new RqAuth(
                    new RqFake(
                        RqMethod.GET,
                        "/k/unlock?label=objectionary%2Fhome%23376&name=rt-repo-objectionary-home"
                    ),
                    "urn:test:5",
                    "Üsér"
                )
            ),
            "TkUnlock should forward when lock does not exist"
        );
    }

    @Test
    void rejectsUnauthenticatedRequest() throws Exception {
        try {
            new TkUnlock(new FkBase()).act(
                new RqFake(
                    RqMethod.GET,
                    "/k/unlock?label=objectionary%2Fhome%23376&name=rt-repo-objectionary-home"
                )
            );
            throw new AssertionError("HttpException expected");
        } catch (final HttpException ex) {
            MatcherAssert.assertThat(
                "TkUnlock did not return 401 status for unauthenticated request",
                ex.code(),
                Matchers.equalTo(HttpURLConnection.HTTP_UNAUTHORIZED)
            );
        }
    }
}
