/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.fake;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import org.takes.Request;

/**
 * Request with XML Accept header for testing.
 *
 * <p>Wraps a request with Accept: text/xml header.
 * Usage example:
 * <pre>{@code
 * Request req = new RqXml(new RqFake());
 * }</pre>
 *
 * @since 2.0
 */
public final class RqXml implements Request {

    /**
     * Origin request.
     */
    private final Request origin;

    /**
     * Ctor.
     * @param req Origin request
     */
    public RqXml(final Request req) {
        this.origin = req;
    }

    @Override
    public Iterable<String> head() throws IOException {
        final List<String> head = new LinkedList<>();
        for (final String line : this.origin.head()) {
            head.add(line);
        }
        head.add("Accept: text/xml");
        return head;
    }

    @Override
    public InputStream body() throws IOException {
        return this.origin.body();
    }
}
