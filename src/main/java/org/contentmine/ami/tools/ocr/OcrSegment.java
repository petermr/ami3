package org.contentmine.ami.tools.ocr;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIOCRTool;
import org.contentmine.ami.tools.AMIOCRTool.OcrType;
import org.contentmine.ami.tools.ocr.OcrMerger.MeanType;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGUtil;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** manages an OCR page.
 * 
 * @author pm286
 *
 */
public class OcrSegment {
	private static final Logger LOG = LogManager.getLogger(OcrSegment.class);
private static final String BOXES = "boxes";
	private File svgFile;
	private OcrType ocrType;
	private boolean hasBoxFilename;
	private SVGElement svgElement;
	private List<SVGG> textBoxList;
	private Object whiteListMap;

	public OcrSegment(File svgFile) {
		this.svgFile = svgFile;
		createAndProcessSVGElement(svgFile);
	}

	private void createAndProcessSVGElement(File svgFile) {
		String baseName = FilenameUtils.getBaseName(svgFile.toString());
		this.ocrType = OcrType.getType(baseName);
		this.hasBoxFilename = baseName.toLowerCase().indexOf(BOXES) != -1;
		getOrCreateSVGElement();
		getOrCreateBoxes();
	}

	/**
    <g>
     <rect x="459.0" y="151.0" width="8.0" height="11.0" class="text" style="fill:none;stroke:red;stroke-width:0.5;"/>
     <text x="459.0" y="162.0" achars="6" style="fill:black;font-family:helvetica;font-size:10.0px;">6</text>
    </g>
	 * @return
	 */
	public List<SVGG> getOrCreateBoxes() {
		getOrCreateSVGElement();
		if (textBoxList == null && svgElement != null && hasBoxFilename) {
			textBoxList = new ArrayList<SVGG>();
			List<SVGElement> gList = SVGUtil.getQuerySVGElements(svgElement, ".//*[local-name()='g' and *[local-name()='rect'] and *[local-name()='text']]");
		}
		return textBoxList;
	}
	
	public void readWhiteList(File file) {
		whiteListMap = new HashMap<>();
	}

	private SVGElement getOrCreateSVGElement() {
		if (svgElement == null && svgFile != null) {
			svgElement = SVGElement.readAndCreateSVG(svgFile);
		}
		return svgElement;
	}

	public void prepareMerge() {
		this.adjustCoordinates();
		this.applyWhitelist();
		}

	private void applyWhitelist() {
		LOG.debug("whitelist NYI");
		if (whiteListMap != null) {
			for (SVGG g : textBoxList) {
				
				
			}
		}
	}

	private void adjustCoordinates() {
		LOG.debug("adjustCoordinates() NYI");
		List<SVGLine> lineList = SVGLine.extractSelfAndDescendantLines(svgElement);
		
		int minValue = 3;
		IntArray xFrequencies = createFrequencies(lineList, Axis2.X, "red", minValue);
//		LOG.debug(xFrequencies);
		xFrequencies = createFrequencies(lineList, Axis2.X, "magenta", minValue);
//		LOG.debug(xFrequencies);
		
		IntArray yFrequencies = createFrequencies(lineList, Axis2.Y, "blue", minValue);
		LOG.debug(yFrequencies);
		xFrequencies = createFrequencies(lineList, Axis2.X, "green", minValue);
		LOG.debug(yFrequencies);
		
	}

	private IntArray createFrequencies(List<SVGLine> lineList, Axis2 axis, String color, int minValue) {
		List<SVGLine> lines = SVGLine.extractLines(SVGElement.getElementsWithAttribute(lineList, "stroke", color));
		IntArray frequencies = project(axis, lines, minValue);
		LOG.debug("ax "+axis+" "+color+" "+frequencies);
		return frequencies;
	}
	
	private IntArray project(Axis2 axis, List<SVGLine> lines, int minValue) {
		Multiset<Integer> multiset = HashMultiset.create();
		System.out.println("====OCR======");
		Axis2 axis2 = axis.otherAxis();
		for (SVGLine line : lines) {
			double coord = Axis2.X.equals(axis) ? line.getXY(0).getX() : line.getXY(0).getY();
//			LOG.debug(">> "+coord);
			Integer intCoord = (int) coord;
			multiset.add(intCoord);
		}
		LOG.debug("multi "+multiset);
		IntArray intArray =  MultisetUtil.extractSortedArrayOfValues(MultisetUtil.createListSortedByValue(multiset), minValue);
		LOG.debug("intArray "+intArray);
		return intArray;
	}

	private void testRealArray() {
		String value = svgElement.getValue().replaceAll("\\s+", " ").trim();
		value = value.replaceAll(",",  ".");
		if (!value.contains(".")) {
			value = "." + value;
		}
		System.out.println("svg ____________________________________ "+value);
		String[] values = value.split("\\s+");
		List<String> valueList = Arrays.asList(values);
		LOG.debug(valueList);
		LOG.debug("VAL size "+values.length);
		RealArray realArray = null;
		try {
			realArray = new RealArray(values);
		} catch (Exception e) {
			System.out.println("bad array : "+value+" / "+e.getMessage());
		}
		
		if (realArray != null) {
			extractRealArray(realArray); 
		}
		
	}

	private void extractRealArray(RealArray realArray) {
		LOG.debug("*****REALARRAY: "+realArray+" ********");
		Double mean = null;
		MeanType type = null;
		
		if (realArray.size() == 1) {
			LOG.debug("***** SINGLE VALUE ****");
		} else if (realArray.size() == 3) {
			if (Real.isEqual(realArray.get(1),0.0,0.01)) {
				mean = 0.0;
				type = MeanType.arithmetic;
			} else if (Real.isEqual(realArray.get(1),0.0,1.01)) {
				mean = 1.0;
				type = MeanType.geometric;
			}
			realArray.deleteElement(1);
		} else if (realArray.size() == 2) {
			mean = Double.NaN;
		}
		if (realArray.size() == 2) {
			double prod = realArray.get(0) * realArray.get(1);
			double sum = realArray.get(0) + realArray.get(1);
			if (Real.isEqual(prod, 1., 0.01)) {
				type = MeanType.geometric;
			} else if (Real.isEqual(sum, 0., 0.01)) {
				type = MeanType.arithmetic;
			} else {
				type = null;
				LOG.debug("cannot find mean: "+realArray);
			}
		}
		if (mean != null) {
			LOG.debug("******** MEAN ****** "+type+": "+realArray);
		}
	}
	


}
