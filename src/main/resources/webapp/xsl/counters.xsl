<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
  <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
  <xsl:include href="/webapp/xsl/layout.xsl"/>
  <xsl:template match="page" mode="head">
    <title>
      <xsl:text>counters</xsl:text>
    </title>
    <script type="text/javascript" src="/js/counters.js?{version/name}">
      <xsl:text> </xsl:text>
      <!-- this is for W3C compliance -->
    </script>
  </xsl:template>
  <xsl:template match="page" mode="body">
    <h1>
      <xsl:text>Atomic Counters</xsl:text>
    </h1>
    <div class="col-12 col-sm-8 col-lg-6 clearfix" style="padding-left:0;">
      <form method="post" class="form-inline" action="{links/link[@rel='add']/@href}">
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
        <table class="table clearfix">
          <thead>
            <tr>
              <th style="width:40%;">
                <xsl:text>name</xsl:text>
              </th>
              <th style="width:50%;">
                <xsl:text>value</xsl:text>
              </th>
              <th>
                <xsl:text>opts</xsl:text>
              </th>
            </tr>
          </thead>
          <tbody>
            <xsl:apply-templates select="counters/counter"/>
          </tbody>
        </table>
      </xsl:when>
      <xsl:otherwise>
        <p class="clearfix">
          <xsl:text>There are no counters in your account.</xsl:text>
        </p>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:value-of select="documentation" disable-output-escaping="yes"/>
  </xsl:template>
  <xsl:template match="counter">
    <tr>
      <td>
        <xsl:value-of select="name"/>
      </td>
      <td>
        <div class="input-group counter">
          <input type="text" class="form-control" value="?" data-href-set="{links/link[@rel='set']/@href}" data-href-increment="{links/link[@rel='increment']/@href}"/>
          <span class="input-group-btn">
            <button class="refresh btn btn-default" type="button">
              <i class="fa fa-refresh">
                <xsl:comment>refresh</xsl:comment>
              </i>
            </button>
            <button class="increment btn btn-default" type="button">
              <i class="fa fa-plus">
                <xsl:comment>plus</xsl:comment>
              </i>
            </button>
            <button class="save btn btn-default" type="button">
              <i class="fa fa-save">
                <xsl:comment>save</xsl:comment>
              </i>
            </button>
          </span>
        </div>
      </td>
      <td>
        <a href="{links/link[@rel='delete']/@href}">
          <i class="fa fa-trash-o">
            <xsl:comment>empty</xsl:comment>
          </i>
        </a>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
