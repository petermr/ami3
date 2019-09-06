<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<!-- creates templates from parameters (e.g. in projections.xml) -->

	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

<!--  
–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
|                               |                         |              
|       header.tableheads       |     header.graphheads   |              
|                               |                         |              
–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
|                               |                         |              
|                               |                         |              
|                               |                         |              
|                               |                         |              
|                               |                         |              
|       body.table              |     body.graph          |              
|                               |                         |              
|                               |                         |              
|                               |                         |              
|                               |                         |              
|                               |                         |              
–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––
|       footer.summary          |     footer.scale        |              
|                               |                         |              
|                               |                         |              
–––––––––––––––––––––––––––––––––––––––––––––––––––––––––––

-->
<!--  operates on projections.xml to create template.xml  
<projections cTree="PMC5911624" imageDir="image.6.2.72_532.419_592" basename="raw_s4_thr_150_ds">
 <xcoords>
  <xcoord min="1021" max="1022"/>
 </xcoords>
 <ycoords>
  <ycoord min="43" max="44"/>
  <ycoord min="413" max="414"/>
 </ycoords>
 <horizontallines>
  <g xmlns="http://www.w3.org/2000/svg">
   <line x1="0.0" y1="43.0" x2="1277.0" y2="43.0" style="stroke:black;stroke-width:1.0;"/>
  </g>
  <g xmlns="http://www.w3.org/2000/svg">
   <line x1="782.0" y1="413.0" x2="1262.0" y2="413.0" style="stroke:black;stroke-width:1.0;"/>
  </g>
 </horizontallines>
 <verticallines>
  <g xmlns="http://www.w3.org/2000/svg">
   <line x1="1021.0" y1="43.0" x2="1021.0" y2="421.0" style="stroke:black;stroke-width:1.0;"/>
   <line x1="1021.0" y1="429.0" x2="1021.0" y2="443.0" style="stroke:black;stroke-width:1.0;"/>
  </g>
 </verticallines>
</projections>
-->

<xsl:variable name="xcoords" select="projections/xcoords"/>
<xsl:variable name="ycoords" select="projections/ycoords"/>
<xsl:variable name="subImage" select="projections/subImage"/>
<xsl:variable name="horizontallines" select="projections/*[local-name()='g' and @class='horizontallines']/*[local-name()='line']"/>
<xsl:variable name="verticallines" select="projections/*[local-name()='g' and @class='verticallines']/*[local-name()='line']"/>

<xsl:variable name="y1" select="$ycoords/ycoord[1]"/>
<xsl:variable name="y2" select="$ycoords/ycoord[2]"/>
 <xsl:variable name="headerBottom" select="$y1/@min - 1"/>
 <xsl:variable name="bodyTop" select="$y1/@max + 1"/>
 <xsl:variable name="footerTop" select="$y2/@max + 1"/>
 <!--  kludge, approximate line between table/Heterogeneity; also top of tickmarks in scale  -->
 <xsl:variable name="bodyBottom" select="$y2/@min - 5"/>
<xsl:variable name="graphLeft" select="$horizontallines[2]/@x1"/>

	<xsl:template match="/">
	  <xsl:call-template name="createTemplate"/>
	</xsl:template>

	<xsl:template name="createTemplate">
	  <template>
		  	<image source="raw.png" split="horizontal" sections="header null body footer" borders="{$headerBottom} {$bodyTop} {$bodyBottom}" extension="png">
	    	  <image source="raw.header.png" split="vertical" sections="tableheads graphheads" borders="{$graphLeft}" extension="png"></image>
	    	  <image source="raw.body.png" split="vertical" sections="table graph" borders="{$graphLeft}" extension="png"></image>
	    	  <image source="raw.footer.png" split="vertical" sections="summary scale" borders="{$graphLeft}" extension="png"></image>
		   </image>
      </template>
	</xsl:template>
	
</xsl:stylesheet>