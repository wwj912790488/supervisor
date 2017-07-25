<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
<xsl:template match="/contents">
<contents>
<xsl:attribute name="type"><xsl:value-of select="@type" /></xsl:attribute>
<xsl:apply-templates select="item">
</xsl:apply-templates>
</contents>
</xsl:template>
    
<xsl:template match="item">
<item>
<xsl:attribute name="type"><xsl:value-of select="@type" /></xsl:attribute>
<name><xsl:value-of select="name" /></name>
<uri><xsl:value-of select="uri" /></uri>
</item>
</xsl:template>

</xsl:stylesheet>