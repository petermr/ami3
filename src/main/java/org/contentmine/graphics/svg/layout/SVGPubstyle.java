package org.contentmine.graphics.svg.layout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGDefs;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.PageCache;
import org.contentmine.graphics.svg.layout.SVGPubstyleColumn.ColumnPosition;

import nu.xom.Element;
import nu.xom.Nodes;

/** per publisher pubstyle.
 * 
<g id="pubstyle" pubstyle="bmc" publisher="BioMedCentral" doi="10.1186">
 * pages are numbered from 1
	<g id="page1" number="P1">
		<g id="header">
			<rect x="0" width="540" y="0" height="47" />
			<text id="biblio" fontName="MyriadPro-It" y="38.469" x="450"
				fontStyle="italic|normal" fontWeight="boldnormal">
				<![CDATA[(?<auth>)(\{\d+\)\s+(\d+:\d+)})]]> </text>
			<!-- DOI 10.1186/s12936-017-1948-z -->
			<text id="doi" fontName="MyriadPro-It" y="48.469" fontStyle="italic|normal"
				fontWeight="boldnormal"><![CDATA[(DOI (10\.1186/s\d+\-\d+\-\d+\-z))]]> </text>
			<text id="journalName" fontName="" fontSize="16.2" fontWeight="normal"
				y="47.307" />
			<title justify="left" wrap="true" fontSize="99" suscript="true"
				font="" />
		</g>

		<g id="abstract">
			<text level="2" x="63.122" fontSize="10.3" fontWeight="bold">Abstract
			</text>
			<g id="abs.section">
				<text level="3" x="63.122" fontSize="10.0" fontWeight="bold">
					(Background|Methods|Results|Discussion|Keywords):
				</text>
				<!-- running text -->
				<text level="3" fontSize="10.0" fontWeight="bold">ANY</text>
			</g>
		</g>
		<g id="page2" number="P2">
			<g id="header">
				<rect x="0" width="540" y="0" height="47" />
				<text id="biblio" fontName="MyriadPro-*" y="38.469" x="450" fontStyle="(italic|normal)" fontWeight="(bold|normal)">
					<![CDATA[(?<authors>)(\{\d+\)\s+(\d+:\d+)})]]></text>
				<text id="pages" justify="right" fontName="MyriadPro-*" fontSize="8.0" fontWeight="normal"
					y="47.307" ><![CDATA[(Page?<page>\s+\d+\s+of\s+\d+)]]></text>
<!--
<text fontName="MyriadPro-Regular" y="38.72" x="56.694,...96.198" 
style="fill:#000000;font-family:Helvetica;font-size:8.0px;font-weight:normal;">Sumarnrote </text>
<text fontName="MyriadPro-It" y="38.472" x="97.894,...,183.869" 
style="fill:#000000;font-family:Helvetica;font-size:8.0px;font-style:italic;font-weight:normal;">et al. Malar J  (2017) 16:299 </text>
<text fontName="MyriadPro-Regular" y="38.984" x="498.15,...513.0" 
style="fill:#000000;font-family:Helvetica;font-size:8.0px;font-weight:normal;">Page 2 of 13</text>
-->					
			</g>

			<g id="wideimage">
				<!-- diagram -->
				<!-- background? -->
				<path style="fill:none;stroke-width:1.0;stroke:#000000;" signature="MLLLZ"
					d="M57.068 486.972 L538.958 486.972 L538.958 94.092 L57.068 94.092 Z" />

				<image xmlns:xlink="http://www.w3.org/1999/xlink"
					transform="matrix(0.240,-0.0,-0.0,0.240,106.67,94.09)" xlink:href="fulltext.p4.i1.png"
					x="0.0" y="0.0" width="1595.0" height="1637.0" />
				<!-- rounded corners rect -->
				<path style="stroke:blue;stroke-width:2.25;fill:yellow;"
					d="M61.068 88.092 
	    C61.068 88.092 57.068 88.092 57.068 92.092 L57.068 519.852 
	    C57.068 519.852 57.068 523.852 61.068 523.852 L534.958 523.852 
	    C534.958 523.852 538.958 523.852 538.958 519.852 L538.958 92.092 
	    C538.958 92.092 538.958 88.092 534.958 88.092 L61.068 88.092 Z"
					signature="MCLCLCLCLZ" />


			</g>

			<g id="widetable">
				<!-- diagram -->
				<!-- table rules -->
				<path style="stroke:#000000;stroke-width:0.15;" d="M56.693 572.729 L538.583 572.729 " />
				<path style="stroke:#000000;stroke-width:0.15;" d="M56.693 703.503 L538.583 703.503 " />

				<!-- background? -->
				<path style="fill:none;stroke-width:1.0;stroke:red;" signature="MLLLZ"
					d="M57.068 486.972 L538.958 486.972 L538.958 94.092 L57.068 94.092 Z" />


			</g>

			<g id="left">
				<rect x="55" width="238" y="82" height="639" />
				<text fontName="MyriadPro-Bold"
					style="fill:#000000;font-family:Helvetica;font-size:10.3px;font-weight:bold;">Background</text>
				<text fontName="WarnockPro-Regular" x="56.693 ... 290.57"
					style="fill:#000000;font-size:9.8px;font-weight:normal;">In 2015, ... countries </text>
			</g>
			<g id="right">
				<rect x="302" width="238" y="82" height="639" />
				<text fontName="WarnockPro-Regular" y="94.917" x="304.721...531.08"
					style="fill:#000000;font-size:9.8px;font-weight:normal;">only found in Ho Chi Minh City [</text>
			</g>
			<g id="footer">
			</g>
		</g>
	</g>
</g>

 * @author pm286
 *
 */
