<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:svg="http://www.w3.org/2000/svg">
	 <xsl:param name="progType">spss</xsl:param>  
	<!-- creates templates from parameters (e.g. in projections.xml) -->

	<xsl:output method="xml" version="1.0" encoding="UTF-8"
		indent="yes" />

	<!-- <xsl:variable name="external-doc" select="@href"/> <xsl:variable name="projections" 
		select="document('${directory}/projections.xml')"/> -->

	<!-- operates on projections.xml to create template.xml <projections cTree="PMC6240665" 
		imageDir="image.6.1.114_482.90_331" basename="raw_s4_thr_150_ds"> <xcoords> 
		<xcoord min="699" max="700"/> </xcoords> <ycoords> <ycoord min="949" max="950"/> 
		</ycoords> <g class="verticallines" xmlns="http://www.w3.org/2000/svg"> <line 
		x1="699.0" y1="264.0" x2="699.0" y2="965.0" style="stroke:blue;stroke-width:2.0;"/> 
		</g> <g class="horizontallines" xmlns="http://www.w3.org/2000/svg"> <line 
		x1="5.0" y1="949.0" x2="1523.0" y2="949.0" style="stroke:red;stroke-width:2.0;"/> 
		</g> </projections> -->

	<xsl:variable name="TICK">
		15
	</xsl:variable>
	<xsl:variable name="ylines">
		twolines
	</xsl:variable>

	<xsl:variable name="xcoords" select="projections/xcoords" />
	<xsl:variable name="ycoords" select="projections/ycoords" />
	<xsl:variable name="subImage" select="projections/subImage" />
	<xsl:variable name="horizontallines"
		select="projections/svg:g[@class='horizontallines']" />
	<xsl:variable name="verticallines"
		select="projections/svg:g[@class='verticallines']" />

	<xsl:variable name="ylines">
		twolines
	</xsl:variable>

	<!-- the vertical line is an indicator of Y coord -->
	<!-- it has a tick below it so is too long -->
	<xsl:variable name="verty1" select="$verticallines[1]/svg:line/@y1" />
	<xsl:variable name="verty2" select="$verticallines[1]/svg:line/@y2" />
	<xsl:variable name="lenverty">
		<xsl:value-of select="$verty2 - $verty1" />
	</xsl:variable>
	<!-- x may be valuable for the ticks -->
	<xsl:variable name="vertx1" select="$verticallines[1]/svg:line/@x1" />

	<!-- this is used if there are good horizontal lines -->

	<xsl:variable name="hory1" select="$horizontallines[1]/svg:line/@y1" />
	<xsl:variable name="hory2" select="$horizontallines[2]/svg:line/@y2" />

	<xsl:variable name="subImageCount" select="count($subImage/x)" />
	<xsl:variable name="epsx">
		3
	</xsl:variable>
	<xsl:variable name="sx1" select="$subImage/x[1]" />
	<xsl:variable name="sx2" select="$subImage/x[2]" />
	<xsl:variable name="sx3" select="$subImage/x[3]" />
	<xsl:variable name="sx1m" select="($sx1/@max + $sx1/@min) div 2" />
	<xsl:variable name="sx2m" select="($sx2/@max + $sx2/@min) div 2" />
	<xsl:variable name="sx3m" select="($sx3/@max + $sx3/@min) div 2" />

	<xsl:template match="/">
		<xsl:call-template name="createTemplate" ></xsl:call-template>
	</xsl:template>

	<xsl:template name="createTemplate">
		<!-- absolute diffs from $vertx1 -->

		<xsl:variable name="bodyTop">
			<xsl:choose>
				<xsl:when test="$hory1 and $hory2">
					<xsl:value-of select="$hory1 + 1" />
				</xsl:when>
				<xsl:when test="$verty1 and $verty2">
					<xsl:value-of select="$verty1 + 1" />
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="tickTop">
			<xsl:choose>
				<xsl:when test="$hory1 and $hory2">
					<xsl:value-of select="$hory2 + 1" />
				</xsl:when>
				<xsl:when test="$verty1 and $verty2">
					<xsl:value-of select="$verty2 - $TICK" />
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="scaleTop">
			<xsl:choose>
				<xsl:when test="$verty2">
					<xsl:value-of select="$verty2 + 1" />
				</xsl:when>
				<xsl:when test="$tickTop">
					<xsl:value-of select="$tickTop + $TICK" />
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="graphLeft">
			<xsl:choose>
				<xsl:when test="$subImageCount = 3">
					<xsl:value-of select="number($sx1m)" />
				</xsl:when>
				<xsl:when test="$subImageCount = 2">
					<xsl:choose>
						<!-- middle tick is lowest -->
						<xsl:when test="$epsx > $sxabs1">
							<xsl:value-of select="$sx1m + $sx1m - $sx2m" />
						</xsl:when>
						<!-- middle tick is highest -->
						<xsl:when test="$epsx > $sxabs2">
							<xsl:value-of select="$sx1m" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$sx1m" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="graphRight">
			<xsl:choose>
				<xsl:when test="$subImageCount = 3">
					<xsl:value-of select="number($sx3m)" />
				</xsl:when>
				<xsl:when test="$subImageCount = 2">
					<xsl:choose>
						<!-- middle tick is lowest -->
						<xsl:when test="$epsx > $sxabs1">
							<xsl:value-of select="$sx2m" />
						</xsl:when>
						<!-- middle tick is highest -->
						<xsl:when test="$epsx > $sxabs2">
							<xsl:value-of select="$sx2m + $sx2m - $sx1m" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$sx2m" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:if test="$subImageCount = 2">
			<xsl:message>
				"ONLY TWO TICKS "
				<xsl:value-of select="number($sx1m)" />
				?
				<xsl:value-of select="number($sx2m)" />
				x1
				<xsl:value-of select="$vertx1" />
			</xsl:message>
		</xsl:if>
		<xsl:if test="$subImageCount = 1">
			<xsl:message>
				ONLY ONE TICK
				<xsl:value-of select="$sx1" />
			</xsl:message>
		</xsl:if>

		<template>
			<image source="raw.png" split="horizontal" sections="header body ticks scale"
				borders="{$bodyTop} {$tickTop} {$scaleTop}" extension="png">
				<image source="raw.body.png" split="vertical" sections="ltable graph rtable"
					borders="{$graphLeft} {$graphRight}" extension="png">
				</image>
			</image>
		</template>
	</xsl:template>

	<xsl:template name="math_abs">
		<xsl:param name="x" />

		<xsl:choose>
			<xsl:when test="$x &lt; 0">
				<xsl:value-of select="$x * -1" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$x" />
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

</xsl:stylesheet>