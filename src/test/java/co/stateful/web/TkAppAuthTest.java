/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import co.stateful.fake.FkBase;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.facets.fork.TkFork;

/**
 * Test case for {@link TkAppAuth}.
 *
 * <p>Tests authentication wrapper functionality and ensures
 * that required dependencies for OAuth providers are available.
 *
 * @since 2.0
 */
final class TkAppAuthTest {

    @Test
    void loadsXmlDependenciesForGithubAuth() throws Exception {
        MatcherAssert.assertThat(
            "TkAppAuth did not load with GitHub authentication support",
            new TkAppAuth(
                new FkBase(),
                new TkFork()
            ),
            Matchers.notNullValue()
        );
    }

    @Test
    void verifiesXpathContextClassAvailable() throws Exception {
        MatcherAssert.assertThat(
            "XPathContext class not available for GitHub OAuth",
            Class.forName("com.jcabi.xml.XPathContext"),
            Matchers.notNullValue()
        );
    }
}
