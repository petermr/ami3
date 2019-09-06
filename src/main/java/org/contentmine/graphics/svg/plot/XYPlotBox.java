package org.contentmine.graphics.svg.plot;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.cache.ComponentCache;

/** holds components of an xy plot.
 * Currently does xyPlots
 * creates axes from ticks, scales, titles.
 * 
 * @author pm286
 *
 */
public class XYPlotBox extends AbstractPlotBox {
	
	public static final Logger LOG = Logger.getLogger(XYPlotBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	static final String MINOR_CHAR = "i";
	static final String MAJOR_CHAR = "I";
	

	public XYPlotBox() {
		setDefaults();
	}
	
	protected void extractDataScreenPoints() {
		screenXYs = new Real2Array();
		for (SVGCircle circle : componentCache.getOrCreateShapeCache().getCircleList()) {
			screenXYs.addElement(circle.getCXY());
		}
		if (screenXYs.size() == 0) {
			LOG.trace("NO CIRCLES IN PLOT");
		}
		if (screenXYs.size() == 0) {
			// this is really messy
			for (SVGLine line : componentCache.getOrCreateShapeCache().getLineList()) {
				Real2 vector = line.getEuclidLine().getVector();
				double angle = vector.getAngle();
				double length = vector.getLength();
				if (length < 3.0) {
					if (Real.isEqual(angle, 2.35, 0.03)) {
						screenXYs.add(line.getMidPoint());
					}
				}
			}
		}
		screenXYs.format(getNdecimal());
	}

	public void readAndCreateCSVPlot(AbstractCMElement svgElement) {
		componentCache = new ComponentCache(this);
		componentCache.setFileRoot(fileRoot);
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
		makeAxialTickBoxesAndPopulateContents();
		makeRangesForAxes();
		extractScaleTextsAndMakeScales();
		extractTitleTextsAndMakeTitles();
		extractDataScreenPoints();
		scaleXYDataPointsToValues();
		createCSVContent();
		writeProcessedSVG(svgOutFile);
		writeCSV(csvOutFile);
	}


	// graphics
	

	

	
	// getters and setters
	

	
	
	// static methods
	
	public void readAndStructureFigures(AbstractCMElement svgElement) {
		List<SVGElement> textElements = SVGUtil.getQuerySVGElements(svgElement, ".//*[local-name()='text']");
		List<SVGText> texts = SVGText.extractTexts(textElements);
	}

	protected void scaleXYDataPointsToValues() {
		scaledXYs = null;
		AnnotatedAxis xAxis = axisArray[AxisType.BOTTOM_AXIS];
		AnnotatedAxis yAxis = axisArray[AxisType.LEFT_AXIS];
		if (xAxis == null) {
			throw new RuntimeException("Missing xAxis");
		}
		if (yAxis == null) {
			throw new RuntimeException("Missing yAxis");
		}
		xAxis.ensureScales();
		yAxis.ensureScales();
		if (xAxis.getScreenToUserScale() == null ||
				xAxis.getScreenToUserConstant() == null ||
				yAxis.getScreenToUserScale() == null ||
				yAxis.getScreenToUserConstant() == null) {
			LOG.trace("XAXIS "+xAxis+"\n"+"YAXIS "+yAxis+"\n"+"Cannot get conversion constants: abort");
			return;
		}
	
		if (screenXYs != null && screenXYs.size() > 0) {
			scaledXYs = new Real2Array();
			for (int i = 0; i < screenXYs.size(); i++) {
				Real2 screenXY = screenXYs.get(i);
				double x = screenXY.getX();
				double scaledX = xAxis.getScreenToUserScale() * x + xAxis.getScreenToUserConstant();
				double y = screenXY.getY();
				double scaledY = yAxis.getScreenToUserScale() * y + yAxis.getScreenToUserConstant();
				Real2 scaledXY = new Real2(scaledX, scaledY);
				scaledXYs.add(scaledXY);
			}
			scaledXYs.format(ndecimal + 1);
		}
	}

	protected String createCSVContent() {
		// use CSVBuilder later
		StringBuilder sb = new StringBuilder();
		if (scaledXYs != null) {
			for (Real2 scaledXY : scaledXYs) {
				sb.append(scaledXY.getX()+","+scaledXY.getY()+"\n");
			}
		}
		csvContent = sb.toString();
		return csvContent;
	}



}
