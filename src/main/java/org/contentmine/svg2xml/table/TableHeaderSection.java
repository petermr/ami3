package org.contentmine.svg2xml.table;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGTitle;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalElement;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalRule;
import org.contentmine.graphics.svg.text.build.Phrase;
import org.contentmine.graphics.svg.text.build.PhraseChunk;
import org.contentmine.svg2xml.util.GraphPlot;

/** manages the table header, including trying to sort out the column spanning
 * 
 * @author pm286
 *
 */
public class TableHeaderSection extends TableSection {
	static final String HEADER_BOXES = "header.boxes";
	static final String HEADER_COLUMN_BOXES = "header.columnBoxes";
	static final Logger LOG = Logger.getLogger(TableHeaderSection.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<HeaderRow> headerRowList;
	public TableHeaderSection() {
		super(TableSectionType.HEADER);
	}
	
	public TableHeaderSection(TableSection tableSection) {
		super(tableSection);
	}

	public void createHeaderRowsAndColumnGroups() {
		// assume this is sorted by Y; form raw colgroups and reorganize later
		createHeaderRowListAndUnassignedPhrases();
		createSortedColumnManagerListFromUnassignedPhrases(allPhrasesInSection);
	}

	private List<Phrase> createHeaderRowListAndUnassignedPhrases() {
		allPhrasesInSection = null;
		headerRowList = new ArrayList<HeaderRow>();
		Double lastY = null;
		HeaderRow headerRow = null;
		for (HorizontalElement element : getHorizontalElementList()) {
			if (element instanceof PhraseChunk) {
				if (allPhrasesInSection == null) {
					allPhrasesInSection = new ArrayList<Phrase>();
				}
				PhraseChunk phraseList = (PhraseChunk) element;
				allPhrasesInSection.addAll(phraseList.getOrCreateChildPhraseList());
			} else if (element instanceof HorizontalRule) {
				HorizontalRule ruler = (HorizontalRule) element;
				Double y = ruler.getY();
				if (lastY == null || (y - lastY) > HorizontalRule.Y_TOLERANCE) {
					headerRow = new HeaderRow();
					headerRowList.add(headerRow);
					lastY = y;
				}
				ColumnGroup columnGroup = new ColumnGroup();
				IntRange rulerRange = ruler.getIntRange();
				for (int i = allPhrasesInSection.size() - 1; i >= 0; i--) {
					Phrase phrase = allPhrasesInSection.get(i);
					// somewhere above the ruler (ignore stacked rulers at this stage
					if (rulerRange.includes(phrase.getIntRange()) && phrase.getY() < ruler.getY()) {
						allPhrasesInSection.remove(i);
						columnGroup.add(phrase);
						columnGroup.add(ruler);
						headerRow.add(columnGroup);
					}
				}
			}
		}
		return allPhrasesInSection;
	}

	public List<HeaderRow> getOrCreateHeaderRowList() {
		if (headerRowList == null) {
			headerRowList = new ArrayList<HeaderRow>();
		}
		return headerRowList;
	}
	
	public SVGElement createMarkedSections(
			SVGElement svgChunk,
			String[] colors,
			double[] opacity) {
		// write SVG
		SVGG g = createColumnBoxesAndTransformToOrigin(svgChunk, colors, opacity);
		svgChunk.appendChild(g);
		g = createHeaderBoxesAndTransformToOrigin(svgChunk, colors, opacity);
		svgChunk.appendChild(g);
		return svgChunk;
	}

	private SVGG createColumnBoxesAndTransformToOrigin(SVGElement svgChunk, String[] colors, double[] opacity) {
		SVGG g = new SVGG();
		g.setSVGClassName(HEADER_COLUMN_BOXES);
		if (boundingBox == null) {
			LOG.trace("no bounding box");
		} else {
			RealRange yRange = boundingBox.getYRange();
			for (int i = 0; i < columnManagerList.size(); i++) {
				ColumnManager columnManager = columnManagerList.get(i);
				IntRange range = columnManager.getEnclosingRange();
				if (range != null) {
					RealRange xRange = new RealRange(range);
					ColumnGroup colGroup = nearestCoveringColumnGroup(xRange);
					RealRange yRange1 = colGroup == null ? yRange : 
						new RealRange(colGroup.getBoundingBox().getYRange().getMax(), yRange.getMax());
					String title = "HEADERCOLUMN: "+i+"/"+columnManager.getStringValue();
					SVGTitle svgTitle = new SVGTitle(title);
					SVGElement plotBox = GraphPlot.createBoxWithFillOpacity(new Real2Range(xRange, yRange1), colors[1], opacity[1]);
					plotBox.appendChild(svgTitle);
					g.appendChild(plotBox);
				}
			}
			TableContentCreator.shiftToOrigin(svgChunk, g);
		}
		return g;
	}

	private ColumnGroup nearestCoveringColumnGroup(RealRange xRange) {
		ColumnGroup columnGroup = null;
		double ymax = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < headerRowList.size(); i++) {
			HeaderRow headerRow = headerRowList.get(i);
			for (ColumnGroup colGroup : headerRow.getOrCreateColumnGroupList()) {
				Real2Range bbox = colGroup.getBoundingBox();
				RealRange colGroupXRange = bbox.getXRange();
				if (colGroupXRange.intersects(xRange)) {
					if (bbox.getYMax() > ymax) {
						ymax = bbox.getYMax();
						columnGroup = colGroup;
					}
				}
			}
		}
		return columnGroup;
	}

	private SVGG createHeaderBoxesAndTransformToOrigin(SVGElement svgChunk, String[] colors, double[] opacity) {
		SVGG g = new SVGG();
		g.setSVGClassName(HEADER_BOXES);
		for (int i = 0; i < headerRowList.size(); i++) {
			HeaderRow headerRow = headerRowList.get(i);
			for (ColumnGroup columnGroup : headerRow.getOrCreateColumnGroupList()) {
				Real2Range bbox = columnGroup.getBoundingBox();
				SVGElement plotBox = GraphPlot.createBoxWithFillOpacity(bbox, colors[1], opacity[1]);
                                List<Phrase> colGroupPhrases = columnGroup.getPhrases();
                                String colGroupPhraseString = "";
                                for (Phrase phrase : colGroupPhrases) {
                                    colGroupPhraseString += phrase.getPrintableString();
                                }
                                
				String title = "HEADERBOX: "+i+"/"+colGroupPhraseString;
                                
				SVGTitle svgTitle = new SVGTitle(title);
				plotBox.appendChild(svgTitle);
				g.appendChild(plotBox);
			}
		}
		TableContentCreator.shiftToOrigin(svgChunk, g);
		return g;
	}

}
