<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://www.w3.org/1999/xhtml">

	<xsl:template match="/">
		<doc xmlns:mml="http://www.w3.org/1998/Math/MathML" xmlns:xlink="http://www.w3.org/1999/xlink">
		<xsl:apply-templates/>
		</doc>
	</xsl:template>

<!--  omit text leaves -->
	<xsl:template match="text()"/>
	
	<xsl:template match="sec">
	  <xsl:choose>
		<xsl:when test="@sec-type='materials|methods' or @sec-type='methods'">
		   <sec sectype="{@sec-type}">
		    <xsl:copy-of select="@*|*|node()"/>
		   </sec>
	     </xsl:when>
		 <xsl:when test="not(normalize-space(@sec-type)='')">
	 		<xsl:message>SEC:<xsl:value-of select="@sec-type"/>:</xsl:message>
	 		<xsl:message>TIT:<xsl:value-of select="title"/>:</xsl:message>
	 	</xsl:when>
		 <xsl:when test="not(@sec-type)">
	 		<xsl:message>noSEC TIT:<xsl:value-of select="title"/>:</xsl:message>
	 	</xsl:when>
	  </xsl:choose>
	</xsl:template> 
	
	
</xsl:stylesheet>