public class SVGPubstyle extends AbstractPubstyle {



	public enum PageType {
		PANY,
		P1,
		P2,
		PN
	}
	private static final Logger LOG = Logger.getLogger(SVGPubstyle.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String PUBSTYLE = "pubstyle";
	private static final String PUBSTYLE_NAME = "pubstyleName";
	public final static String CLASSNAME = PUBSTYLE;
	// CLASS, ID defined above
	private static final String IDREF = "idref";
	private static final String IDREF_ELEMENTS = ".//*[@" + IDREF + "]";
	private static final String IDCLASS = CLASS;
	
	private static final String NUMBER = "number";
	private static final String ABSTRACT = SVGPubstyleAbstract.SVG_CLASSNAME;
//	private static final String ABSTRACT_XPATH = ".//*[@" + NUMBER + "='"+PageType.P1+"']//*[@" + IDCLASS + "='" + ABSTRACT + "']";
	private static final String ABSTRACT_SECTION = "abstract.section";
//	private static final String ABSTRACT_SECTION_XPATH = ".//*[@" + IDCLASS + "='" + ABSTRACT_SECTION + "']";
	private static final String DOI = "doi";
//	private static final String DOI_XPATH = "./*/@" + DOI;
	private static final String FOOTER =  SVGPubstyleFooter.SVG_CLASSNAME;
//	private static final String FOOTER_XPATH = "./*[@" + IDCLASS + "='" + FOOTER + "']";
	private static final String HEADER =  SVGPubstyleHeader.SVG_CLASSNAME;
//	private static final String HEADER_XPATH = ".//*[@" + IDCLASS + "='" + HEADER + "']";
//	private static final String HEADER_XPATH1 = ".//*[contains(concat(' ',@" + IDCLASS + ",' '),concat(' '," + HEADER + ",' '))]";
	private static final String LEFT =  SVGPubstyleLeftColumnOLD.SVG_CLASSNAME;
//	private static final String LEFT_XPATH = "./*[@" + IDCLASS + "='" + LEFT + "']";
	private static final String MIDDLE = SVGPubstyleMiddleColumnOLD.SVG_CLASSNAME;
//	private static final String MIDDLE_XPATH = "./*[@" + IDCLASS + "='" + MIDDLE + "']";
//	private static final String PUBSTYLE_XPATH = "./*/@" + PUBSTYLE;
	private static final String PUBLISHER = "publisher";
//	private static final String PUBLISHER_XPATH = "./*/@" + PUBLISHER;
	private static final String RIGHT = SVGPubstyleRightColumnOLD.SVG_CLASSNAME;
//	private static final String RIGHT_XPATH = "./*[@" + IDCLASS + "='" + RIGHT + "']";
	private static final String WIDE = "wide";
//	private static final String WIDE_XPATH = "./*[@" + IDCLASS + "='" + WIDE + "']";
	private static final String WIDE_IMAGE =  SVGPubstyleWideImage.SVG_CLASSNAME;
//	private static final String WIDE_IMAGE_XPATH = ".//*[@" + IDCLASS + "='" + WIDE_IMAGE + "']";
	private static final String WIDE_TABLE =  SVGPubstyleWideTable.SVG_CLASSNAME;
//	private static final String WIDE_TABLE_XPATH = ".//*[@" + IDCLASS + "='" + WIDE_TABLE + "']";


	private PubstyleManager pubstyleManager ;
	private int endPage;
	int currentPage;
	String dirRoot;

	private SVGPubstyle() {
		
	}

	public SVGPubstyle(SVGElement svgElement, PubstyleManager pubstyleManager) {
		this();
		this.pubstyleManager = pubstyleManager;
		if (svgElement instanceof SVGSVG) {
			this.copyAttributesChildrenElements(svgElement);
		} else {
			this.appendChild(svgElement.copy());
		}
		annotateAsPubstyle();
	}

	private void annotateAsPubstyle() {
		List<SVGElement> elements = SVGElement.extractSelfAndDescendantElements(this);
		for (SVGElement element : elements) {
			element.addSVGClassName(PUBSTYLE);
		}
	}
	
	/** number can be 1, 2, last
	 * 
	 * @param pageNumber
	 * @return
	 */
	public SVGElement getRawPage(PageType type) {
		String xpath = ".//*[@" + NUMBER + "='"+type+"']";
		SVGElement page = (SVGElement)XMLUtil.getSingleElement(this, xpath);
		return page;
	}

	public String getPublisher() {
		return this.getFirstG().getAttributeValue(PUBLISHER);
	}
	
	public String getPubstyleName() {
		return this.getFirstG().getAttributeValue(PUBSTYLE_NAME);
	}
	
	private Element getFirstG() {
		Nodes nodes= this.query("./*[local-name()='"+SVGG.TAG+"']");
		Element element = nodes.size() != 1 ? null : (Element) nodes.get(0);
		LOG.trace("firstG "+element.toXML().substring(0, 100));
		return element;
	}

	public String getDoi() {
//		return this.getSingleValueWithClassValue(DOI);
		return this.getFirstG().getAttributeValue(DOI);
	}

	public SVGPubstyleAbstract getAbstract() {
		SVGElement element = this.getSingleElementWithClassValue(ABSTRACT);
		if (element == null) {
			LOG.debug("null abstract "+this.toXML().substring(0,  100)+" ...");
		}
		return element == null ? null : new SVGPubstyleAbstract(element);
	}
	
	public SVGPubstyleFooter getFooter(PageType type) {
		SVGElement page = getRawPage(type);
		SVGElement element = page == null ? null : page.getSingleElementWithClassValue(FOOTER);
		debugNullElement(FOOTER, element);
		return element == null ? null : new SVGPubstyleFooter(element);
	}

	public SVGPubstyleHeader getHeader(PageType type) {
		SVGElement page = getRawPage(type);
		SVGElement element = page == null ? null : page.getSingleElementWithClassValue(HEADER);
		return element == null ? null : new SVGPubstyleHeader(element);
	}

	/** returns PubstyleColumn for column position
	 * 
	 * @param type of page
	 * @param columnPosition currently WIDE, LEFT, MIDDLE, RIGHT
	 * @return
	 */
	public SVGPubstyleColumn getColumn(PageType type, ColumnPosition columnPosition) {
		SVGElement page = getRawPage(type);
		String column = null;
		if (ColumnPosition.LEFT.equals(columnPosition)) {
			column = LEFT;
		} else if (ColumnPosition.MIDDLE.equals(columnPosition)) {
			column = MIDDLE;
		} else if (ColumnPosition.RIGHT.equals(columnPosition)) {
			column = RIGHT;
		} else if (ColumnPosition.WIDE.equals(columnPosition)) {
			column = WIDE;
		} else {
			LOG.debug("NULL column xpath: "+columnPosition);
		}
		SVGElement element = page == null ? null : page.getSingleElementWithClassValue(column);
		SVGPubstyleColumn pubstyleColumn = element == null ? null : new SVGPubstyleColumn(element);
		if (pubstyleColumn != null) {
			pubstyleColumn.setXPath(column);
			pubstyleColumn.setContainingPubstyle(this);
			LOG.trace("COLUMN "+column);
		}
		return pubstyleColumn;
	}

	public SVGPubstylePage getPubstylePage(PageType type) {
		SVGElement page = getRawPage(type);
		return page == null ? null : new SVGPubstylePage(page);
	}

	public SVGElement getAbstractSection() {
		SVGElement page = getRawPage(PageType.P1);
		SVGElement abstractElement = page == null ? null : page.getSingleElementWithClassValue(ABSTRACT_SECTION);
		debugNullElement(ABSTRACT_SECTION, abstractElement);
		return abstractElement;
	}
	
	public SVGPubstyleWideImage getWideImage(PageType type) {
		SVGElement page = getRawPage(type);
		SVGElement element = page == null ? null : page.getSingleElementWithClassValue(WIDE_IMAGE);
		debugNullElement(WIDE_IMAGE, element);
		return element == null ? null : new SVGPubstyleWideImage(element);
	}
	
	public SVGPubstyleWideTable getWideTable(PageType type) {
		SVGElement page = getRawPage(type);
		SVGElement element = page == null ? null : page.getSingleElementWithClassValue(WIDE_TABLE);
		debugNullElement(WIDE_TABLE, element);
		return element == null ? null : new SVGPubstyleWideTable(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return CLASSNAME;
	}

	/** resolve idrefs
	 * 
	 */
	public void normalize() {
		int ibreak = 0; // safety for bug
		while (true) {
			if (ibreak++ > 10) {
				throw new RuntimeException("BUG loops for ever");
			}
			List<Element> idrefs = XMLUtil.getQueryElements(this, IDREF_ELEMENTS);
			if (idrefs.size() == 0) {
				break;
			}
			for (Element element : idrefs) {
				replaceIdrefByCopyOfId(element);
			}
		}
		detachDefsElement();
		SVGSVG.wrapAndWriteAsSVG(this, new File("target/pubstyle/pubstyle2.svg"));
	}

	private void detachDefsElement() {
		List<SVGElement> svgElements = SVGUtil.getQuerySVGElements(this, ".//*[local-name()='" + SVGDefs.TAG + "']");
		if (svgElements.size() == 1) {
			for (SVGElement svgElement : svgElements) {
				svgElement.detach();
			}
		}
	}

	private void replaceIdrefByCopyOfId(Element idrefElement) {
		String idref = idrefElement.getAttributeValue(IDREF);
		String xpath = "//*[@" + ID + "='"+idref+"']";
		Element idElement = XMLUtil.getSingleElement(this, xpath);
		if (idElement == null) {
			throw new RuntimeException("Cannot find target id of: "+idref);
		}
		Element idTargetElement = (Element) idElement.copy();
		idTargetElement.removeAttribute(idTargetElement.getAttribute(ID));
		idrefElement.getParent().replaceChild(idrefElement, idTargetElement);
	}

	public void setEndPage(int end) {
		this.endPage = end;
	}

	public void setCurrentPage(int page) {
		this.currentPage = page;
	}
	
	public ColumnPosition[] getColumnPositions() {
		ColumnPosition[] columnPositions;
		if (currentPage == 1) {
			columnPositions = new ColumnPosition[] {ColumnPosition.WIDE, ColumnPosition.LEFT, ColumnPosition.RIGHT};
		} else if (currentPage >= endPage - 2) {
			columnPositions = new ColumnPosition[] {ColumnPosition.WIDE, ColumnPosition.LEFT, ColumnPosition.RIGHT};
		} else {
			columnPositions = new ColumnPosition[] {ColumnPosition.WIDE, ColumnPosition.LEFT, ColumnPosition.RIGHT};
		}
		return columnPositions;
	}

	public PageType getPageType() {
		PageType pageType = PageType.PANY;
		if (currentPage == 1) {
			pageType = PageType.P1;
		} else if (currentPage >= endPage - 2) {
			pageType = PageType.PN;				
		} else {
			pageType = PageType.P2;
		}
		return pageType;
	}

	public List<DocumentChunk> createDocumentChunks(PageCache pageCache) {
		List<DocumentChunk> documentChunks = new ArrayList<DocumentChunk>();
		ColumnPosition[] columnPositions = getColumnPositions();
		for (ColumnPosition columnPosition : columnPositions) {
			SVGPubstyleColumn pubstyleColumn = getColumn(getPageType(), columnPosition);
			if (pubstyleColumn == null) {
				LOG.error("null pubstyleColumn "+getPageType()+"; "+columnPosition);
				continue;
			} 
			List<DocumentChunk> documentChunks1 = pubstyleColumn.extractDocumentChunksInBox(pageCache);
			documentChunks.addAll(documentChunks1);
		}
		return documentChunks;
	}

	public List<DocumentChunk> createDocumentChunks(SVGElement inputSVGElement) {
		List<DocumentChunk> documentChunks = new ArrayList<DocumentChunk>();
		ColumnPosition[] columnPositions = getColumnPositions();
		for (ColumnPosition columnPosition : columnPositions) {
			SVGPubstyleColumn pubstyleColumn = getColumn(getPageType(), columnPosition);
			if (pubstyleColumn == null) {
				LOG.error("null pubstyleColumn "+getPageType()+"; "+columnPosition);
				continue;
			} 
			List<DocumentChunk> documentChunks1 = pubstyleColumn.extractDocumentChunksInBox(inputSVGElement);
			documentChunks.addAll(documentChunks1);
		}
		return documentChunks;
	}

	public void setDirRoot(String dirRoot) {
		this.dirRoot = dirRoot;
	}

	// ===========================================
	private void debugNullElement(String clazz, SVGElement element) {
		if (element == null) {
			LOG.trace("xml for null "+clazz+ this.toXML().substring(0,  100)+" ...");
		}
	}



}
