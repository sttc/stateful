/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.log.Logger;
import java.io.File;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Launch (used only for heroku).
 *
 * @since 1.6.6
 */
public final class Launch {

    /**
     * Utility class.
     */
    private Launch() {
        // intentionally empty
    }

    /**
     * Entry point.
     * @param args Command line args
     * @throws Exception If fails
     */
    public static void main(final String... args) throws Exception {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        final Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("target/tomcat-base");
        final String port = args[0];
        tomcat.setPort(Integer.valueOf(port));
        tomcat.getConnector();
        final String home = new File(args[1]).getAbsolutePath();
        Logger.info(Launch.class, "Loading webapp from %s...", home);
        tomcat.addWebapp("", home);
        tomcat.start();
        tomcat.getServer().await();
    }

}
