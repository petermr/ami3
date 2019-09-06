<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://www.w3.org/1999/xhtml">

	<xsl:import href="../normami/src/main/resources/org/contentmine/norma/pubstyle/util/toHtml.xsl"/>

	<!--  BMC -->
    <xsl:variable name="publisher">BioMed Central Ltd</xsl:variable>
    <xsl:variable name="prefix">10.1186</xsl:variable>
    <xsl:variable name="publisherSelector">//*[local-name()='meta' and
      (
      (@name='dc.publisher' and @content='BioMed Central Ltd') or 
      (@name='citation_doi' and contains(@content,concat('10.1186','/')))
      )
    ]</xsl:variable>
		
<!--  tags to omit -->
        <xsl:template match="
        h:script
        | h:noscript
        | h:style
        | h:link
        | comment()
        | h:div[@id='oas-campaign']
        | h:div[@id='oas-positions']
        | h:div[@id='branding']
        | h:div[@id='branding-inner']
        | h:div[@id='left-article-box']
        | h:div[@id='right-panel']
        | h:div[@id='article-alert-signup-div']
        | h:div[@id='footer']
        | h:div[@id='springer']
        | h:div[@class='hide' and h:dl[@class='google-ad']]
        ">
        <!-- 
        <xsl:message>SKIP</xsl:message>
        -->
        </xsl:template>

<!-- wrappers to skip -->
	<!--  WRAPPERS -->
	<xsl:template match="h:div[
	    @id='membership-message-loader-desktop' or
        @class='c-journal-header__inner' or
	    contains(@class,'u-container') or 
	    contains(@class,'c-content-layout--fulltext') or
	    contains(@class,'c-journal-footer__inner') or
	    contains(@class,'c-journal-footer__summary') or
	    h:h4span[@class='c-journal-title__text'] or
	    contains(@class,'CollapseSection') or
	    @class='Categories' or
	    (@class='FulltextWrapper' and (h:div='Research' or h:div='Open Access')) or
	    false()
	    ]">
	    <xsl:message>UNWRAP <xsl:value-of select="@class|@id"/></xsl:message>
	  <xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="h:section[
        @id='CopyrightMessage'
	    ]">
	    <xsl:message>UNWRAP H4 <xsl:value-of select="@class|@id"/></xsl:message>
	  <xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="h:h4[
        contains(@class,'c-journal-title__footer')
	    ]">
	    <xsl:message>UNWRAP H4 <xsl:value-of select="@class|@id"/></xsl:message>
	  <xsl:apply-templates/>
	</xsl:template>
	

<!--  DELETE -->	
	<xsl:template match="h:img[
        @class='tracker'
	    ]">
	    <xsl:message>DELETE IMG <xsl:value-of select="@class|@id"/></xsl:message>
	  <xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="h:h2[
        normalize-space(.)='Copyright'
	    ]">
	    <xsl:message>DELETE H4 <xsl:value-of select="@class|@id"/></xsl:message>
	</xsl:template>
	
	<xsl:template match="h:div[
	    @class='line-gutter-backdrop' or
	    (@class='p-separator' and h:h3[.='Metrics']) or
	    @class='aside_UNKNOWN' or
	    (@class='FulltextWrapper' and h:section//h:h2[.='Comments'])  or
	    @class='c-journal-footer__contact' or
	    @class='c-publisher-footer' or
	    contains(@class,'adsbox') or
	    contains(@class,'c-content-layout__nav') or
        contains(@class,'c-journal-header__identity') or
        @class='u-visually-hidden' or
        @class='c-navbar c-navbar--secondary' or
        false()
	    ]">
	    <xsl:message>DELETE DIV-CLASS <xsl:value-of select="@class"/></xsl:message>
	</xsl:template>
	
	<xsl:template match="h:a[
	    h:span='Skip to content'
	    ]">
	    <xsl:message>A-SPAN <xsl:value-of select="."/></xsl:message>
	</xsl:template>

	<xsl:template match="h:div[
	    @id='publisher-header-search'
	    ]">
	    <xsl:message>ID-CLASS <xsl:value-of select="@class"/></xsl:message>
	</xsl:template>

	<xsl:template match="h:div[
	    @aria-label='Publisher footer links' 
	    ]">
	    <xsl:message>ARIA <xsl:value-of select="@aria-label"/></xsl:message>
	</xsl:template>

	<xsl:template match="h:div[
	    @itemscope='http://schema.org/Organization' 
	    ]">
	    <xsl:message>ITEMSCOPE <xsl:value-of select="@itemscope"/></xsl:message>
	</xsl:template>

	<xsl:template match="h:div[
	    @class='svg_UNKNOWN' 
	    ]">
	    <xsl:message>SVG <xsl:value-of select="@class"/></xsl:message>
	</xsl:template>

<!-- 
	<xsl:template match="h:div[
	    @role='navigation' 
	    ]">
	    <xsl:message>NAVIGATION <xsl:value-of select="@class"/></xsl:message>
	</xsl:template>
-->

	<xsl:template match="h:nav">
	    <xsl:message>NAV <xsl:value-of select="@class"/></xsl:message>
	</xsl:template>
	
	<!--  table stuff? -->
	<xsl:template match="h:div[@class='col_UNKNOWN' or @class='colgroup_UNKNOWN']">
<!-- 
	    <xsl:message>COL <xsl:value-of select="@class"/></xsl:message>
-->
	    <xsl:apply-templates/>
	</xsl:template>
	
	<!-- 
	<div class="FulltextWrapper"> <section id="comments" class="Section1 RenderAsSection1 inline-comments"> <h2 class="Heading" data-component="collapse-fulltext">Comments</h2>
	<div class="basic-white-logo c-navbar__logo"
-->	
	<!-- 
	<xsl:template match="h:div[@class='FulltextWrapper']">
	  <xsl:copy>
	    <xsl:message>COPY</xsl:message>
	    <xsl:apply-templates select="@*|node()"/>
	  </xsl:copy>
	</xsl:template>
	-->
	
	
</xsl:stylesheet>
