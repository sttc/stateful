/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.core.DefaultBase;
import co.stateful.quota.QtBase;
import co.stateful.quota.Quota;
import com.jcabi.http.request.JdkRequest;
import com.jcabi.http.response.RestResponse;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.http.FtRemote;

/**
 * Integration test case for {@link TkAppAuth}.
 *
 * <p>Tests that GitHub OAuth callback has all required
 * dependencies available at runtime.
 *
 * @since 2.0
 */
final class TkAppAuthITCase {

    @Test
    @SuppressWarnings("PMD.UnitTestShouldIncludeAssert")
    void loadsAllDependenciesForGithubOauth() throws Exception {
        new FtRemote(
            new TkApp(new QtBase(new DefaultBase(), Quota.UNLIMITED))
        ).exec(
            home -> new JdkRequest(home)
                .uri()
                .path("/")
                .queryParam("PsByFlag", "PsGithub")
                .queryParam("code", UUID.randomUUID().toString())
                .back()
                .fetch()
                .as(RestResponse.class)
                .assertBody(
                    Matchers.not(
                        Matchers.containsString("NoClassDefFoundError")
                    )
                )
        );
    }
}
