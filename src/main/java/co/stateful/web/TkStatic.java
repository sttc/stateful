/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import org.takes.HttpException;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rs.RsWithBody;

/**
 * Static classpath resource take.
 *
 * <p>Serves static resources from the classpath using the context classloader.
 * For single files, use with the full resource path. For directories, use
 * the base path and the request URI will be appended.
 * Usage example:
 * <pre>{@code
 * new TkStatic("/webapp/robots.txt")
 * new TkStatic("/webapp", true)
 * }</pre>
 *
 * @since 2.0
 */
public final class TkStatic implements Take {

    /**
     * Resource base path.
     */
    private final String base;

    /**
     * Whether to append request path.
     */
    private final boolean folder;

    /**
     * Ctor for single file.
     * @param resource Resource path
     */
    public TkStatic(final String resource) {
        this(resource, false);
    }

    /**
     * Ctor.
     * @param path Base path
     * @param directory Whether this is a directory
     */
    public TkStatic(final String path, final boolean directory) {
        this.base = path;
        this.folder = directory;
    }

    @Override
    public Response act(final Request req) throws IOException {
        final String resource;
        if (this.folder) {
            resource = this.base + new RqHref.Base(req).href().path();
        } else {
            resource = this.base;
        }
        final String name;
        if (resource.startsWith("/")) {
            name = resource.substring(1);
        } else {
            name = resource;
        }
        final InputStream stream = Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(name);
        if (stream == null) {
            throw new HttpException(
                HttpURLConnection.HTTP_NOT_FOUND,
                String.format("Resource %s not found", resource)
            );
        }
        return new RsWithBody(stream);
    }
}
