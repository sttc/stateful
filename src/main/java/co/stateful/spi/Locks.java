/*
 * Copyright (c) 2014-2025, Stateful.co
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
package co.stateful.spi;

import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.Map;

/**
 * Locks.
 *
 * @since 1.1
 */
@Immutable
public interface Locks {

    /**
     * Maximum allowed per account.
     */
    int MAX = 4096;

    /**
     * Get list of them all, and their labels.
     * @return List of locks
     * @throws IOException If fails
     */
    Map<String, String> names() throws IOException;

    /**
     * Lock it.
     * @param name Unique name of the lock
     * @param label Label to attach
     * @return Empty if success or a label of a current lock
     * @throws IOException If fails
     */
    String lock(String name, String label) throws IOException;

    /**
     * Read label.
     * @param name Unique name of the lock
     * @return Empty if it doesn't exist, or a label
     * @throws IOException If fails
     */
    String label(String name) throws IOException;

    /**
     * Unlock it.
     * @param name Unique name of the lock
     * @throws IOException If fails
     */
    void unlock(String name) throws IOException;

    /**
     * Unlock only if label matches.
     * @param name Unique name of the lock
     * @param label Label to match
     * @return Empty if success or label of current lock
     * @throws IOException If fails
     * @since 1.6
     */
    String unlock(String name, String label) throws IOException;

}
