<?xml version="1.0"?>
<!--
 * Copyright (c) 2014, stateful.co
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
 -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns="http://www.w3.org/1999/xhtml" version="2.0" exclude-result-prefixes="xs">
    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template match="page" mode="head">
        <title>
            <xsl:text>counters</xsl:text>
        </title>
        <script type="text/javascript">
            <xsl:attribute name="src">
                <xsl:text>/js/counters.js?</xsl:text>
                <xsl:value-of select="version/name"/>
            </xsl:attribute>
            <xsl:text> </xsl:text>
            <!-- this is for W3C compliance -->
        </script>
    </xsl:template>
    <xsl:template match="page" mode="body">
        <h1>
            <xsl:text>Atomic Counters</xsl:text>
        </h1>
        <div class="col-12 col-sm-8 col-lg-6 clearfix" style="padding-left:0;">
            <form method="post" class="form-inline">
                <xsl:attribute name="action">
                    <xsl:value-of select="links/link[@rel='add']/@href"/>
                </xsl:attribute>
                <fieldset>
                    <div class="input-group">
                        <input name="name" type="text" class="form-control" placeholder="name of a new counter"/>
                        <span class="input-group-btn">
                            <button type="submit" class="btn btn-primary">
                                <xsl:text>Add</xsl:text>
                            </button>
                        </span>
                    </div>
                </fieldset>
            </form>
        </div>
        <xsl:choose>
            <xsl:when test="counters/counter">
                <table class="table">
                    <thead>
                        <tr>
                            <th style="width:40%;"><xsl:text>name</xsl:text></th>
                            <th style="width:50%;"><xsl:text>value</xsl:text></th>
                            <th><xsl:text>opts</xsl:text></th>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:apply-templates select="counters/counter"/>
                    </tbody>
                </table>
                <p>
                    <xsl:text>
                        Counters are accessible through RESTful API. There
                        are two operations availables on every counter:
                    </xsl:text>
                    <code>set</code><xsl:text> and </xsl:text><code>inc</code><xsl:text>.</xsl:text>
                    <code>Set</code><xsl:text> returns nothing, while </xsl:text><code>inc</code>
                    <xsl:text>
                        returns current value of the counter.
                        In order to read without changing
                        you just increment by zero. An increment with
                        a negative value will decrement the counter.
                    </xsl:text>
                </p>
                <p>
                    <xsl:text>
                        For example, in order to set the first counter in your list to 123, you
                        make a PUT request:
                    </xsl:text>
                    <code>
                        <xsl:value-of select="counters/counter[1]/links/link[@rel='set']/@href"/>
                        <xsl:text>?value=123</xsl:text>
                    </code>
                </p>
                <p>
                    <xsl:text>
                        In order to increment the same counter by 54, you
                        make a GET request:
                    </xsl:text>
                    <code>
                        <xsl:value-of select="counters/counter[1]/links/link[@rel='increment']/@href"/>
                        <xsl:text>?value=54</xsl:text>
                    </code>
                </p>
            </xsl:when>
            <xsl:otherwise>
                <p class="clearfix">
                    <xsl:text>You don't have any counters yet...</xsl:text>
                </p>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="counter">
        <tr>
            <td><xsl:value-of select="name"/></td>
            <td>
                <div class="input-group counter">
                    <input type="text" class="form-control" value="?">
                        <xsl:attribute name="data-href-set">
                            <xsl:value-of select="links/link[@rel='set']/@href"/>
                        </xsl:attribute>
                        <xsl:attribute name="data-href-increment">
                            <xsl:value-of select="links/link[@rel='increment']/@href"/>
                        </xsl:attribute>
                    </input>
                    <span class="input-group-btn">
                        <button class="refresh btn btn-default" type="button">
                            <i class="fa fa-refresh"><xsl:comment>refresh</xsl:comment></i>
                        </button>
                        <button class="increment btn btn-default" type="button">
                            <i class="fa fa-plus"><xsl:comment>plus</xsl:comment></i>
                        </button>
                        <button class="save btn btn-default" type="button">
                            <i class="fa fa-save"><xsl:comment>save</xsl:comment></i>
                        </button>
                    </span>
                </div>
            </td>
            <td>
                <a>
                    <xsl:attribute name="href">
                        <xsl:value-of select="links/link[@rel='delete']/@href"/>
                    </xsl:attribute>
                    <i class="fa fa-trash-o"><xsl:comment>empty</xsl:comment></i>
                </a>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
