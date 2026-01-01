/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import co.stateful.core.DefaultBase;
import co.stateful.quota.QtBase;
import co.stateful.quota.Quota;
import co.stateful.spi.Base;
import com.jcabi.aspects.Loggable;
import com.jcabi.manifests.Manifests;
import com.jcabi.manifests.ServletMfs;
import java.io.IOException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.validation.constraints.NotNull;

/**
 * Lifespan.
 *
 * @since 0.1
 */
@Loggable(Loggable.INFO)
public final class Lifespan implements ServletContextListener {

    @Override
    public void contextInitialized(@NotNull final ServletContextEvent event) {
        try {
            Manifests.DEFAULT.append(new ServletMfs(event.getServletContext()));
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
        event.getServletContext().setAttribute(
            Base.class.getName(),
            new QtBase(new DefaultBase(), Quota.UNLIMITED)
        );
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        // nothing
    }

}
