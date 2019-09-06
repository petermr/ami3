package org.contentmine.graphics.svg.plot;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.stml.STMLArray;
import org.contentmine.eucl.stml.STMLScalar;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPoly;
import org.contentmine.graphics.svg.SVGPolyline;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager.BoxEdge;
import org.contentmine.graphics.svg.linestuff.ComplexLine;
import org.contentmine.graphics.svg.linestuff.ComplexLine.CombType;
import org.contentmine.graphics.svg.linestuff.ComplexLine.LineOrientation;
import org.contentmine.graphics.svg.linestuff.Joint;
import org.contentmine.graphics.svg.text.TextAnalyzerUtils;
import org.contentmine.graphics.svg.words.TypedNumber;

import nu.xom.Attribute;

/** an axis on a graph.
 * 
 * Normally horizontal or vertical. 
 * 
 * Being refactored from old version.
 * 
 * @author pm286
 *
 */
public class Axis {

	private final static Logger LOG = Logger.getLogger(Axis.class);

	public enum Direction {
		BELOW,
		ABOVE,
		LEFT,
		RIGHT
	}
	
	public static final String AXIS_PREF = "axis_";
	public static final String AXIS = AXIS_PREF+ "axis";
	private static final String AXISCLASS = AXIS_PREF+ "axis";
	private static final String BACKBONE = AXIS_PREF+ "backbone";
	private static final String LABEL = AXIS_PREF+ "label";
	private static final String MAJOR_TICKS = AXIS_PREF+ "majorTicks";
	private static final String MINOR_TICKS = AXIS_PREF+ "minorTicks";
	private static final String VALUES = AXIS_PREF+ "values";
	private static final Double XTEXT_EXTENSION = 50.; // spread of vertical labels beyond backbone
	
	private static final Double X_SIDE = 10.; // spread of horizontal labels beyond backbone
	private static final Double X_VERT = 25.;
	private static final Double Y_SIDE = 10.; // spread of horizontal labels beyond backbone
	private static final Double Y_VERT = 45.;
	private static final double TEXT_EPS = 1.0;
	private static final int FORMAT_NDEC = 3;

	private double eps = 0.001;
	private ComplexLine complexLine;
	private Real2 axisWorldCoordStart = null;
	private Real2 axisWorldCoordEnd = null;
	private String axisLabel = null;
	private String axisUnits = null;
	private CombType combType;
	private List<SVGElement> texts;
	private Double boxThickness;
	private Double boxLengthExtension;
	private AxisAnalyzer axisAnalyzerX;
	private String id;

	private double minTickLengthPixels;
	private double maxTickLengthPixels;
	private List<Joint> majorTickJointList;
	private List<Joint> minorTickJointList;
	private Integer majorTickSpacingPixelsToMinorTick;
	private Double majorTickSpacingInPixels = null;
	private Double minorTickSpacingInPixels = null;
	
	private STMLArray majorTickMarkValuesOld;
	private STMLScalar scalar;
	
	private LineOrientation lineOrientation;
	
	private Real2 lowestMajorTickInPixels;
	private Real2 highestMajorTickInPixels;
	private Double lowestMajorTickCoordInPixels;
	private Double highestMajorTickCoordInPixels;
	private Double lowestTickMarkValue;
	private Double highestTickMarkValue;

	private List<SVGText> numericTextsOld;
	private List<SVGText> nonNumericTexts;

	private Double arraySpacingInValues;
	private RealRange axisRangeInPixels;
	private Double lowestAxisValue;
	private Double highestAxisValue;

	private Double pixelToValueScale;


	public Axis(AxisAnalyzer axisAnalyzerX) {
		this.axisAnalyzerX = axisAnalyzerX;
		this.boxLengthExtension = axisAnalyzerX.getBoxLengthExtension();
		this.boxThickness = axisAnalyzerX.getBoxThickness();
	}

	public Double getBoxThickness() {
		return boxThickness;
	}

	public void setBoxThickness(Double boxThickness) {
		this.boxThickness = boxThickness;
	}

	public CombType getCombType() {
		return combType;
	}

	public void setCombType(CombType combType) {
		this.combType = combType;
	}

	public Double getMajorTickPixelSpacing() {
		return majorTickSpacingInPixels;
	}

	public void setMajorTickPixelSpacing(Double majorTickPixelSpacing) {
		this.majorTickSpacingInPixels = majorTickPixelSpacing;
	}

	public Double getMinorTickPixelSpacing() {
		return minorTickSpacingInPixels;
	}

