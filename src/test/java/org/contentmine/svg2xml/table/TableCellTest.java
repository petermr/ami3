package org.contentmine.svg2xml.table;

import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGUtil;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Element;

/** 
 * test for CellChunk
 * @author pm286
 *
 */
public class TableCellTest {

	private final static Logger LOG = Logger.getLogger(TableCellTest.class);
	
	@Test
	public void dummy() {
		LOG.trace("CellChunkTest NYI");
	}

	@Test
	public void testTDCellValue() {
		TableChunk cellChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.CELL00FILE);
		String value = cellChunk.getValue();
		Assert.assertEquals("value", "IN61", value);
	}

	@Test
	public void testTDCellValue00() {
		RealRangeArray horizontal0Mask = new RealRangeArray();
		horizontal0Mask.add(new RealRange(75., 93.));
		RealRangeArray vertical0Mask = new RealRangeArray();
		vertical0Mask.add(new RealRange(120., 130.));
		TableChunk cellChunk = TableFixtures.createCellFromMaskedElements(TableFixtures.TDBLOCKFILE, horizontal0Mask, vertical0Mask);
		String value = cellChunk.getValue();
		Assert.assertEquals("value", "IN61", value);
	}

	@Test
	public void testTH2ChunkValue() {
		TableChunk cellChunk = TableFixtures.createGenericChunkFromElements(TableFixtures.HROW2FILE);
		String value = cellChunk.getValue();
		Assert.assertEquals("value", "MLT(min)", value);
	}

	@Test
	public void testTH0ChunkValue() {
		TableChunk cellChunk = new TableChunk();
		Element element = XMLUtil.parseQuietlyToDocument(TableFixtures.HROW0FILE).getRootElement();
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(element);
		List<SVGElement> elementList = SVGUtil.getQuerySVGElements(svgElement, TableFixtures.TEXT_OR_PATH_XPATH);
		cellChunk.setElementList(elementList);
		String value = cellChunk.getValue();
		Assert.assertEquals("value", "Strain", value);
	}
	
	
}
