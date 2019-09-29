<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="html" />

	<xsl:template match="/">
		<html>
			<head>
				<style>
					* {
					bold : bold;
					}
					code {
					font-family : courier;
					font-weight :
					bold;
					font-size : 14pt;
					}
				</style>
			</head>
			<body>
				<xsl:apply-templates select="argList" />
			</body>
		</html>
	</xsl:template>

	<xsl:template match="argList">
		<div>
			<h1>
				<tt>
					<xsl:value-of select="@name" />
					(
					<xsl:value-of select="@version" />
					)
				</tt>
			</h1>
			<h2>Arguments</h2>
			<ul>
				<xsl:apply-templates select="arg" />
			</ul>
			<xsl:apply-templates select="help" />
			<xsl:apply-templates select="examples" />

			<xsl:if test="value">
				<h3>Name-values</h3>
				<ul>
					<xsl:apply-templates select="value" />
				</ul>
			</xsl:if>
		</div>
	</xsl:template>

	<xsl:template match="arg">
		<li>
			<h2>
				<tt>
					<xsl:value-of select="@name" />
				</tt>
			</h2>
			<code>
				<xsl:value-of select="@long" />
				<xsl:text> </xsl:text>
				<xsl:value-of select="@args" />
				<xsl:value-of select="@countRange" />
			</code>
			<br />
			<xsl:if test="@pattern or @valueRange">
				<code>
					Constraints:
					<xsl:text> </xsl:text>
					<xsl:value-of select="@pattern or @valueRange" />
				</code>
			</xsl:if>
			<xsl:if test="@default">
				<code>
					Default:
					<xsl:text> </xsl:text>
					<xsl:value-of select="@default" />
				</code>
			</xsl:if>
			<xsl:if
				test="@finalMethod or @initMethod or @outputMethod or @parseMethod or @runMethod">
				<em>Methods: </em>
				<xsl:if test="@initMethod">
					<xsl:text> init: </xsl:text>
					<xsl:value-of select="@initMethod" />
				</xsl:if>
				<xsl:if test="@parseMethod">
					<xsl:text> parse: </xsl:text>
					<xsl:value-of select="@parseMethod" />
				</xsl:if>
				<xsl:if test="@runMethod">
					<xsl:text> run: </xsl:text>
					<xsl:value-of select="@runMethod" />
				</xsl:if>
				<xsl:if test="@outputMethod">
					<xsl:text> output: </xsl:text>
					<xsl:value-of select="@outputMethod" />
				</xsl:if>
				<xsl:if test="@finalMethod">
					<xsl:text> final: </xsl:text>
					<xsl:value-of select="@finalMethod" />
				</xsl:if>
				<br />
			</xsl:if>
			<xsl:if test="@brief or @class">
				<br />
				<em>
					<xsl:text>  b: </xsl:text>
				</em>
				<code>
					<xsl:value-of select="@brief" />
				</code>
				<em>
					<xsl:text>  c: </xsl:text>
				</em>
				<code>
					<xsl:value-of select="@class" />
				</code>
			</xsl:if>
			<p>
				<xsl:apply-templates select="help" />
			</p>

		</li>
	</xsl:template>

	<!-- } else if (FORBIDDEN.equals(namex)) { } else if (REQUIRED.equals(namex)) 
		{ -->

	<xsl:template match="examples">
		<xsl:apply-templates select="p[@class='note']" />
		<h3>Examples</h3>
		<ul>
			<xsl:apply-templates select="example" />
		</ul>
	</xsl:template>

	<xsl:template match="example">
		<li>
			<p>
				<xsl:apply-templates select="input" />
				<xsl:text>;    </xsl:text>
				<xsl:apply-templates select="output" />
			</p>
			<xsl:apply-templates select="desc" />
			<xsl:apply-templates select="command" />
		</li>
	</xsl:template>

	<xsl:template match="input">
		<span class="bold">Input: </span>
		<code>
			<xsl:copy-of select="node()" />
		</code>
	</xsl:template>

	<xsl:template match="output">
		<span class="bold">Output: </span>
		<code>
			<xsl:copy-of select="node()" />
		</code>
	</xsl:template>

	<xsl:template match="desc">
		<p>
			<span class="desc">
				<xsl:copy-of select="node()" />
			</span>
		</p>
	</xsl:template>

	<xsl:template match="command">
		<p>
			<code>
				<xsl:copy-of select="node()" />
			</code>
		</p>
	</xsl:template>

	<xsl:template match="p[@class='note']">
		<p>
			NOTE:
			<xsl:copy-of select="." />
		</p>
	</xsl:template>

	<xsl:template match="value">
		<li>
			Name:
			<xsl:value-of select="@name" />
			value:
			<xsl:value-of select="@value" />
		</li>
		li>
	</xsl:template>

	<xsl:template match="help">
		<h3>Description</h3>
		<xsl:copy-of select="." />
	</xsl:template>

</xsl:stylesheet>