	public void setMinorTickPixelSpacing(Double minorTickPixelSpacing) {
		this.minorTickSpacingInPixels = minorTickPixelSpacing;
	}

	public Real2 getAxisWorldCoordStart() {
		if (axisWorldCoordStart == null) {
			axisWorldCoordStart = complexLine.getBackbone().getXY(0);
		}
		return axisWorldCoordStart;
	}

	public Real2 getAxisWorldCoordEnd() {
		if (axisWorldCoordEnd == null) {
			axisWorldCoordEnd = complexLine.getBackbone().getXY(1);
		}
		return axisWorldCoordEnd;
	}

	public String getAxisLabel() {
		return axisLabel;
	}

	public void setAxisLabel(String axisLabel) {
		this.axisLabel = axisLabel;
	}

	public String getAxisUnits() {
		return axisUnits;
	}

	public void setAxisUnits(String axisUnits) {
		this.axisUnits = axisUnits;
	}

	public ComplexLine getComplexLine() {
		return complexLine;
	}
	
	public LineOrientation getOrientation() {
		if (lineOrientation == null) {
			if (complexLine != null) {
				lineOrientation = complexLine.getBackboneOrientation();
			}
		}
		return lineOrientation;
	}

	public List<Joint> getMinorTickJointList() {
		return minorTickJointList;
	}

	public void setComplexLine(ComplexLine complexLine) {
		complexLine.getBackbone().normalizeDirection(eps);
		this.complexLine = complexLine;
	}
	
	public List<Joint> trimJointList(List<Joint> jointList, double minTickLength, double maxTickLength) {
		minorTickJointList = new ArrayList<Joint>();
		for (Joint joint : jointList) {
			double jointLength = joint.getLength();
			if (jointLength <= maxTickLength && jointLength >= minTickLength) {
				minorTickJointList.add(joint);
			}
		}
		return minorTickJointList;
	}


	public String debug(String msg) {
		String s = msg+"\n";
		s += " TrimmedJoints: "+minorTickJointList.size();
		s += " Spacing: "+minorTickSpacingInPixels;
		s += " Orient: "+complexLine.getBackboneOrientation()+"\n";
		s += " start: "+complexLine.getBackbone().getXY(0)+" end "+complexLine.getBackbone().getXY(1)+"\n";
		return s;
	}
	
	/** only works for correctly oriented text
	 * may have to rotate for other text
	 * 
	 * @param container
	 * @param boxThickness
	 * @param boxLengthExtension
	 */
	public List<SVGText> extractText(AbstractCMElement container) {
		if (LineOrientation.HORIZONTAL.equals(lineOrientation)) {
			return extractHorizontalAxisText(container, Direction.BELOW); // below to start with
		} else if (LineOrientation.VERTICAL.equals(lineOrientation)) {
			return extractVerticalAxisText(container, Direction.LEFT);
		} else {
			return null;
		}
	}

	private List<SVGText> extractHorizontalAxisText(AbstractCMElement container, Direction direction) {
		Real2Range bbox = complexLine.getBoundingBoxWithoutJoints();
		LOG.trace(bbox);
		Real2Range bboxExt = bbox.getReal2RangeExtendedInX((double)X_SIDE, (double)X_SIDE);
		double above = (Direction.BELOW.equals(direction)) ? 0.0 : X_VERT;
		double below = (Direction.ABOVE.equals(direction)) ? 0.0 : X_VERT;
		bboxExt = bboxExt.getReal2RangeExtendedInY(above, below);
		LOG.trace(bboxExt);
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(container);
		textList = SVGText.extractTexts(SVGUtil.findElementsWithin(bboxExt, textList));
		return textList;
	}

	private List<SVGText> extractVerticalAxisText(AbstractCMElement container, Direction direction) {
		Real2Range bbox = complexLine.getBoundingBoxWithoutJoints();
		LOG.trace(bbox);
		Real2Range bboxExt = bbox.getReal2RangeExtendedInY((double)Y_SIDE, (double)Y_SIDE);
		double right = (Direction.RIGHT.equals(direction)) ? 0.0 : Y_VERT;
		double left = (Direction.LEFT.equals(direction)) ? 0.0 : Y_VERT;
		bboxExt = bboxExt.getReal2RangeExtendedInX(right, left);
		LOG.trace(bboxExt);
		List<SVGText> textList = SVGText.extractSelfAndDescendantTexts(container);
		textList = SVGText.extractTexts(SVGUtil.findElementsWithin(bboxExt, textList));
		return textList;
	}

