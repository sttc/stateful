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
    <xsl:template match="/">
        <xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
        <xsl:apply-templates select="page"/>
    </xsl:template>
    <xsl:template match="page">
        <html lang="en">
            <head>
                <meta charset="UTF-8"/>
                <meta name="description" content="Stateful Web Primitives"/>
                <meta name="keywords" content="stateful.co"/>
                <meta name="author" content="www.stateful.co"/>
                <link rel="stylesheet" type="text/css" media="all">
                    <xsl:attribute name="href">
                        <xsl:text>/css/screen.css?</xsl:text>
                    </xsl:attribute>
                </link>
                <link rel="icon" type="image/gif">
                    <xsl:attribute name="href">
                        <xsl:text>http://img.stateful.co/logo-128x128.png</xsl:text>
                    </xsl:attribute>
                </link>
                <link rel="stylesheet" href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css"/>
                <xsl:call-template name="head"/>
            </head>
            <body>
                <div class="container">
                    <header class="header">
                        <div>
                            <img alt="logo" class="logo">
                                <xsl:attribute name="src">
                                    <xsl:text>http://img.stateful.co/logo-256x256.png</xsl:text>
                                </xsl:attribute>
                            </img>
                        </div>
                        <div>
                            <xsl:choose>
                                <xsl:when test="identity">
                                    <xsl:apply-templates select="identity"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:call-template name="buttons"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </div>
                    </header>
                    <section>
                        <xsl:call-template name="content"/>
                    </section>
                    <footer class="footer">
                        <div>
                            <xsl:text>(c) stateful.co, all rights reserved</xsl:text>
                        </div>
                        <div>
                            <xsl:apply-templates select="version"/>
                        </div>
                    </footer>
                </div>
            </body>
        </html>
    </xsl:template>
    <xsl:template name="millis">
        <xsl:param name="millis" as="xs:integer"/>
        <xsl:choose>
            <xsl:when test="$millis &gt; 1000">
                <xsl:value-of select="format-number($millis div 1000, '0.0')"/>
                <xsl:text>s</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="format-number($millis, '#')"/>
                <xsl:text>ms</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template match="version">
        <xsl:value-of select="name"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="revision"/>
        <xsl:text> </xsl:text>
        <xsl:call-template name="millis">
            <xsl:with-param name="millis" select="/page/millis"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template match="identity">
        <img class="photo">
            <xsl:attribute name="src">
                <xsl:value-of select="photo"/>
            </xsl:attribute>
            <xsl:attribute name="alt">
                <xsl:value-of select="name"/>
            </xsl:attribute>
        </img>
        <span class="name">
            <xsl:value-of select="name"/>
        </span>
        <i>
            <xsl:attribute name="class">
                <xsl:text>auth fa </xsl:text>
                <xsl:choose>
                    <xsl:when test="starts-with(urn, 'urn:facebook:')">
                        <xsl:text>fa-facebook-square</xsl:text>
                    </xsl:when>
                    <xsl:when test="starts-with(urn, 'urn:google:')">
                        <xsl:text>fa-google-plus-square</xsl:text>
                    </xsl:when>
                    <xsl:when test="starts-with(urn, 'urn:github:')">
                        <xsl:text>fa-github-square</xsl:text>
                    </xsl:when>
                </xsl:choose>
            </xsl:attribute>
            <xsl:comment>icon</xsl:comment>
        </i>
        <a>
            <xsl:attribute name="href">
                <xsl:value-of select="/page/links/link[@rel='rexsl:logout']/@href"/>
            </xsl:attribute>
            <i class="fa fa-exit"><xsl:comment>logout</xsl:comment></i>
        </a>
    </xsl:template>
    <xsl:template match="flash">
        <div class="flash">
            <xsl:attribute name="class">
                <xsl:value-of select="level"/>
            </xsl:attribute>
            <xsl:value-of select="message"/>
        </div>
    </xsl:template>
    <xsl:template name="buttons">
        <a>
            <xsl:attribute name="href">
                <xsl:value-of select="/page/links/link[@rel='rexsl:facebook']/@href"/>
            </xsl:attribute>
            <i class="fa fa-facebook-square"><xsl:comment>facebook</xsl:comment></i>
        </a>
        <xsl:text> </xsl:text>
        <a>
            <xsl:attribute name="href">
                <xsl:value-of select="/page/links/link[@rel='rexsl:google']/@href"/>
            </xsl:attribute>
            <i class="fa fa-google-plus-square"><xsl:comment>google-plus</xsl:comment></i>
        </a>
        <xsl:text> </xsl:text>
        <a>
            <xsl:attribute name="href">
                <xsl:value-of select="/page/links/link[@rel='rexsl:github']/@href"/>
            </xsl:attribute>
            <i class="fa fa-github-square"><xsl:comment>github</xsl:comment></i>
        </a>
    </xsl:template>
</xsl:stylesheet>
