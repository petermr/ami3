package org.contentmine.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGTitle;
import org.contentmine.graphics.svg.text.build.Phrase;
import org.contentmine.svg2xml.util.GraphPlot;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** manages the table header, including trying to sort out the column spanning
 * 
 * @author pm286
 *
 */
public class TableBodySection extends TableSection {
	static final String BODY_CELL_BOXES = "body.cellBoxes";
	private static final String BODY_COLUMN_BOXES = "body.columnBoxes";
	private static final String BODY_SUBTABLE_BOXES = "body.subtableBoxes";
	static final Logger LOG = Logger.getLogger(TableBodySection.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<RealRange> indentRangeArray;
	private ColumnManager columnManager0;
		
	public TableBodySection(TableSection tableSection) {
		super(tableSection);
	}
	
	public void createHeaderRowsAndColumnGroups() {
		getOrCreateAllPhrasesInSection();
		createSortedColumnManagerListFromUnassignedPhrases(allPhrasesInSection);
		alignColumns();
		createIndentArray();
	}

	private List<RealRange> createIndentArray() {
		if (columnManagerList == null || columnManagerList.size() == 0) {
			return new ArrayList<RealRange>();
		}
		columnManager0 = columnManagerList.get(0);
		indentRangeArray = new ArrayList<RealRange>();
		RealArray indentArray = columnManager0.getOrCreateIndentArray();
		boolean inIndent = false;
		double startRange = 0;
		double endRange = 0;
		Phrase phrasei = null;
		for (int i = 0; i < indentArray.size(); i++) {
			phrasei = columnManager0.getPhrase(i);
			if (phrasei.getStringValue().trim().length() == 0) {
				LOG.trace("EMPTY phrase");
				continue;
			}
			double xIndent = indentArray.get(i);
			// FIXME simple single indent ATM 
			if (xIndent > epsilon) {
				if (!inIndent) {
					startRange = phrasei.getY();
				}
				endRange = phrasei.getY();
				inIndent = true;
				LOG.trace("indent "+phrasei.getY()+"/"+phrasei.getStringValue());
			} else {
				LOG.trace("unindent "+phrasei.getStringValue());
				endRange = phrasei.getY();
				if (inIndent) {
					RealRange range = new RealRange(startRange, endRange);
					indentRangeArray.add(range);
				}
				inIndent = false;
			}
		}
		if (inIndent) {
			endRange = boundingBox.getYMax();
			indentRangeArray.add(new RealRange(startRange, endRange));
		}
		return indentRangeArray;
	}

	private void alignColumns() {
		double fontSize = 8.0;
		double lastY = boundingBox == null ? 0.0 : boundingBox.getYMin();
		for (int i = 0; i < columnManagerList.size(); i++) {
			columnManagerList.get(i).resetYPointer();
		}
		RealArray yCoordArray = getYCoordinatesForPhrases();
		for (int j = 0; j < yCoordArray.size(); j++) {
			double y = yCoordArray.elementAt(j);
			RealRange yRange = new RealRange(lastY, y);
			for (int i = 0; i < columnManagerList.size(); i++) {
				ColumnManager columnManager = columnManagerList.get(i);
				RealRange xRange = new RealRange(columnManager.getEnclosingRange());
				columnManager.addCell(new Real2Range(xRange, yRange), fontSize);
			}
			lastY = y;
		}
		for (int i = 0; i < columnManagerList.size(); i++) {
			/*RealArray indentArray = */columnManagerList.get(i).getOrCreateIndentArray();
		}
	}

	private RealArray getYCoordinatesForPhrases() {
		RealArray yCoordArray = new RealArray();
		Multiset<Double> yCoordSet = HashMultiset.create();
		for (int i = 0; i < allPhrasesInSection.size(); i++) {
			yCoordSet.add(allPhrasesInSection.get(i).getY());
		}
		Iterable<Entry<Double>> yCoords = MultisetUtil.getEntriesSortedByValue(yCoordSet);
		for (Entry<Double> yCoord : yCoords) {
			yCoordArray.addElement(yCoord.getElement());
		}
		return yCoordArray;
	}

	public SVGElement createMarkedSections(
			SVGElement svgChunk,
			String[] colors,
			double[] opacity) {
		// write SVG
		AbstractCMElement g;
		g = createSubtableBoxesAndShiftToOrigin(svgChunk, colors, opacity);
		svgChunk.appendChild(g);
		g = createColumnBoxesAndShiftToOrigin(svgChunk, colors, opacity);
		svgChunk.appendChild(g);
		g = createCellBoxesAndShiftToOrigin(svgChunk, colors, opacity);
		svgChunk.appendChild(g);
		return svgChunk;
	}

	private SVGG createColumnBoxesAndShiftToOrigin(SVGElement svgChunk, String[] colors, double[] opacity) {
		SVGG g = new SVGG();
		g.setSVGClassName(BODY_COLUMN_BOXES);
		if (boundingBox == null) {
			LOG.trace("no bounding box");
		} else {
			for (int i = 0; i < columnManagerList.size(); i++) {
				ColumnManager columnManager = columnManagerList.get(i);
				Real2Range colManagerBox = new Real2Range(new RealRange(columnManager.getEnclosingRange()), boundingBox.getYRange());
				String title = "BODYCOLUMN: "+i+"/"+columnManager.getStringValue();
				SVGTitle svgTitle = new SVGTitle(title);
				SVGElement plotBox = GraphPlot.createBoxWithFillOpacity(colManagerBox, colors[1], opacity[1]);
				plotBox.appendChild(svgTitle);
				g.appendChild(plotBox);
			}
			TableContentCreator.shiftToOrigin(svgChunk, g);
		}
		return g;
	}
	
	private SVGG createCellBoxesAndShiftToOrigin(SVGElement svgChunk, String[] colors, double[] opacity) {
		SVGG g = new SVGG();
		g.setSVGClassName(BODY_CELL_BOXES);
		if (boundingBox == null) {
			LOG.trace("no bounding box");
		} else {
			for (int icol = 0; icol < columnManagerList.size(); icol++) {
				ColumnManager columnManager = columnManagerList.get(icol);
				SVGG gg = columnManager.createCellBoxes(icol, colors, opacity);
				g.appendChild(gg);
			}
			TableContentCreator.shiftToOrigin(svgChunk, g);
		}
		return g;
	}
	
	private SVGG createSubtableBoxesAndShiftToOrigin(SVGElement svgChunk, String[] colors, double[] opacity) {
		SVGG g = new SVGG();
		g.setSVGClassName(BODY_SUBTABLE_BOXES);
		if (boundingBox == null) {
			LOG.trace("no bounding box");
		} else {
			// for a single indent
			if (columnManager0 != null) {
				double xIndent = columnManager0.getMinIndent() - columnManager0.getMaxIndent();
				RealRange xRange = boundingBox.getXRange();
				xRange.extendLowerEndBy(xIndent);
				for (int i = 0; i < indentRangeArray.size(); i++) {
					Real2Range subTable = new Real2Range(xRange, indentRangeArray.get(i));
					SVGElement plotBox = GraphPlot.createBoxWithFillOpacity(subTable, colors[i % colors.length], opacity[i % opacity.length]);
					g.appendChild(plotBox);
				}
			}
			TableContentCreator.shiftToOrigin(svgChunk, g);
		}
		return g;
	}
	
	

}
