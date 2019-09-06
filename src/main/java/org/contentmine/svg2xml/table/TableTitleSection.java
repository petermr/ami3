package org.contentmine.svg2xml.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGTitle;
import org.contentmine.svg2xml.util.GraphPlot;

/** manages the table header, including trying to sort out the column spanning
 * 
 * @author pm286
 *
 */
public class TableTitleSection extends TableSection {
	static final String TITLE_TITLE = "title.title";
	static final Logger LOG = Logger.getLogger(TableTitleSection.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public TableTitleSection() {
		super(TableSectionType.TITLE);
	}
	
	public TableTitleSection(TableSection tableSection) {
		super(tableSection);
	}
	
	public SVGElement createMarkedContent(
			SVGElement svgChunk,
			String[] colors,
			double[] opacity) {
			SVGG g = createBoxAndShiftToOrigin(svgChunk, colors, opacity);
			svgChunk.appendChild(g);
			return svgChunk;
	}
	
	private SVGG createBoxAndShiftToOrigin(SVGElement svgChunk, String[] colors, double[] opacity) {
		SVGG g = new SVGG();
		g.setSVGClassName(TITLE_TITLE);
		if (boundingBox == null) {
			LOG.trace("no bounding box");
		} else {
			String title = "TITLE: "+this.getFontInfo()+" //" +this.getStringValue();
			SVGTitle svgTitle = new SVGTitle(title);
			SVGElement plotBox = GraphPlot.createBoxWithFillOpacity(boundingBox, colors[0], opacity[0]);
			plotBox.appendChild(svgTitle);
			g.appendChild(plotBox);
			TableContentCreator.shiftToOrigin(svgChunk, g);
		}
		return g;
	}
}