	/** only works for correctly oriented text
	 * may have to rotate for other text
	 * 
	 * @param container
	 * @param boxThickness
	 * @param boxLengthExtension
	 */
	@Deprecated // because it relies on TSpans
	public void processScaleValuesAndTitles(AbstractCMElement container) {
		texts = SVGUtil.getQuerySVGElements(container, ".//svg:text");
		countTSpanChildren("ALL ", texts);
		Real2Range textBox = getTextBox(complexLine.getBackbone());
		BoxEdge edge = (LineOrientation.HORIZONTAL.equals(getOrientation())) ? BoxEdge.XMIN : BoxEdge.YMIN;
		List<SVGElement> sortedTexts = BoundingBoxManager.getElementsSortedByEdge(texts, edge);
		countTSpanChildren("SORTED ", texts);
		List<SVGText> boundedTexts = getTextsInBox(textBox, sortedTexts); 
		countTSpanChildren("BOUND ", texts);
		ensureTickmarks();
		if (LineOrientation.HORIZONTAL.equals(lineOrientation)) {
			List<SVGText> horizontalTexts = getTexts(boundedTexts, LineOrientation.HORIZONTAL);
			countTSpanChildren("HOR ", horizontalTexts);
			for (AbstractCMElement horizontalText : horizontalTexts) {
				LOG.trace("HOR TEXT"+horizontalText);
			}
			analyzeHorizontalAxis(horizontalTexts);
		} else if (LineOrientation.VERTICAL.equals(lineOrientation)) {
			List<SVGText> verticalTexts = getTexts(boundedTexts, LineOrientation.HORIZONTAL);
			analyzeVerticalAxis(verticalTexts);
			for (SVGText rotatedText : verticalTexts) {
				LOG.trace("ROT "+rotatedText.getValue()+" .. "+
			       rotatedText.getTransform().getAngleOfRotation().getDegrees());
			}
		}
	}


	/** 
	 * Only works for correctly oriented text.
	 * May have to rotate for other text.
	 * <p>
	 * Assumes line of scale values and then a title.
	 * 
	 * @param text 
	 * @param boxThickness
	 * @param boxLengthExtension
	 */
	public void processScaleValuesAndTitlesNew(AbstractCMElement g) {
		throw new RuntimeException("MUST REWRITE");
//		List<SVGText> textList = extractText(g);
//		TextStructurer textStructurer = new TextStructurer(textList);
//		List<TextLine> textLineList = textStructurer.getTextLineList();
//		RawWords rawWords0 = (textLineList.size() > 0) ? textLineList.get(0).getRawWords() : null;
//		//IntArray intScales = (rawWords0 == null) ? null : rawWords0.translateToIntArray();
//		RealArray realScales = (rawWords0 == null) ? null : rawWords0.translateToRealArray();
//		RawWords title = (textLineList.size() > 1) ? textLineList.get(1).getRawWords() : null;
//		this.setAxisLabel(title.toString());
//		ensureTickmarks();
//		if (LineOrientation.HORIZONTAL.equals(lineOrientation)) {
//		     majorTickMarkValuesOld = new CMLArray(realScales);
//		} else if (LineOrientation.VERTICAL.equals(lineOrientation)) {
//			//List<SVGText> verticalTexts = getTexts(boundedTexts, LineOrientation.HORIZONTAL);
//			//analyzeVerticalAxis(verticalTexts);
//			//for (SVGText rotatedText : verticalTexts) {
//				//LOG.trace("ROT "+rotatedText.getValue()+" .. "+
//				//rotatedText.getTransform().getAngleOfRotation().getDegrees());
//			//}
//		}
	}

	private void countTSpanChildren(String msg, List<? extends SVGElement> texts) {
		int tspanCount = 0;
		for (AbstractCMElement text : texts) {
			tspanCount += ((SVGText)text).getChildTSpans().size();
		}
		LOG.trace(msg+" TSPANS****************"+tspanCount);
	}
	
