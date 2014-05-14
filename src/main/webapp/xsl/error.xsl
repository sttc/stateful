<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" version="2.0">
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    <xsl:include href="/xsl/layout.xsl"/>
    <xsl:template match="page" mode="head">
        <title>
            <xsl:text>error</xsl:text>
        </title>
    </xsl:template>
    <xsl:template match="page" mode="body">
        <h1>
            <xsl:text>Page not found</xsl:text>
        </h1>
        <p>
            <xsl:text>
                Invalid request or page not found. Perhaps the link
                you have is expired. It's also possible that there is
                a bug in our system. Don't hesitate to report it
                to
            </xsl:text>
            <a href="https://github.com/sttc/stateful/issues">
                <xsl:text>our issues list in Github</xsl:text>
            </a>
            <xsl:text>.</xsl:text>
        </p>
        <p style="text-align:center;">
            <img src="//img.stateful.co/404.svg" style="width:450px;"/>
        </p>
    </xsl:template>
</xsl:stylesheet>
