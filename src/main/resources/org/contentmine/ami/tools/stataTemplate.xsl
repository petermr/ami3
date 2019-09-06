<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- creates templates from parameters (e.g. in projections.xml) -->

	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

<!--
<xsl:variable name="external-doc" select="@href"/>
	<xsl:variable name="projections" select="document('${directory}/projections.xml')"/>
  -->

<!--  operates on projections.xml to create template.xml  
<projections cTree="PMC6320077" imageDir="image.5.1.122_554.409_699" basename="raw_thr_230_ds">
 <xcoords>
  <xcoord min="564" max="567"/>
 </xcoords>
 <ycoords>
  <ycoord min="231" max="232"/>
  <ycoord min="742" max="746"/>
 </ycoords>
 <subImage>
  <x min="391" max="394"/>
  <x min="563" max="568"/>
  <x min="737" max="742"/>
  <y min="0" max="0"/>
 </subImage>
</projections>
-->
<xsl:variable name="xcoords" select="projections/xcoords"/>
<xsl:variable name="ycoords" select="projections/ycoords"/>
<xsl:variable name="subImage" select="projections/subImage"/>
<xsl:variable name="y1" select="$ycoords/ycoord[1]"/>
<xsl:variable name="y2" select="$ycoords/ycoord[2]"/>
<xsl:variable name="sx1" select="$subImage/x[1]"/>
<xsl:variable name="sx2" select="$subImage/x[2]"/>
<xsl:variable name="sx3" select="$subImage/x[3]"/>
<xsl:variable name="bodyTop" select="$y1/@max + 1"/>
<xsl:variable name="scaleTop" select="$y2/@max + 1"/>
<xsl:variable name="graphLeft" select="$sx1/@max"/>
<xsl:variable name="graphRight" select="$sx3/@max"/>

	<xsl:template match="/">
	  <xsl:call-template name="createTemplate"/>
	</xsl:template>

	<xsl:template name="createTemplate">
	  <template>
		  	<image source="raw.png" split="horizontal" sections="header body scale" borders="{$bodyTop} {$scaleTop}" extension="png">
	    	  <image source="raw.body.png" split="vertical" sections="ltable graph rtable" borders="{$graphLeft} {$graphRight}" extension="png">
	    	  </image>
		   </image>
      </template>
	</xsl:template>
	
</xsl:stylesheet>