	public void createAxisGroup() {
/*
	private double eps = 0.001;
	private Real2 axisWorldCoordStart = null;
	private Real2 axisWorldCoordEnd = null;
	private String axisLabel = null;
	private String axisUnits = null;
	private CombType combType;
	private List<SVGElement> texts;
	private Double boxThickness;
	private Double boxLengthExtension;
	private AxisAnalyzer axisAnalyzer;
	private TextAnalyzer textAnalyzer;
	private String id;

	private double minTickLengthPixels;
	private double maxTickLengthPixels;
	
	private ComplexLine complexLine;
	private List<Joint> majorTickJointList;
	private List<Joint> minorTickJointList;
	
	private Integer majorTickSpacingPixelsToMinorTick;
	private Double majorTickSpacingInPixels = null;
	private Double minorTickSpacingInPixels = null;
	
	private CMLArray majorTickMarkValues;
	private CMLScalar scalar;
	
	private LineOrientation lineOrientation;
	
	private Real2 lowestMajorTickInPixels;
	private Real2 highestMajorTickInPixels;
	private Double lowestMajorTickCoordInPixels;
	private Double highestMajorTickCoordInPixels;
	private Double lowestTickMarkValue;
	private Double highestTickMarkValue;

	private List<SVGText> numericTexts;
	private List<SVGText> nonNumericTexts;

	private Double arraySpacingInValues;
	private RealRange axisRangeInPixels;
	private Double lowestAxisValue;
	private Double highestAxisValue;

	private Double pixelToValueScale;

 */
		SVGLine backbone = complexLine.getBackbone();
		AbstractCMElement parent = (AbstractCMElement) backbone.getParent();
		if (parent == null) {
			throw new RuntimeException("backbone has no parent");
		}
		SVGG svgg = new SVGG();
		svgg.setSVGClassName(AXISCLASS);
		parent.appendChild(svgg);
		
//		groupBackbone(backbone, svgg);
		groupField(svgg, BACKBONE, backbone);
		// do minor first as major ticks are also included in minor
		groupTickJoints(svgg, MINOR_TICKS, minorTickJointList);
		groupTickJoints(svgg, MAJOR_TICKS, majorTickJointList);
		if (nonNumericTexts != null && nonNumericTexts.size() > 0) {
			groupField(svgg, LABEL, nonNumericTexts.get(0));
		}
		groupFields(svgg, VALUES, numericTextsOld);
		List<SVGElement> axisMarks = SVGUtil.getQuerySVGElements(svgg, "./svg:*[contains(@class, '"+AXIS_PREF+"')]");
		for (SVGElement axisMark : axisMarks) {
			axisMark.setStroke("yellow");
		}
		List<SVGElement> rects = SVGUtil.getQuerySVGElements(svgg, ".//svg:rect");
		for (AbstractCMElement rect : rects) {
			rect.detach();
		}
	}

	private void groupField(AbstractCMElement svgg, String fieldName, SVGElement field) {
		if (field != null) {
			field.setSVGClassName(fieldName);
			field.detach();
			svgg.appendChild(field);
		}
	}

	private void groupFields(AbstractCMElement svgg, String fieldName, List<? extends SVGElement> fields) {
		if (fields != null) {
			for (SVGElement field : fields) {
				field.setSVGClassName(fieldName);
				field.detach();
				svgg.appendChild(field);
			}
		}
	}

//	private void groupBackbone(SVGLine backbone, SVGG svgg) {
//		backbone.setClassName(BACKBONE);
//		backbone.detach();
//		svgg.appendChild(backbone);
//	}

	private void groupTickJoints(AbstractCMElement svgg, String tickType, List<Joint> tickList) {
		SVGG jointG = new SVGG();
		jointG.setSVGClassName(tickType);
		svgg.appendChild(jointG);
		for (Joint joint : tickList) {
			SVGElement line = joint.getLine();
			line.detach();
			jointG.appendChild(line);
		}
	}

	private void transformArrayFromPixelsToScale(List<SVGPolyline> polylines) {
		getOrientation();
		AbstractCMElement parentSVG = (AbstractCMElement)complexLine.getBackbone().getParent();
		if (parentSVG == null) {
			LOG.trace("NULL SVG PARENT");
		} else {
			ensureTickmarks();
			AbstractCMElement parent = (AbstractCMElement) parentSVG.getParent();
			for (SVGPoly polyline : polylines) {
				Real2Array polylineCoords = polyline.getReal2Array();
				RealArray polylineAxisPixelCoords = (LineOrientation.HORIZONTAL.equals(lineOrientation)) ?
						polylineCoords.getXArray() : polylineCoords.getYArray();
				RealArray polylineValueCoords = polylineAxisPixelCoords.createScaledArrayToRange(
					lowestMajorTickCoordInPixels, highestMajorTickCoordInPixels, lowestTickMarkValue, highestTickMarkValue);
				Double range = polylineValueCoords.getRange().getRange();
				int places = (int) Math.max(0, 6 - (Math.log10(range)-0.5));
				polylineValueCoords.format(places);
			}
		}
	}

