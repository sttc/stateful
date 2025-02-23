/*
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
 */
package co.stateful.rest;

import com.rexsl.page.BasePage;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Base RESTful page.
 *
 * <p>All other JAXB pages are inherited from this class, in runtime,
 * by means of {@link com.rexsl.page.PageBuilder}.
 *
 * <p>The class is mutable and NOT thread-safe.
 *
 * @since 0.1
 */
@XmlRootElement(name = "page")
@XmlAccessorType(XmlAccessType.NONE)
public class StPage extends BasePage<StPage, BaseRs> {

}
