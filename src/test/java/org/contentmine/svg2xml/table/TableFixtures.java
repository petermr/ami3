package org.contentmine.svg2xml.table;

import java.io.File;
import java.util.List;

import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.svg2xml.SVG2XMLFixtures;

import nu.xom.Element;

public class TableFixtures {

	// full table
	public final static File TABLEFILE = new File(SVG2XMLFixtures.ANALYZER_DIR, "bmc174.table1.svg");
	public final static File TDBLOCKFILE = new File(SVG2XMLFixtures.ANALYZER_DIR, "bmc174.table1.tdbody.svg");
	public final static File CELL00FILE = new File(SVG2XMLFixtures.ANALYZER_DIR, "bmc174.table1.cell.0.0.svg");
	
	public final static File HROWFILE = new File(SVG2XMLFixtures.ANALYZER_DIR, "bmc174.table1.hrow.svg");
	public final static File HROW0FILE = new File(SVG2XMLFixtures.ANALYZER_DIR, "bmc174.table1.hrow.0.svg");
	public final static File HROW1FILE = new File(SVG2XMLFixtures.ANALYZER_DIR, "bmc174.table1.hrow.1.svg");
	public final static File HROW2FILE = new File(SVG2XMLFixtures.ANALYZER_DIR, "bmc174.table1.hrow.2.svg");
	
	public static final String TEXT_OR_PATH_XPATH = ".//svg:text|//svg:path";
	public static final String PATH_XPATH = ".//svg:path";
	public static final String TEXT_XPATH = ".//svg:text";
	
	public static final Real2Range PAGE_BOX = new Real2Range(new RealRange(0., 600.), new RealRange(0., 800.));
	
	public static TableChunk createGenericChunkFromElements(File file) {
		TableChunk genericChunk = new TableChunk();
		List<SVGElement> elementList = readFileAndXPathFilterToElementList(file, TEXT_OR_PATH_XPATH);
		genericChunk.setElementList(elementList);
		return genericChunk;
	}
	
	public static TableChunk createCellFromMaskedElements(
			File file, RealRangeArray horizontalMask, RealRangeArray verticalMask) {
		List<? extends SVGElement> elementList = readFileAndXPathFilterToElementList(file, TEXT_OR_PATH_XPATH);
		elementList = SVGElement.filterHorizontally(elementList, horizontalMask);
		elementList = SVGElement.filterVertically(elementList, verticalMask);
		TableChunk genericChunk = createGenericChunkFromElements(TableFixtures.CELL00FILE);
		return genericChunk;
	}

	public static List<SVGElement> readFileAndXPathFilterToElementList(File file, String xpath) {
		Element element = XMLUtil.parseQuietlyToDocument(file).getRootElement();
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(element);
		List<SVGElement> elementList = SVGUtil.getQuerySVGElements(svgElement, xpath);
		return elementList;
	}

}