	private void ensureTickmarks() {
		if (lowestMajorTickCoordInPixels == null) {
			getOrientation();
			getLowestMajorTickCoordinateInPixels();
			getHighestMajorTickCoordinateInPixels();
			getLowestMajorTickPointInPixels();
			getLowestTickMarkValue();
			getHighestMajorTickPointInPixels();
			getHighestTickMarkValue();
			getHighestAndLowestAxisValues();
		}
	}

	private Real2 getLowestMajorTickPointInPixels() {
		lowestMajorTickInPixels = majorTickJointList.get(0).getPoint();
		return lowestMajorTickInPixels;
	}

	private Real2 getHighestMajorTickPointInPixels() {
		highestMajorTickInPixels = majorTickJointList.get(majorTickJointList.size()-1).getPoint();
		return highestMajorTickInPixels;
	}
	
	private Double getLowestMajorTickCoordinateInPixels() {
		Real2 point = getLowestMajorTickPointInPixels();
		LOG.trace("LowestTick "+point+ "orientation "+lineOrientation);
		lowestMajorTickCoordInPixels = (LineOrientation.HORIZONTAL.equals(lineOrientation)) ? point.getX() : point.getY();
		return lowestMajorTickCoordInPixels;
	}
	
	private Double getHighestMajorTickCoordinateInPixels() {
		Real2 point = getHighestMajorTickPointInPixels();
		LOG.trace("HighestTick "+point+ "orientation "+lineOrientation);
		highestMajorTickCoordInPixels = (LineOrientation.HORIZONTAL.equals(lineOrientation)) ? point.getX() : point.getY();
		return highestMajorTickCoordInPixels;
	}
	
	private void getArraySpacingInValues() {
		if (arraySpacingInValues == null) {
			int size = majorTickMarkValuesOld.getSize();
			if (XMLConstants.XSD_INTEGER.equals(majorTickMarkValuesOld.getDataType())) {
				arraySpacingInValues = ((double) majorTickMarkValuesOld.getInts()[size-1] - (double) majorTickMarkValuesOld.getInts()[0])  / (double )(size - 1);
			} else if (XMLConstants.XSD_DOUBLE.equals(majorTickMarkValuesOld.getDataType())) {
				arraySpacingInValues = ((double) majorTickMarkValuesOld.getDoubles()[size-1] - (double) majorTickMarkValuesOld.getDoubles()[0])  / (double )(size - 1);
			} 
			LOG.trace("SCALE/TICK "+arraySpacingInValues);
		}
	}
	
	private void getHighestAndLowestAxisValues() {
		if (lowestTickMarkValue != null) {
			getArraySpacingInValues();
			getAxisRangeInPixels();
			ensureTickmarks();
			getPixelToValueScale();
			if (lowestTickMarkValue != null && highestTickMarkValue != null) {
				double axisMin = axisRangeInPixels.getMin();
				lowestAxisValue = (axisMin - lowestMajorTickCoordInPixels) / (pixelToValueScale) + lowestTickMarkValue;
				LOG.trace(" axisMin: "+axisMin+" lowestMajorTick "+lowestMajorTickCoordInPixels+" arraySpacingInPixels "+
				      arraySpacingInValues+" lowestTickMarkValue "+lowestTickMarkValue);
				LOG.trace("lowestAxisValue: "+lowestAxisValue);
				double axisMax = axisRangeInPixels.getMax();
				highestAxisValue = (axisMax - highestMajorTickCoordInPixels) / (pixelToValueScale) + highestTickMarkValue;
				LOG.trace(" axisMax: "+axisMax+" highestMajorTick "+highestMajorTickCoordInPixels+" arraySpacingInPixels "+
				      arraySpacingInValues+" highestTickMarkValue "+highestTickMarkValue);
				LOG.trace("highestAxisValue: "+highestAxisValue);
			}
		}
	}

	private double getPixelToValueScale() {
		if (pixelToValueScale == null) {
			ensureTickmarks();
			if (lowestTickMarkValue != null && lowestMajorTickCoordInPixels != null) {
				pixelToValueScale = (highestMajorTickCoordInPixels - lowestMajorTickCoordInPixels) / (highestTickMarkValue - lowestTickMarkValue);
			}
		}
		return pixelToValueScale;
	}

	private void analyzeHorizontalAxis(List<SVGText> ySortedTexts) {
		createNumericAndNonNumericTexts(ySortedTexts);
		processHorizontalScaleValuesAndScaleTitle(ySortedTexts);
		mapTickPositionsToValues();
	}
	
