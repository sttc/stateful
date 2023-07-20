/*
 * Copyright (c) 2014-2023, Stateful.co
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the stateful.co nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
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
        final String home = new File("target/test-webapp").getAbsolutePath();
        Logger.info(Launch.class, "Loading webapp from %s...", home);
        tomcat.addWebapp("", home);
        tomcat.start();
        tomcat.getServer().await();
    }

}
