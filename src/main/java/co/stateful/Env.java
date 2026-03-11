/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful;

import com.jcabi.manifests.Manifests;

/**
 * Environment configuration reader.
 *
 * <p>This class provides access to manifest properties configured during build.
 * Usage example:
 * <pre>{@code
 * String version = new Env().read("Stateful-Version");
 * }</pre>
 *
 * @since 2.0
 */
public final class Env {

    /**
     * Read a manifest property.
     * @param name Property name
     * @return Property value
     */
    public String read(final String name) {
        return Manifests.read(name);
    }
}