	private void analyzeVerticalAxis(List<SVGText> ySortedTexts) {
		createNumericAndNonNumericTexts(ySortedTexts);
		processVerticalScaleValuesAndScaleTitle(ySortedTexts);
		mapTickPositionsToValues();
	}

	private void mapTickPositionsToValues() {
		if (majorTickMarkValuesOld != null && majorTickJointList != null) {
			if (majorTickMarkValuesOld.getSize() == majorTickJointList.size()) {
				getArraySpacingInValues();
				// this should be elsewhere
				List<SVGPolyline> polylines = SVGPolyline.extractPolylines(SVGUtil.getQuerySVGElements(null, "./svg:g/svg:polyline"));
				transformArrayFromPixelsToScale(polylines);
			} else {
				LOG.trace("ARRAY: "+majorTickMarkValuesOld.getSize()+ " != "+majorTickJointList.size());
			}
		}
	}
	
	/** texts should have already have been grouped into words
	 * 
	 * @param texts
	 */
	private void processHorizontalScaleValuesAndScaleTitle(List<SVGText> texts) {
		createNumericAndNonNumericTexts(texts);
		Integer y = null;
		Double numericYCoord = TextAnalyzerUtils.getCommonYCoordinate(numericTextsOld, axisAnalyzerX.eps);
		if (numericYCoord != null) {
			majorTickMarkValuesOld = createNumericValuesOld(numericTextsOld);
		}
		Double nonNumericYCoord = TextAnalyzerUtils.getCommonYCoordinate(nonNumericTexts, axisAnalyzerX.eps);
		if (nonNumericYCoord != null && nonNumericTexts.size() > 0) {
			axisLabel = nonNumericTexts.get(0).getValue();
		}
	}

	/** texts should have already have been grouped into words
	 * assuming horizontal scale values at present
	 * @param texts
	 */
	private void processVerticalScaleValuesAndScaleTitle(List<SVGText> texts) {
		
		createNumericAndNonNumericTexts(texts);
		Integer y = null;
		Double numericRightXCoord = TextAnalyzerUtils.getCommonRightXCoordinate(numericTextsOld, TEXT_EPS);
		Double numericLeftXCoord = TextAnalyzerUtils.getCommonLeftXCoordinate(numericTextsOld, TEXT_EPS);
		if (numericRightXCoord != null || numericLeftXCoord != null) {
			majorTickMarkValuesOld = createNumericValuesOld(numericTextsOld);
		}
		Double nonNumericYCoord = TextAnalyzerUtils.getCommonYCoordinate(nonNumericTexts, axisAnalyzerX.eps);
		if (nonNumericYCoord != null && nonNumericTexts.size() == 1) {
			axisLabel = nonNumericTexts.get(0).getValue();
		}
	}

	private STMLArray createNumericValuesOld(List<SVGText> numericTexts) {
		STMLArray array = null;
		if (numericTexts.size() == 1 ) {
			SVGElement text = numericTexts.get(0);
			String dataType = text.getAttributeValue(TypedNumber.DATA_TYPE);
			String numbers = text.getAttributeValue(TypedNumber.NUMBERS);
			LOG.trace("NUMBERS: "+numbers);
			if (XMLConstants.XSD_INTEGER.equals(dataType)) {
				IntArray intArray = new IntArray(numbers);
				array = new STMLArray(intArray.getArray());
			} else if (XMLConstants.XSD_DOUBLE.equals(dataType)) {
				RealArray realArray = new RealArray(numbers);
				array = new STMLArray(realArray.getArray());
			}
		} else {
			String dataType = getCommonDataTypeOld(numericTexts);
			if (dataType != null) {
				List<String> values = new ArrayList<String>();
				for (SVGElement numericText : numericTexts) {
					values.add(TypedNumber.getNumericValue(numericText));
				}
				if (XMLConstants.XSD_INTEGER.equals(dataType)) {
					IntArray intArray = new IntArray(values.toArray(new String[0]));
					array = new STMLArray(intArray.getArray());
				} else if (XMLConstants.XSD_DOUBLE.equals(dataType)) {
					RealArray realArray = new RealArray(values.toArray(new String[0]));
					array = new STMLArray(realArray.getArray());
				}
			}
		}
		return array;
	}

	private String getCommonDataTypeOld(List<SVGText> numericTexts) {
		String dataType = null;
		for (SVGElement numericText : numericTexts) {
			String dt = numericText.getAttributeValue(TypedNumber.DATA_TYPE);
			if (dataType == null) {
				dataType = dt;
			} else if (!dataType.equals(dt)) {
				dataType = null;
				break;
			}
		}
		return dataType;
	}

