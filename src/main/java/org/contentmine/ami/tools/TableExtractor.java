package org.contentmine.ami.tools;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIOCRTool.OcrType;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLineList;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class TableExtractor {
	private static final Logger LOG = Logger.getLogger(TableExtractor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<String> tableTypeList;
	private SVGElement svgElement;
	private File svgFile;
	private Multiset<Double> left;
	private Multiset<Double> right;
	private Multiset<Double> top;
	private Multiset<Double> bot;
	private OcrType ocrType;
	private double xlineWidthFactor = 0.1;
	private double ylineWidthFactor = 0.1;

	public void extractTable(File svgFile) {
		this.svgFile = svgFile;
		svgElement = SVGElement.readAndCreateSVG(svgFile);
		if (tableTypeList.contains("hocr")) {
			this.readHocrTable();
		}
		if (tableTypeList.contains("gocr")) {
			this.readGocrTable();
		}
	}

	void readHocrTable() {
		LOG.debug("hocrTable");
	/** 
	 * a tesseract chunk containing 2 words
	<g id="line_1_2" baseline="(0.0,-3.0)" style="font-size:22.0px;" class="line">
	the first lines are the fontsize annotation and we'll remove them soon
	<text x="14.0" y="29.0" style="font-size:7.0px;">24.3</text>
	<rect x="14.0" y="29.0" width="112.0" height="15.0" style="fill:darkgray;opacity:0.2;"/>
	<g id="word_1_3" class="word" x_wconf="72">
	<rect x="14.0" y="29.0" width="74.0" height="15.0" style="fill:green;opacity:0.2;"/>
	<text x="14.0" y="39.5" class="text" style="font-size:11.538461538461538px;">Karaloprak</text>
	</g>
	<g id="word_1_4" class="word" x_wconf="74">
	<rect x="93.0" y="29.0" width="33.0" height="12.0" style="fill:green;opacity:0.2;"/>
	<text x="93.0" y="41.0" class="text" style="font-size:15.600000000000001px;">2003</text>
	</g>
	*/
		List<SVGElement> wordgs = SVGUtil.getQuerySVGElements(svgElement, ".//*[local-name()='g' and @class='word' and *[local-name()='rect']]");
		initislizeMultisets();
		this.addLimitsOfHocrRects(wordgs);
		addLimitLines();
	}

	void readGocrTable() {
//		LOG.debug("gocrTable");
/** 
   <g class="line">
    <rect x="14.0" y="8.0" width="17.0" height="12.0" class="space" style="fill:none;stroke:blue;stroke-width:0.5;"/>
    <g>
     <rect x="14.0" y="8.0" width="17.0" height="12.0" class="text" style="fill:none;stroke:red;stroke-width:0.5;"/>
     <text x="14.0" y="20.0" achars="" style="fill:black;font-family:helvetica;font-size:10.0px;">?</text>
    </g>
    <g>
     <rect x="33.0" y="9.0" width="6.0" height="11.0" class="text" style="fill:none;stroke:red;stroke-width:0.5;"/>
     <text x="33.0" y="20.0" achars="" style="fill:black;font-family:helvetica;font-size:10.0px;">?</text>
    </g>
 */
		List<SVGElement> wordgs = SVGUtil.getQuerySVGElements(svgElement, 
				".//*[local-name()='g' and *[local-name()='rect'] and *[local-name()='text']]");
		initislizeMultisets();
		this.addLimitsOfGocrRects(wordgs);
		addLimitLines();
	}

	private void initislizeMultisets() {
		left = HashMultiset.create();
		right = HashMultiset.create();
		top = HashMultiset.create();
		bot = HashMultiset.create();
	}

	private void addLimitLines() {
		this.createAndAppendLines(Axis2.X, MultisetUtil.getEntriesSortedByCount(right), "red");
		this.createAndAppendLines(Axis2.X, MultisetUtil.getEntriesSortedByCount(left), "magenta");
		this.createAndAppendLines(Axis2.Y, MultisetUtil.getEntriesSortedByCount(top), "green");
		this.createAndAppendLines(Axis2.Y, MultisetUtil.getEntriesSortedByCount(bot), "blue");
	}

	void addLimitsOfHocrRects(List<SVGElement> wordgs) {
		for (SVGElement wordg : wordgs) {
			SVGRect rect = (SVGRect)SVGUtil.getQuerySVGElements(wordg, "./*[local-name()='rect']").get(0);
			extractRectLimits(rect);
		}
	}

	void addLimitsOfGocrRects(List<SVGElement> wordgs) {
		for (SVGElement wordg : wordgs) {
			SVGRect rect = (SVGRect)SVGUtil.getQuerySVGElements(wordg, "./*[local-name()='rect']").get(0);
			extractRectLimits(rect);
		}
	}

	private void extractRectLimits(SVGRect rect) {
		Real2Range bbox = rect.getBoundingBox();
		left.add(bbox.getXMin());
		right.add(bbox.getXMax());
		top.add(bbox.getYMin());
		bot.add(bbox.getYMax());
	}

	void createAndAppendLines(Axis2 axis, Iterable<Entry<Double>> entriesSortedByCount, String fill) {
		SVGLineList svgLineList = this.createLineList(axis, entriesSortedByCount, fill);
		for (SVGLine line : svgLineList) {
			svgElement.appendChild(line);
		}
		String absolutePath = svgFile.getAbsolutePath();
		File file = new File(absolutePath.replace(".svg", ".boxes.svg"));
		XMLUtil.writeQuietly(svgElement, file, 1);
	}

	SVGLineList createLineList(Axis2 axis, Iterable<Entry<Double>> entriesSortedByCount, String stroke) {
		SVGLineList lineList = new SVGLineList();
		Double y1 = new Double(0.0);
		for (Entry<Double> entry : entriesSortedByCount) {
			double value = entry.getElement();
			double v1 = 0.0;
					
			Real2 xy1 = Axis2.X.equals(axis) ? new Real2(value, v1) : new Real2(v1, value);
			Real2 xy2 = Axis2.X.equals(axis) ? new Real2(value, svgElement.getHeight()) : new Real2(svgElement.getWidth(), value);
			SVGLine line = new SVGLine(xy1, xy2);
			line.setStrokeWidth((Axis2.X.equals(axis) ? ylineWidthFactor : xlineWidthFactor) * (double)entry.getCount());
			line.setStroke(stroke);
			line.setOpacity(0.5);
			lineList.add(line);
		}
		return lineList;
	}

	public void setTableTypeList(List<String> tableTypeList) {
		this.tableTypeList = tableTypeList;
		if (tableTypeList == null) {
			throw new RuntimeException("must give tableTypeList");
		} else if (tableTypeList.contains( OcrType.hocr.toString())) {
			setHocrType();
		} else if (tableTypeList.contains( OcrType.gocr.toString())) {
			setGocrType();
		} else {
			throw new RuntimeException("tableType must contain ocrType");
		}
	}

	private void setGocrType() {
		this.ocrType = OcrType.gocr;
		this.xlineWidthFactor = 0.02;
		this.ylineWidthFactor = 0.2;
	}

	private void setHocrType() {
		this.ocrType = OcrType.hocr;
		this.xlineWidthFactor = 0.2;
		this.ylineWidthFactor = 0.2;
	}

}
