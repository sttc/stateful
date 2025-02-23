<?xml version="1.0"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2025, Stateful.co
 * SPDX-License-Identifier: MIT
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
        <meta name="viewport" content="width=device-width,minimum-scale=1,initial-scale=1"/>
        <meta name="description" content="Stateful Web Primitives"/>
        <meta name="keywords" content="stateful.co"/>
        <meta name="author" content="www.stateful.co"/>
        <link rel="icon" type="image/gif" href="/images/favicon.ico?{version/name}"/>
        <link rel="stylesheet" href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css"/>
        <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css"/>
        <link rel="stylesheet" type="text/css" media="all" href="/css/layout.css?{version/name}"/>
        <script type="text/javascript" src="//code.jquery.com/jquery-2.0.0.js">
          <xsl:text> </xsl:text>
        </script>
        <xsl:apply-templates select="." mode="head"/>
      </head>
      <body>
        <div class="container">
          <header class="header">
            <div>
              <a href="{links/link[@rel='home']/@href}">
                <img alt="logo" class="logo" src="/images/pomegranate.svg?{version/name}"/>
              </a>
            </div>
            <xsl:choose>
              <xsl:when test="identity">
                <div class="identity">
                  <xsl:apply-templates select="identity"/>
                </div>
                <div class="token">
                  <span>
                    <code>
                      <xsl:value-of select="identity/urn"/>
                    </code>
                  </span>
                  <span>
                    <xsl:text>/</xsl:text>
                  </span>
                  <span>
                    <code>
                      <xsl:value-of select="token"/>
                    </code>
                  </span>
                  <span>
                    <a href="{links/link[@rel='user:refresh']/@href}">
                      <i class="fa fa-refresh">
                        <xsl:comment>refresh</xsl:comment>
                      </i>
                    </a>
                  </span>
                </div>
                <nav class="menu">
                  <ul class="list-inline">
                    <xsl:for-each select="links/link[contains(@rel,'menu:')]">
                      <li>
                        <xsl:variable name="label" select="substring(@rel,6)"/>
                        <xsl:choose>
                          <xsl:when test="/page/menu = $label">
                            <xsl:attribute name="class">
                              <xsl:text>active</xsl:text>
                            </xsl:attribute>
                            <xsl:value-of select="$label"/>
                          </xsl:when>
                          <xsl:otherwise>
                            <a href="{@href}">
                              <xsl:value-of select="$label"/>
                            </a>
                          </xsl:otherwise>
                        </xsl:choose>
                      </li>
                    </xsl:for-each>
                  </ul>
                </nav>
              </xsl:when>
              <xsl:otherwise>
                <div class="buttons">
                  <xsl:call-template name="buttons"/>
                </div>
              </xsl:otherwise>
            </xsl:choose>
          </header>
          <xsl:apply-templates select="flash"/>
          <section class="well well-lg content">
            <xsl:apply-templates select="." mode="body"/>
          </section>
          <footer class="footer">
            <div style="margin-bottom: 5px">
              <a href="http://www.sixnines.io/h/0841">
                <img src="http://www.sixnines.io/b/0841?style=flat"/>
              </a>
            </div>
            <div>
              <xsl:apply-templates select="version"/>
            </div>
            <div>
              <a href="https://github.com/sttc/stateful">
                <i class="fa fa-github-alt">
                  <xsl:comment>github</xsl:comment>
                </i>
              </a>
            </div>
          </footer>
        </div>
        <script type="text/javascript">
                    //<![CDATA[
                    (function(i,s,o,g,r,a,m){i.GoogleAnalyticsObject=r;i[r]=i[r]||function(){
                    (i[r].q=i[r].q||[]).push(arguments);},i[r].l=1*new Date();a=s.createElement(o),
                    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m);
                    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
                    ga('create', 'UA-1963507-35', 'stateful.co');
                    ga('send', 'pageview');
                    //]]>
                </script>
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
    <span>
      <xsl:value-of select="name"/>
    </span>
    <span>
      <xsl:call-template name="millis">
        <xsl:with-param name="millis" select="/page/millis"/>
      </xsl:call-template>
    </span>
  </xsl:template>
  <xsl:template match="identity">
    <span>
      <img class="photo" src="{photo}" alt="{name}"/>
    </span>
    <span class="name">
      <xsl:value-of select="name"/>
    </span>
    <span>
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
    </span>
    <span>
      <a href="{/page/links/link[@rel='rexsl:logout']/@href}">
        <i class="fa fa-sign-out">
          <xsl:comment>logout</xsl:comment>
        </i>
      </a>
    </span>
  </xsl:template>
  <xsl:template match="flash">
    <div>
      <xsl:attribute name="class">
        <xsl:text>alert </xsl:text>
        <xsl:choose>
          <xsl:when test="level = 'INFO'">
            <xsl:text>alert-success</xsl:text>
          </xsl:when>
          <xsl:when test="level = 'WARNING'">
            <xsl:text>alert-warning</xsl:text>
          </xsl:when>
          <xsl:when test="level = 'SEVERE'">
            <xsl:text>alert-danger</xsl:text>
          </xsl:when>
        </xsl:choose>
      </xsl:attribute>
      <xsl:value-of select="message"/>
    </div>
  </xsl:template>
  <xsl:template name="buttons">
    <a href="{/page/links/link[@rel='rexsl:facebook']/@href}">
      <i class="fa fa-facebook-square">
        <xsl:comment>facebook</xsl:comment>
      </i>
    </a>
    <xsl:text> </xsl:text>
    <a href="{/page/links/link[@rel='rexsl:google']/@href}">
      <i class="fa fa-google-plus-square">
        <xsl:comment>google-plus</xsl:comment>
      </i>
    </a>
    <xsl:text> </xsl:text>
    <a href="{/page/links/link[@rel='rexsl:github']/@href}">
      <i class="fa fa-github-square">
        <xsl:comment>github</xsl:comment>
      </i>
    </a>
  </xsl:template>
</xsl:stylesheet>