	private void createNumericAndNonNumericTexts(List<SVGText> texts) {
		if (numericTextsOld == null) {
			numericTextsOld = new ArrayList<SVGText>();
			nonNumericTexts = new ArrayList<SVGText>();
			for (SVGText text : texts) {
				if (text.query("@"+TypedNumber.NUMBER).size() > 0 ||
					text.query("@"+TypedNumber.NUMBERS).size() > 0  ) {
					numericTextsOld.add(text);
				} else {
					if (text.getValue().trim().length() != 0) {
						nonNumericTexts.add(text);
					}
				}
			}
			LOG.trace("NUMERIC "+numericTextsOld.size()+" NON-NUM "+nonNumericTexts.size());
		}
	}

//	public ChunkAnalyzer getTextAnalyzerX() {
//		return textAnalyzerX;
//	}

	private List<SVGText> getTexts(List<SVGText> textList, LineOrientation orientation) {
		LOG.trace("ORIENT "+orientation+" texts "+textList.size());
		List<SVGText> subTextList = new ArrayList<SVGText>();
		for (SVGText text : textList) {
			Transform2 transform = text.getTransform();
			boolean isRotated = false;
			Double degrees = null;
			if (transform != null) {
				degrees = transform.getAngleOfRotation().getDegrees();
			} else {
				degrees = 0.0;
			}
			isRotated = Math.abs(degrees) > eps;
			LOG.trace("IS ROT "+isRotated);
			if (isRotated == LineOrientation.VERTICAL.equals(orientation)) {
				LOG.trace("ADDED TEXT ");
				subTextList.add(text);
			} else {
				LOG.trace("NOT added: "+text);
//				text.debug("NOT ADDED");
			}
		}
		return subTextList;
	}

	private List<SVGText> getTextsInBox(Real2Range textBox, List<SVGElement> sortedTexts) {
		// crude at present
		LOG.trace("TEXTBOX "+textBox);
		List<SVGText> textList = new ArrayList<SVGText>();
		for (int i = 0; i < sortedTexts.size(); i++) {
			SVGText sortedText = (SVGText) sortedTexts.get(i);
			Real2Range bb = sortedText.getBoundingBox();
			LOG.trace("   BOX? "+bb);
			if (textBox.includes(bb)) {
				textList.add(sortedText);
			} else {
				sortedText.getBoundingBox();
//				sortedText.debug(bb+ " NOT INCLUDED in "+textBox);
			}
		}
		return textList;
	}

	private Real2Range getTextBox(SVGLine backbone) {
		Real2Range textBox = null;
		if (LineOrientation.HORIZONTAL.equals(getOrientation())) {
			double x0 = backbone.getXY(0).getX();
			double x1 = backbone.getXY(1).getX();
			double y = backbone.getXY(0).getY();
			textBox = new Real2Range(new Real2(x0 - boxLengthExtension, y), 
					      new Real2(x1 + boxLengthExtension, y + boxThickness));
		} else if (LineOrientation.VERTICAL.equals(getOrientation())) { // only LHS at present
			double y0 = backbone.getXY(0).getY();
			double y1 = backbone.getXY(1).getY();
			double x = backbone.getXY(0).getX();
			textBox = new Real2Range(
					new Real2(x - boxThickness, y0 - boxLengthExtension), 
					new Real2(x, y1 + boxLengthExtension));
		}
		return textBox;
	}
	

	public String getId() {
		return this.id;
	}

	public void setId(String string) {
		this.id = string;
	}

	public List<Joint> getMajorTicks(double tickEpsRatio) {
		RealArray realArray = new RealArray();
		for (Joint joint : minorTickJointList) {
			realArray.addElement(joint.getLength());
		}
		minTickLengthPixels = realArray.getMin();
		maxTickLengthPixels = realArray.getMax();
		double meanTickLength = (minTickLengthPixels + maxTickLengthPixels) / 2.0; 
		// if not significant difference assume all ticks same size
		if (maxTickLengthPixels / minTickLengthPixels < tickEpsRatio) {
			return minorTickJointList;
		}
		majorTickJointList = new ArrayList<Joint>();
		for (Joint joint : minorTickJointList) {
			if (joint.getLength() > meanTickLength) {
				majorTickJointList.add(joint);
			}
		}
		return majorTickJointList;
	}

