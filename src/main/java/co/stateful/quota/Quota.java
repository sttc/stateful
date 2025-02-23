/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.quota;

import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Quota.
 *
 * @since 1.4
 */
@Immutable
public interface Quota {

    /**
     * Unlimited.
     */
    Quota UNLIMITED = new Quota() {
        @Override
        public Quota into(final String path) {
            return Quota.UNLIMITED;
        }

        @Override
        public void use(final String name) {
            // nothing to do
        }
    };

    /**
     * Into this path.
     * @param path Path
     * @return New quota
     */
    Quota into(String path);

    /**
     * Use this named service.
     * @param name Name of the service
     * @throws IOException If fails due to IO problem
     */
    void use(String name) throws IOException;

}
