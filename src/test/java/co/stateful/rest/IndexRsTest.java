/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import co.stateful.core.DefaultBase;
import co.stateful.spi.Base;
import com.jcabi.matchers.JaxbConverter;
import com.jcabi.matchers.XhtmlMatchers;
import com.rexsl.mock.HttpHeadersMocker;
import com.rexsl.mock.UriInfoMocker;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link IndexRs}.
 *
 * @since 0.1
 */
final class IndexRsTest {

    /**
     * IndexRs can render front page.
     * @throws Exception If some problem inside
     */
    @Test
    void rendersFrontPage() throws Exception {
        final IndexRs res = new IndexRs();
        res.setUriInfo(new UriInfoMocker().mock());
        res.setHttpHeaders(new HttpHeadersMocker().mock());
        final ServletContext ctx = Mockito.mock(ServletContext.class);
        Mockito.doReturn(new DefaultBase())
            .when(ctx).getAttribute(Base.class.getName());
        res.setServletContext(ctx);
        final SecurityContext sec = Mockito.mock(SecurityContext.class);
        res.setSecurityContext(sec);
        final Response response = res.index();
        MatcherAssert.assertThat(
            JaxbConverter.the(response.getEntity()),
            XhtmlMatchers.hasXPaths(
                "/page/millis",
                "/page/version/name"
            )
        );
    }

}