	public void analyzeMajorMinorTicks(ComplexLine complexLine) {
		addAxisAttribute(complexLine.getBackbone(), getId());
		for (Joint joint : getMinorTickJointList()) {
			addAxisAttribute(joint.getLine(), getId());
		}
		minorTickSpacingInPixels = ComplexLine.calculateInterJointSpacing(minorTickJointList, axisAnalyzerX.jointEps);
		majorTickJointList = getMajorTicks(AxisAnalyzer._MAJOR_MINOR_TICK_RATIO);
		majorTickSpacingInPixels = ComplexLine.calculateInterJointSpacing(majorTickJointList, axisAnalyzerX.jointEps);
		majorTickSpacingPixelsToMinorTick = null;
		if (majorTickSpacingInPixels != null && minorTickSpacingInPixels != null) {
			double ratio = majorTickSpacingInPixels/minorTickSpacingInPixels;
			majorTickSpacingPixelsToMinorTick = (int) Math.rint(ratio);
			if (Math.abs(ratio - majorTickSpacingPixelsToMinorTick) > 0.1) {
				throw new RuntimeException("Cannot get integer tick mark ratio: "+ratio + "/" +majorTickSpacingPixelsToMinorTick);
			}
			LOG.trace("MAJOR/MINOR "+(majorTickSpacingPixelsToMinorTick)+" majorTicks: "+majorTickJointList.size()+" ");
			LOG.trace(debug("NEW COMB"));
		}
	}

	public RealArray createScaledArrayToRange(RealArray polylinePixelCoords) {
		ensureTickmarks();
		RealArray realArray = null;
		if (lowestTickMarkValue != null && lowestMajorTickCoordInPixels != null) {
			realArray =  polylinePixelCoords.createScaledArrayToRange(
					lowestMajorTickCoordInPixels, highestMajorTickCoordInPixels, lowestTickMarkValue, highestTickMarkValue);
		}
		return realArray;
	}
	
	void addAxisAttribute(SVGElement element, String id) {
		element.addAttribute(new Attribute(AXIS, id));
	}

	public String toString() {
		String s = "\n";
		ensureTickmarks();
		if (majorTickMarkValuesOld != null && majorTickSpacingInPixels != null && majorTickJointList != null) {
			s += tickDetail("major", majorTickSpacingInPixels, majorTickJointList)+"\n";
			int nValues = majorTickMarkValuesOld.getSize();
			s += " "+nValues+" major values "+getLowestTickMarkValue()+" ... "+(nValues-1)+" gaps ... "+
			" "+getHighestTickMarkValue()+"\n";
		}
		if (minorTickSpacingInPixels != null && minorTickSpacingInPixels != null && minorTickJointList != null) {
			s += tickDetail("minor", minorTickSpacingInPixels, minorTickJointList)+"\n";
		}
		getHighestAndLowestAxisValues();
		if (lowestAxisValue != null) {
			s += "axis " + lowestAxisValue+" ... " + highestAxisValue + "\n";
		} else {
			s += "NO AXIS VALUES"+ "\n";
		}
		s += "label: "+axisLabel+"\n";
		
		return s;
	}

	private Double getLowestTickMarkValue() {
		if (lowestTickMarkValue == null && majorTickMarkValuesOld != null) {
			lowestTickMarkValue = majorTickMarkValuesOld.getElementAt(0).getNumberAsDouble();
		}
		return lowestTickMarkValue;
	}

	private Double getHighestTickMarkValue() {
		if (highestTickMarkValue == null && majorTickMarkValuesOld != null) {
			highestTickMarkValue = majorTickMarkValuesOld.getElementAt(majorTickMarkValuesOld.getSize()-1).getNumberAsDouble();
		}
		return highestTickMarkValue;
	}

	private String tickDetail(String title, double spacing, List<Joint> jointList) {
		int nTicks = jointList.size();
		return " "+nTicks+" "+title+" ticks (pixels): "+jointList.get(0).getPoint().format(decimalPlaces())+" ... "+(nTicks-1)+" gaps "+
				Real.normalize(spacing, 3)+"(pixels) ... "+jointList.get(nTicks-1).getPoint().format(decimalPlaces());
	}

	public RealRange getAxisRangeInPixels() {
		Real2Range r2r = complexLine.getBackbone().getReal2Range();
		axisRangeInPixels = (LineOrientation.HORIZONTAL.equals(lineOrientation)) ? r2r.getXRange() : r2r.getYRange();
		return axisRangeInPixels;
	}
	
	protected int decimalPlaces() {
		return FORMAT_NDEC;
	}
}
