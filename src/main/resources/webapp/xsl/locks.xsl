<?xml version="1.0" encoding="UTF-8"?>
<!--
 * SPDX-FileCopyrightText: Copyright (c) 2014-2026, Stateful.co
 * SPDX-License-Identifier: MIT
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0"
                exclude-result-prefixes="xs">
  <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
  <xsl:include href="/xsl/layout.xsl"/>
  <xsl:template match="page" mode="head">
    <title>
      <xsl:text>locks</xsl:text>
    </title>
  </xsl:template>
  <xsl:template match="page" mode="body">
    <h1>
      <xsl:text>Locks</xsl:text>
    </h1>
    <div class="col-12 col-sm-8 col-lg-6 clearfix" style="padding-left:0;">
      <form method="post" class="form-inline" action="{links/link[@rel='lock']/@href}">
        <fieldset>
          <div class="input-group">
            <input name="name" type="text" class="form-control" placeholder="name of a new lock"/>
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
      <xsl:when test="locks/lock">
        <table class="table clearfix">
          <thead>
            <tr>
              <th style="width:80%;">
                <xsl:text>name/label</xsl:text>
              </th>
              <th>
                <xsl:text>opts</xsl:text>
              </th>
            </tr>
          </thead>
          <tbody>
            <xsl:apply-templates select="locks/lock"/>
          </tbody>
        </table>
      </xsl:when>
      <xsl:otherwise>
        <p class="clearfix">
          <xsl:text>There are no locks in your account.</xsl:text>
        </p>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:value-of select="documentation" disable-output-escaping="yes"/>
  </xsl:template>
  <xsl:template match="lock">
    <tr>
      <td>
        <xsl:value-of select="name"/>
        <br/>
        <xsl:value-of select="label"/>
      </td>
      <td>
        <a href="{/page/links/link[@rel='unlock']/@href}?name={name}">
          <i class="fa fa-trash-o">
            <xsl:comment>empty</xsl:comment>
          </i>
        </a>
      </td>
    </tr>
  </xsl:template>
</xsl:stylesheet>
