package org.contentmine.graphics.svg.plot.forest;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RangeScaler;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.plot.AbstractPlotBox;

/** reads vector/text SVG and creates a Forest Plot.
 * 
 * @author pm286
 *
 */
public class ForestPlot extends AbstractPlotBox {

	private static final Logger LOG = Logger.getLogger(ForestPlot.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	// most variables are in superclass
	
	public ForestPlot() {
		setDefaults();
	}

	protected void setDefaults() {
		super.setDefaults();
		setOutputDir(new File("target/forest"));
		setDiamondEps(0.5);
		setNonAxialEps(0.1);
	}

	public void readCacheAndAnalyze(File inputDir, String fileRoot) {
		this.fileRoot = fileRoot;
		getRangeScaler();
		File infile = new File(inputDir, fileRoot + SVG);
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(infile);
		componentCache = createCaches(svgElement);
		getOrCreatePolygonsAndLines();
				
		debugSVGElements();
	}

	protected void getOrCreatePolygonsAndLines() {
		this.getOrCreateRhombList();
		this.getOrCreateHorizontalLineList();
		horizontalLines = SVGLine.mergeParallelLines(horizontalLines, lineMergeEps);
		this.getOrCreateVerticalLineList();
		verticalLines = SVGLine.mergeParallelLines(verticalLines, lineMergeEps);
		this.createShapeElement();
		this.createRectElement();
		this.createRhombElement();
	}

	private void debugSVGElements() {
		SVGSVG.wrapAndWriteAsSVG(getOrCreateRhombList(), getPolyListFile());
		SVGSVG.wrapAndWriteAsSVG(getOrCreateHorizontalLineList(), getHorizontalLinesFile());
		SVGSVG.wrapAndWriteAsSVG(getOrCreateVerticalLineList(), getVerticalLinesFile());
		SVGSVG.wrapAndWriteAsSVG(createShapeElement(), createRectsFile());
		SVGSVG.wrapAndWriteAsSVG(createPolygonElement(), getPolygonsFile());
		SVGSVG.wrapAndWriteAsSVG(componentCache.getOrCreateConvertedSVGElement(), getCacheFile());
	}

	private void getRangeScaler() {
		if (rangeScaler == null) {
			rangeScaler = new RangeScaler();
			rangeScaler.setInputRange(new RealRange(235, 364));
			rangeScaler.setOutputRange(new RealRange(-1.0,1.0)); // logarithmic
		}
	}

	

	

	// ============== diagnostic files =======
	
	
	
	
	
// create lists of SVGElements

	

	// ======== helpers ======
	
	

	// getters and setters 

	public void setApplyScale(boolean applyScale) {
		this.applyScale = applyScale;
	}

	@Override
	protected void readAndCreateCSVPlot(AbstractCMElement svgElement) {
		throw new RuntimeException("NYI");
	}

	


}
