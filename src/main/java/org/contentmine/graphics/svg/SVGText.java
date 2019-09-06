package org.contentmine.graphics.svg;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Angle.Units;
import org.contentmine.eucl.euclid.EuclidConstants;
import org.contentmine.eucl.euclid.EuclidRuntimeException;
import org.contentmine.eucl.euclid.IntSet;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlSub;
import org.contentmine.graphics.html.HtmlSup;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.contentmine.graphics.svg.fonts.FontWidths;
import org.contentmine.image.ImageUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.Text;

/** 
 * Draws text.
 * 
 * NOTE: text can be rotated and the additional fields manage some of the
 * metrics for this. Still very experimental.
 * 
 * @author pm286
 */
public class SVGText extends SVGElement {

	private static final String X = "x";

	private static Logger LOG = Logger.getLogger(SVGText.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public enum RotateText {
		TRUE,
		FALSE,
		;
	}


	// just in case there is a scaling problem
	private static final double _SVG2AWT_FONT_SCALE = 1.0;
	
	public final static String TAG ="text";
	
    public static String SUB0 = XMLConstants.S_UNDER+XMLConstants.S_LCURLY;
    public static String SUP0 = XMLConstants.S_CARET+XMLConstants.S_LCURLY;
    public static String SUB1 = XMLConstants.S_RCURLY+XMLConstants.S_UNDER;
    public static String SUP1 = XMLConstants.S_RCURLY+XMLConstants.S_CARET;
    
    public final static Double DEFAULT_FONT_WIDTH = 500.0;
    public final static Double DEFAULT_FONT_WIDTH_FACTOR = 10.0;
    public final static Double MIN_WIDTH = 0.001; // useful for non printing characters
	private final static Double SCALE1000 = 0.001; // width multiplied by 1000

	public final static String ALL_TEXT_XPATH = ".//svg:text";

	private static final String BOLD = "bold";
	private static final String ITALIC = "italic";

	// old font name was "fontName", 
	public static final String FONT_NAME = "font-name";
	public static final String FONT_NAME_OLD = "fontName";
	public static final String WIDTH = "width";

	/** 
	 * Rough average of width of "n" 
	 */
	private static final Double N_SPACE = 0.55;
	public static final Double SPACE_FACTOR = 0.2; //extra space that denotes a space
	private Double SPACE_WIDTH1000 = /*274.0*/ 200.;
	public final static Double DEFAULT_SPACE_FACTOR = 0.05;
	private static final double MIN_FONT_SIZE = 0.01;
	private static final Double DEFAULT_CHARACTER_WIDTH = 500.0;

	/** categorizes text strings as common types
	 * 
	 * @author pm286
	 *
	 */
	public enum TextType {
		ALPHA("A", "[A-Za-z_]+"),
		BRACK("B", "[\\(\\{\\[\\<]"),
		EMPTY("0", ""),
		END_BRACK("C", "[\\)\\}\\]\\>]"),
		EQUALS("E", "\\="),
		FLOAT("F", "\\-?\\d+\\.\\d+"),
		// assumes leading and trailing digits (e.g. not .3 or 2.)
		INTEGER("I", "\\-?\\d+"),
		PERCENT("%", "\\-?\\d+(\\.\\d+)?\\%"),
		PUNCT("P", "[\\!\\@\\#\\$\\^\\&\\*\\:\\;\\,]"),
		QUOTE("Q", "[\\\"\\']"),
		SPACE(" ", "\\s+"),
		// anything else
		STRING("S", "[^\\s]+"),
		UNKNOWN("?", ".*"),
		;
		private String abbrev;
		private Pattern pattern;

		private TextType(String abbrev, String regex) {
			this.abbrev = abbrev;
			this.pattern = Pattern.compile(regex);
		}
		public Pattern getPattern() {
			return pattern;
		}
		public String getAbbrev() {
			return abbrev;
		}
		
		public static TextType getType(String text) {
			if (text == null) return null;
			if (text.length() == 0) {
				return EMPTY;
			}
			if (text.trim().length() == 0) {
				return SPACE;
			}
			if (text.length() == 1) {
				for (TextType type : new TextType[] {BRACK, END_BRACK, EQUALS, PERCENT, PUNCT, QUOTE}) {
					if (type.pattern.matcher(text).matches()) {
						return type;
					}
				}
			}
			if (INTEGER.pattern.matcher(text).matches()) {
				return INTEGER;
			}
			if (FLOAT.pattern.matcher(text).matches()) {
				return FLOAT;
			}
			if (PERCENT.pattern.matcher(text).matches()) {
				return PERCENT;
			}
			if (ALPHA.pattern.matcher(text).matches()) {
				return ALPHA;
			}
			if (STRING.pattern.matcher(text).matches()) {
				return STRING;
			}
			LOG.error("Cannot match: "+text);
			return UNKNOWN;
			
		}
	};

	public Angle ROT90 = new Angle(Math.PI/2.0, Units.RADIANS);
	public double ANGLE_EPS = 0.01;

	// these are all when text is used for concatenation, etc.
	private double estimatedHorizontallength = Double.NaN; 
	private double currentFontSize = Double.NaN;
	private double currentBaseY = Double.NaN;
	private String rotate = null;
	private double calculatedTextEndCoordinate = Double.NaN;
	private List<SVGTSpan> tspans;

	private FontWeight fontWeight;
	private Double widthOfFirstCharacter;
	private Double heightOfFirstCharacter;

	private RealArray xArray;
	private RealArray yArray;
	private RealArray fontWidthArray;
	
	/** 
	 * Constructor
	 */
	public SVGText() {
		super(TAG);
		init();
	}
	
	/** 
	 * Constructor
	 * 
	 * @param xy
	 * @param text
	 */
	public SVGText(Real2 xy, String text) {
		this();
		setXYAndText(xy, text);
	}

	private void setXYAndText(Real2 xy, String text) {
		if (new Real2(0.0, 0.0).isEqualTo(xy, 0.000001)) {
			LOG.warn("Text of (0,0) is suspicious");
		}
		setXY(xy);
		setText(text);
	}

	/** 
	 * Constructor.
	 * 
	 * @param xy
	 * @param text
	 */
	protected SVGText(Real2 xy, String text, String tag) {
		this(tag);
		setXYAndText(xy, text);
	}

	protected void init() {
		super.setDefaultStyle();
//		setDefaultStyle(this);
	}
	
	private void clearRotate() {
		estimatedHorizontallength = Double.NaN; 
		currentBaseY = Double.NaN;
		calculatedTextEndCoordinate = Double.NaN;
		setBoundingBoxCached(false);
	}

	public static void setDefaultStyle(SVGElement text) {
		text.setStroke("none");
		text.setStrokeWidth(0.0);
	}
	
	/** constructor
	 */
	public SVGText(SVGElement element) {
        super(element);
	}
	
	/** constructor
	 */
	protected SVGText(SVGElement element, String tag) {
        super(element, tag);
	}
	
	/** constructor
	 */
	public SVGText(Element element) {
        super((SVGElement) element);
	}
	
	/** constructor
	 */
	protected SVGText(Element element, String tag) {
        super((SVGElement) element, tag);
	}
	
	protected SVGText(String tag) {
		super(tag);
	}
    /**
     * copy node .
     *
     * @return Node
     */
    public Node copy() {
        return new SVGText(this, TAG);
    }
    
    /** now edited to manage compact form 
     * x = "1.2, 3.4" etc.
     * @return first Xcoordinate
     */
	public Real2 getXY() {
		Double x = getFirstX();
		Double y = getFirstY();
		return (x == null || y == null) ? null : new Real2(x, y);
	}

	private Double getFirstY() {
		yArray = parseSingleOrArrayAttribute(SVGElement.Y);
		Double y = (yArray == null || yArray.size() == 0) ? null : yArray.elementAt(0);
		return y;
	}

	private Double getFirstX() {
		xArray = parseSingleOrArrayAttribute(SVGElement.X);
		Double x = (xArray == null || xArray.size() == 0) ? null : xArray.elementAt(0);
		return x;
	}

	@Override
	/** required to parse single and compact form.
	 * 
	 */
	public Double getX() {
		Double x = getFirstX();
		return x;
	}
	
	/** required to parse single and compact form.
	 * 
	 */
	public RealArray getXArray() {
		xArray = parseSingleOrArrayAttribute(SVGElement.X);
		return xArray;
	}
	
	/** required to parse single and compact form.
	 * 
	 */
	public RealArray getYArray() {
		yArray = parseSingleOrArrayAttribute(SVGElement.Y);
		return yArray;
	}
	
	@Override
	/** required to parse single and compact form.
	 * 
	 */
	public Double getY() {
		Double y = getFirstY();
		return y;
	}
	
	private RealArray parseSingleOrArrayAttribute(String attName) {
		String attValue = this.getAttributeValue(attName);
		return parseRealArray(attValue);
	}

	private RealArray parseSingleOrArraySVGXAttribute(String attName) {
		String attValue = SVGUtil.getSVGXAttribute(this, attName);
		return parseRealArray(attValue);
	}

	private RealArray parseRealArray(String value) {
		RealArray coordArray = null;
		if (value == null) {
			return coordArray;
		}
		if (value.equals("")) {
			return new RealArray();
		}
		try {
			coordArray = new RealArray(value.split("(\\,|\\s+)"));
		} catch (EuclidRuntimeException ere) {
			// bad coordArray;
		}
		return coordArray;
	}
	
	protected void drawElement(Graphics2D g2d) {
		saveGraphicsSettingsAndApplyTransform(g2d);
		String text = this.getText();
		if (text != null) {
			String fill = this.getFill();
			Color fillColor = getJava2DColor(fill); 
			float fontSize = (float) (double) this.getFontSize();
			fontSize *= cumulativeTransform.getMatrixAsArray()[0] * _SVG2AWT_FONT_SCALE;
			Font font = g2d.getFont();
			font = font.deriveFont(fontSize);
			if (isItalic()) {
				font = font.deriveFont(Font.ITALIC);
			}
			setTextAntialiasing(g2d, true);
			Real2 xy = this.getXY();
			xy = transform(xy, cumulativeTransform);
			LOG.trace("CUM "+cumulativeTransform+"XY "+xy);
			saveColor(g2d);
			if (fillColor != null) {
				g2d.setColor(fillColor);
			}
			g2d.setFont(font);
			g2d.drawString(text, (int)xy.x, (int)xy.y);
			restoreColor(g2d);
		}
		restoreGraphicsSettingsAndTransform(g2d);
	}

	public void applyTransformPreserveUprightText(Transform2 t2) {
		// transform the position and scale
		applyTransform(t2, RotateText.FALSE);
	}

	/** if TRUE the characters are rotated back to be Upright
	 * 
	 * @param t2
	 * @param rotateText
	 */
	public void applyTransform(Transform2 t2, RotateText rotateText) {
		Real2 xy = getXY();
		if (xy == null || t2 == null) return;
		xy.transformBy(t2);
		this.setXY(xy);
		transformFontSize(t2);
		Angle angle = t2.getAngleOfRotation();
		//rotate characters to preserve relative orientation
		if (angle != null && !angle.isEqualTo(0.0, SVGLine.EPS)) {
			if (RotateText.FALSE.equals(rotateText)) {
				angle = angle.multiplyBy(0.0);
			} else if (RotateText.TRUE.equals(rotateText)) {
				angle = angle.multiplyBy(-1.0);
			}
			Transform2 t = Transform2.getRotationAboutPoint(angle, xy);
			
			t = t.concatenate(t2);
			this.setTransform(t);
		}
	}

	/** result is always positive
	 * 
	 * @param t2
	 */
	public void transformFontSize(Transform2 t2) {
		Double fontSize = this.getFontSize();
		// transform fontSize
		if (fontSize != null) {
			Real2 ff = new Real2(fontSize, 1.0);
			Transform2 rotMat = new Transform2(t2);
			rotMat.setTranslation(new Real2(0.0,0.0));
			ff.transformBy(rotMat);
			double size = Math.max(ff.getX(), ff.getY()); // takes account of rotation
			LOG.trace("FS "+ff+" .. "+size);
			this.setFontSize(size);
		}
	}

    /** round to decimal places.
     * 
     * @param places
     * @return this
     */
    public void format(int places) {
    	super.format(places);
    	Real2 xy = getXY();
    	if (xy != null) {
			setXY(xy.format(places));
	    	Double fontSize = this.getFontSize();
	    	if (fontSize != null) {
	    		fontSize = Util.format(fontSize, places);
	    		this.setFontSize(fontSize);
	    	}
    	}
    }

    /** round to decimal places.
     * 
     * @param places
     * @return this
     */
    public void formatTransform(int places) {
    	super.formatTransform(places);
    	Real2 xy = getXY();
    	if (xy != null) {
			setXY(xy.format(places));
	    	Double fontSize = this.getFontSize();
	    	if (fontSize != null) {
	    		fontSize = Util.format(fontSize, places);
	    		this.setFontSize(fontSize);
	    	}
    	}
    }


	/**
	 * @return tag
	 */
	public String getTag() {
		return TAG;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		Nodes nodes = query("./text()");
		return (nodes.size() == 1 ? nodes.get(0).getValue() : null);
	}
	
	/**
	 * Clears text and replaces if not null
	 * 
	 * @param text the text to set
	 * @throws nu.xom.IllegalCharacterDataException for non-XML character
	 */
	public void setText(String text) {
		if (this.getChildCount() > 0) {
			Node node = this.getChild(0);
			if (node instanceof Text) {
				node.detach();
			} else if (node instanceof SVGTSpan) {
				// expected child
			} else if (node instanceof SVGTitle) {
				// expected child
			} else {
				LOG.warn("unexpected child of SVGText: "+node.getClass());
			}
		}
		if (text != null) {
			try {
				this.appendChild(text);
			} catch (nu.xom.IllegalCharacterDataException e) {
				throw new nu.xom.IllegalCharacterDataException("Cannot append text: "+text+" (char-"+(int)text.charAt(0)+")", e);
			}
		}
		boundingBox = null;
		calculatedTextEndCoordinate = Double.NaN;
		estimatedHorizontallength = Double.NaN; 
	}

	/** extent of text
	 * defined as the point in the middle of the visual string (
	 * e.g. near the middle of the crossbar in "H")
	 * @return
	 */
	public Real2Range getBoundingBoxForCenterOrigin() {
		
		//double fontWidthFactor = DEFAULT_FONT_WIDTH_FACTOR;
		//double fontWidthFactor = 1.0;
		// seems to work??
		double fontWidthFactor = 0.3;
		double halfWidth = getEstimatedHorizontalLength(fontWidthFactor) / 2.0;
		
		double height = this.getFontSize();
		Real2Range boundingBox = new Real2Range();
		Real2 center = getXY();
		boundingBox.add(center.plus(new Real2(halfWidth, 0.0)));
		boundingBox.add(center.plus(new Real2(-halfWidth, height)));
		return boundingBox;
	}

	/** 
	 * Extent of text.
	 * Defined as the point origin (i.e. does not include font).
	 * 
	 * @return
	 */
	public Real2Range getBoundingBox() {
		if (boundingBoxNeedsUpdating()) {
			getChildTSpans();
			Double width = null;
			Double height = null;
			if (tspans.size() > 0) {
				boundingBox = tspans.get(0).getBoundingBox();
				for (int i = 1; i < tspans.size(); i++) {
					Real2Range r2ra = tspans.get(i).getBoundingBox();
					boundingBox = boundingBox.plus(r2ra);
				}
			} else {
				double fontWidthFactor = 1.0;
				Real2 xy = getXY();
				if (xy == null) {
					return null;
				}
				width = getEstimatedHorizontalLength(fontWidthFactor);
				if (width == null || Double.isNaN(width)) {
					width = MIN_WIDTH;
					String text = getText();
					if (text == null || "null".equals(text)) {
						setText("");
					} else if (text.length() == 0) {
						throw new RuntimeException("found empty text ");
					} else if (text.contains("\n")) {
						throw new RuntimeException("found LF "+String.valueOf(((int) text.charAt(0))));
					} else if (text.equals(" ")) {
						// space without width
					} else {
						LOG.trace("Missing svgx:width? found strange Null text "+text+"/"+String.valueOf(((int) text.charAt(0))));
					}
				}
				height = getFontSize() * fontWidthFactor;
				boundingBox = (xy == null) ? null : new Real2Range(xy, xy.plus(new Real2(width, -height)));
			}	
			if (!boundingBox.isValid()) {
				throw new RuntimeException("Invalid bbox: "+width+"/"+height);
			}
			rotateBoundingBoxForRotatedText();
				
		}
		return boundingBox;
	}
	
	private void rotateBoundingBoxForRotatedText() {
		Transform2 t2 = getTransform();
		if (t2 != null) {
			Angle rotation = t2.getAngleOfRotation();
			if (rotation == null) {
				LOG.trace("Null angle: "+t2);
			}
			// significant rotation?
			if (rotation != null && !rotation.isEqualTo(0., 0.001)) {
				Real2[] corners = boundingBox.getLLURCorners();
				corners[0].transformBy(t2);
				corners[1].transformBy(t2);
				boundingBox = new Real2Range(corners[0], corners[1]);
			}
		}
	}

	/** 
	 * This is a hack and depends on what information is available.
	 * 
	 * Include fontSize and factor.
	 * 
	 * @param fontWidthFactor
	 * @return
	 */
	public Double getEstimatedHorizontalLength(double fontWidthFactor) {
		estimatedHorizontallength = Double.NaN;
		if (xArray != null && yArray != null) {
			String text = getText();
			int nchar = xArray.size();
			// get length from x-coordinates
			estimatedHorizontallength = xArray.get(nchar - 1) - xArray.get(0);
			getSVGXFontWidthArray();
			Double lastFontWidth = null;
			if (fontWidthArray == null) {
				lastFontWidth = xArray.size() > 1 ? xArray.getMean() : DEFAULT_FONT_WIDTH;
			} else if (fontWidthArray.size() == xArray.size()) {
				lastFontWidth = fontWidthArray.getLast();
			} else {
				lastFontWidth = fontWidthArray.getMean();
			}
			estimatedHorizontallength += lastFontWidth * 0.001 * this.getFontSize();
		} else if (getChildTSpans().size() == 0) {
			String s = getText();
			if (s != null) {
				String family = getFontFamily();
				double[] lengths = FontWidths.getFontWidths(family);
				if (lengths == null) {
					lengths = FontWidths.SANS_SERIF;
				}
				Double fontSize = getFontSize();
				estimatedHorizontallength = 0.0;
				if (fontSize == null) {
					throw new RuntimeException("missing font-size: "+this.toXML());
				}
				for (int i = 0; i < s.length(); i++) {
					char c = s.charAt(i);
					if (c > 255) {
						c = 's';  // as good as any
					}
					double length = fontSize * fontWidthFactor * lengths[(int) c];
					estimatedHorizontallength += length;
				}
			}
		}
		return estimatedHorizontallength;
	}
	
	public Real2 getCalculatedTextEnd(double fontWidthFactor) {
		getRotate();
		Real2 xyEnd = null;
		getEstimatedHorizontalLength(fontWidthFactor);
		if (!Double.isNaN(estimatedHorizontallength)) {
			if (rotate == null) {
				xyEnd = this.getXY().plus(new Real2(estimatedHorizontallength, 0.0));
			} else if (rotate.equals(SVGElement.YPLUS)) {
				xyEnd = this.getXY().plus(new Real2(0.0, -estimatedHorizontallength));
			} else if (rotate.equals(SVGElement.YMINUS)) {
				xyEnd = this.getXY().plus(new Real2(0.0, estimatedHorizontallength));
			}
		}
		return xyEnd;
	}
	
	public double getCalculatedTextEndCoordinate(double fontWidthFactor) {
		if (Double.isNaN(calculatedTextEndCoordinate)) {
			getRotate();
			Real2 xyEnd = getCalculatedTextEnd(fontWidthFactor);
			if (xyEnd != null) {
				if (rotate == null) {
					calculatedTextEndCoordinate = xyEnd.getX();
				} else if (rotate.equals(YMINUS)){
					calculatedTextEndCoordinate = xyEnd.getY();
				} else if (rotate.equals(YPLUS)){
					calculatedTextEndCoordinate = xyEnd.getY();
				} else {
					calculatedTextEndCoordinate = xyEnd.getY();
				}
			}
		}
		return calculatedTextEndCoordinate;
	}
	
	public void setCalculatedTextEndCoordinate(double coord) {
		this.calculatedTextEndCoordinate = coord;
	}
	
	public double getCurrentFontSize() {
		if (Double.isNaN(currentFontSize)) {
			currentFontSize = this.getFontSize();
		}
		return currentFontSize;
	}
	public void setCurrentFontSize(double currentFontSize) {
		this.currentFontSize = currentFontSize;
	}
	
	public double getCurrentBaseY() {
		getRotate();
		if (Double.isNaN(currentBaseY)) {
			currentBaseY = (rotate == null) ? this.getY() : this.getX();
		}
		return currentBaseY;
	}
	public void setCurrentBaseY(double currentBaseY) {
		this.currentBaseY = currentBaseY;
	}
	
	public String getRotate() {
		if (rotate == null) {
			rotate = getAttributeValue(SVGElement.ROTATE);
		}
		return rotate;
	}
	
	public void setRotate(String rotate) {
		this.rotate = rotate;
		clearRotate();
	}
	
	/**
	 * tries to concatenate text1 onto this. If success (true) alters this,
	 * else leaves this unaltered
	 * 
	 * @param fontWidthFactor
	 * @param fontHeightFactor
	 * @param text1 left text
	 * @param subVert fraction of large font size to determine subscript
	 * @param supVert fraction of large font size to determine superscript
	 * @return null if concatenated
	 */
	public boolean concatenateText(double fontWidthFactor, double fontHeightFactor, 
			SVGText text1, double subVert, double supVert, double eps) {

		String rotate0 = this.getAttributeValue(SVGElement.ROTATE);
		String rotate1 = text1.getAttributeValue(SVGElement.ROTATE);
		// only compare text in same orientation
		boolean rotated = false;
		if (rotate0 == null) {
			rotated = (rotate1 != null);
		} else {
			rotated = (rotate1 == null || !rotate0.equals(rotate1));
		}
		if (rotated) {
			LOG.info("text orientation changed");
			return false;
		}
		String newText = null;
		String string0 = this.getText();
		double fontSize0 = this.getCurrentFontSize();
		Real2 xy0 = this.getXY();
		String string1 = text1.getText();
		double fontSize1 = text1.getFontSize();
		Real2 xy1 = text1.getXY();
		double fontRatio0to1 = fontSize0 / fontSize1;
		double fontWidth = fontSize0 * fontWidthFactor;
		double fontHeight = fontSize0 * fontHeightFactor;
		// TODO update for different orientation
		double coordHoriz0 = (rotate0 == null) ? xy0.getX() : xy0.getY();
		double coordHoriz1 = (rotate1 == null) ? xy1.getX() : xy1.getY();
		double coordVert0 = this.getCurrentBaseY();
		double coordVert1 = (rotate1 == null) ? xy1.getY() : xy1.getX();
		double deltaVert = coordVert0 - coordVert1;
		double maxFontSize = Math.max(fontSize0, fontSize1);
		double unscriptFontSize = Double.NaN;
		String linker = null;
		// anticlockwise Y rotation changes order
		double sign = (YPLUS.equals(rotate)) ? -1.0 : 1.0;
		double[] fontWidths = FontWidths.getFontWidths(this.getFontFamily());
		double spaceWidth = fontWidths[(int)C_SPACE] * maxFontSize * fontWidthFactor;
		
		// same size of font?
		// has vertical changed by more than the larger font size?
		if (!Real.isEqual(coordVert0, coordVert1, maxFontSize * fontHeightFactor)) {
			LOG.info("changed vertical height "+coordVert0+" => "+coordVert1+" ... "+maxFontSize);
		} else if (fontRatio0to1 > 0.95 && fontRatio0to1 < 1.05) {
			// no change of size
			if (Real.isEqual(coordVert0, coordVert1, eps)) {
				// still on same line?
				// allow a space
				double gapXX = (coordHoriz1 - coordHoriz0) * sign;
				double calcEnd = this.getCalculatedTextEndCoordinate(fontWidthFactor);
				double gapX = (coordHoriz1 - calcEnd) *sign;
				double nspaces = (gapX / spaceWidth);
				if (gapXX < 0) {
					// in front of preceding (axes sometime go backwards
					linker = null;
				} else if (nspaces < 0.5) {
					nspaces = 0;
				} else if (nspaces > 2) {
					nspaces = 100;
				} else {
					nspaces = 1;
				}
				linker = null;
				if (nspaces == 0) {
					linker = XMLConstants.S_EMPTY;
				} else if (nspaces == 1) {
					linker = XMLConstants.S_SPACE;
				}
			} else {
				LOG.trace("slight vertical change: "+coordVert0+" => "+coordVert1);
			}
		} else if (fontRatio0to1 > 1.05) {
			// coords down the page?
			// sub/superScript
			if (deltaVert > 0 && Real.isEqual(deltaVert, subVert * fontHeight, maxFontSize)) {
				// start of subscript?
				linker = SUB0;
				// save font as larger size
				this.setFontSize(text1.getFontSize());
			} else if (deltaVert < 0 && Real.isEqual(deltaVert, supVert * fontHeight, maxFontSize)) {
				// start of superscript?
				linker = SUP0;
				// save font as larger size
				this.setFontSize(text1.getFontSize());
			} else {
				LOG.info("ignored font change");
			}
		} else if (fontRatio0to1 < 0.95) {
			// end of sub/superScript
			if (deltaVert > 0 && Real.isEqual(deltaVert, -supVert * fontHeight, maxFontSize)) {
				// end of superscript?
				linker = SUP1;
			} else if (deltaVert < 0 && Real.isEqual(deltaVert, -subVert * fontHeight, maxFontSize)) {
				// end of subscript?
				linker = SUB1;
			} else {
				LOG.info("ignored font change");
			}
			if (newText != null) {
				setCurrentBaseY(text1.getCurrentBaseY());
			}
			unscriptFontSize = text1.getFontSize();
		} else {
			LOG.info("change of font size: "+fontSize0+"/"+fontSize1+" .... "+getText()+" ... "+text1.getText());
		}
		if (linker != null) {
			newText = string0 + linker + string1;
			setText(newText);
			setCurrentFontSize(text1.getFontSize());
			// preserve best estimate of text length
			setCalculatedTextEndCoordinate(text1.getCalculatedTextEndCoordinate(fontWidthFactor));
			if (!Double.isNaN(unscriptFontSize)) {
				setFontSize(unscriptFontSize);
				setCurrentFontSize(unscriptFontSize);
			}
		}
		return (newText != null);
	}
	
	public SVGElement getBoundingSVGRect() {
		Real2Range r2r = getBoundingBox();
		SVGRect rect = new SVGRect();
		rect.setBounds(r2r);
		return rect;
	}
	
	/** 
	 * Property of graphic bounding box.
	 * Can be overridden.
	 * 
	 * @return default none
	 */
	protected String getBBFill() {
		return "none";
	}

	/** 
	 * Property of graphic bounding box.
	 * Can be overridden.
	 * 
	 * @return default magenta
	 */
	protected String getBBStroke() {
		return "magenta";
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default 0.5
	 */
	protected double getBBStrokeWidth() {
		return 0.2;
	}
	
	public void createWordWrappedTSpans(Double textWidthFactor, Real2Range boundingBox, Double fSize) {
		String textS = getText().trim();
		if (textS.length() == 0) {
			return;
		}
		setText(null);
		double fontSize = (fSize == null ? getFontSize() : fSize);
		String[] tokens = textS.split(EuclidConstants.S_WHITEREGEX);
		Double x0 = boundingBox.getXMin();
		Double x1 = boundingBox.getXMax();
		Double x = x0;
		Double y0 = boundingBox.getYMin();
		Double y = y0;
		Double deltay = fontSize*1.2;
		y += deltay;
		SVGTSpan span = createSpan(tokens[0], new Real2(x0, y), fontSize);
		int ntok = 1;
		while (ntok < tokens.length) { 
			String s = span.getText();
			span.setText(s+" "+tokens[ntok]);
			double xx = span.getCalculatedTextEndCoordinate(textWidthFactor);
			if (xx > x1) {
				span.setText(s);
				y += deltay;
				span = createSpan(tokens[ntok], new Real2(x0, y), fontSize);
			}
			ntok++;
		}
		clearRotate();
	}
	
	public SVGTSpan createSpan(String text, Real2 xy, Double fontSize) {
		SVGTSpan span = new SVGTSpan();
		span.setXY(xy);
		span.setFontSize(fontSize);
		span.setText(text);
		appendChild(span);
		return span;
	}

	/** makes a new list composed of the texts in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<SVGText> extractTexts(List<SVGElement> elements) {
		List<SVGText> textList = new ArrayList<SVGText>();
		for (AbstractCMElement element : elements) {
			if (element instanceof SVGText) {
				textList.add((SVGText) element);
			}
		}
		return textList;
	}
	
	/** 
	 * Convenience method to extract list of svgTexts in element
	 * 
	 * @param svgElement
	 * @return
	 */
	public static List<SVGText> extractSelfAndDescendantTexts(AbstractCMElement svgElement) {
		return SVGText.extractTexts(SVGUtil.getQuerySVGElements(svgElement, ALL_TEXT_XPATH));
	}
	
	/** 
	 * Convenience method to extract list of svgTexts in element
	 * 
	 * @param svgElement element to query
	 * @param rotAngle angle rotated from 0
	 * @param eps tolerance in rotation angle
	 * @return
	 */
	public static List<SVGText> extractSelfAndDescendantTextsWithSpecificAngle(AbstractCMElement svgElement, Angle targetAngle, double eps) {
		List<SVGText> textList = extractSelfAndDescendantTexts(svgElement);
		List<SVGText> newTextList = new ArrayList<SVGText>();
		for (SVGText text : textList) {
			Transform2 t2 = text.getTransform();
			boolean rotated = false;
			if (t2 == null) {
				rotated = targetAngle.isEqualTo(0.0, eps);
			} else {
				Angle rotAngle = t2.getAngleOfRotation();
				rotated = Real.isEqual(targetAngle.getRadian(), rotAngle.getRadian(), eps);
			}
			if (rotated) {
				newTextList.add(text);
			}
		}
		return newTextList;
	}
	

	public static List<SVGText> getQuerySVGTexts(AbstractCMElement svgElement, String xpath) {
		List<SVGElement> elements = SVGUtil.getQuerySVGElements(svgElement, xpath);
		return extractTexts(elements);
	}


	/** 
	 * Special routine to make sure characters are correctly oriented
	 */
	public void setTransformToRotateAboutTextOrigin() {
		Transform2 transform2 = getTransform();
		RealSquareMatrix rotMat = transform2.getRotationMatrix();
		setTransformToRotateAboutTextOrigin(rotMat);
	}

	public void setTransformToRotateAboutTextOrigin(RealSquareMatrix rotMat) {
		Real2 xy = new Real2(this.getXY());
		Transform2 newTransform2 = new Transform2(new Vector2(xy)); 
		newTransform2 = newTransform2.concatenate(new Transform2(rotMat));
		newTransform2 = newTransform2.concatenate(new Transform2(new Vector2(xy.multiplyBy(-1.0))));
		this.setTransform(newTransform2);
	}

	public List<SVGTSpan> getChildTSpans() {
		tspans = SVGTSpan.extractTSpans(SVGUtil.getQuerySVGElements(this, "./svg:tspan"));
		return tspans;
	}
	
	public String createAndSetFontWeight() {
		String f = super.getFontWeight();
		if (f == null) {
			FontWeight fw = FontWeight.NORMAL;
			String fontFamily = this.getFontFamily();
			if (fontFamily != null) {
				if (fontFamily.toLowerCase().contains(FontWeight.BOLD.toString().toLowerCase())) {
					fw = FontWeight.BOLD;
				}
			}
			this.setFontWeight(fw);
			f = fw.toString();
		}
		return f;
	}
	
	
	public String createAndSetFontStyle() {
		String f = super.getFontStyle();
		if (f == null) {
			FontStyle fs = FontStyle.NORMAL;
			String fontFamily = this.getFontFamily();
			if (fontFamily != null) {
				String ff = fontFamily.toLowerCase();
				if (ff.contains("italic") || ff.contains("oblique")) {
					fs = FontStyle.ITALIC;
				}
			}
			this.setFontStyle(fs);
			f = fs.toString();
		}
		return f;
	}
	
	public boolean isBold() {
		String fontWeight = this.getFontWeight();
		return SVGText.BOLD.equalsIgnoreCase(fontWeight);
	}
	
	public boolean isItalic() {
		String fontStyle = this.getFontStyle();
		return SVGText.ITALIC.equalsIgnoreCase(fontStyle);
	}

	/** normally only present when added by PDF2SVG
	 * of form svgx:fontName="ABCDEF+FOOBar"
	 * @return name (or null)
	 */
	public String getSVGXFontName() {
		String fontName = SVGUtil.getSVGXAttribute(this, FONT_NAME);
		if (fontName == null) {
			fontName = this.getAttributeValue(FONT_NAME);
		}
		// old style
		if (fontName == null) {
			fontName = SVGUtil.getSVGXAttribute(this, FONT_NAME_OLD);
		}
		return fontName; 
	}
	
	/** 
	 * Normally only present when added by PDF2SVG.
	 * <p>
	 * Of form svgx:width="234.0".
	 * <p>
	 * Different to getWidth, which uses "width" attribute and is probably wrong for SVGText.
	 * 
	 * NOTE has to deal with arrays of widths in compacted form, so may also need
	 * getSVGXFontWidthArray().  If multiple values are found returns the first
	 * @return width (or null)
	 */
	public Double getSVGXFontWidth() {
		getSVGXFontWidthArray();
		return fontWidthArray == null || fontWidthArray.size() == 0 ? null : fontWidthArray.get(0); 
	}
	
	/** 
	 * Normally only present when added by PDF2SVG or textDecorator.compactText()
	 * <p>
	 * Of form svgx:width="234.0,450.0".
	 * <p>
	 * Different to getWidth, which uses "width" attribute and is probably wrong for SVGText.
	 * 
	 * @return width (or null)
	 */
	public RealArray getSVGXFontWidthArray() {
		fontWidthArray = parseSingleOrArraySVGXAttribute(SVGElement.WIDTH);
		return fontWidthArray;
	}

	
	
	/** 
	 * Adds svgx:width attribute.
	 * <p>
	 * Only use when constructing new characters, such as spaces, and deconstructing
	 * ligatures.
	 * </p>
	 * <p>
	 * Of form svgx:width="234.0".
	 * </p>
	 * <p>
	 * Different to getWidth, which uses "width" attribute and is probably wrong for SVGText.
	 * </p>
	 * 
	 * @return width (or null)
	 */
	public void setSVGXFontWidth(Double width) {
		SVGUtil.setSVGXAttribute(this, WIDTH, String.valueOf(width));
	}

	public void setSVGXFontWidth(RealArray array) {
		SVGUtil.setSVGXAttribute(this, WIDTH, array.getStringArray());
	}

	public GlyphVector getGlyphVector() {
		if (getFontSize() == null) {
			return null;
		}
		int arbitraryFontSize = 20;
		Font font = new Font(getFontFamily(), (isItalic() ? (isBold() ? Font.BOLD | Font.ITALIC : Font.ITALIC): (isBold() ? Font.BOLD : Font.PLAIN)), arbitraryFontSize);
		font = font.deriveFont((float) (double) getFontSize());
		String text = getText();
		GlyphVector glyphVector = text == null ? null :
			font.createGlyphVector(new FontRenderContext(new AffineTransform(), true, true), text);
		return glyphVector;
	}
	
	/**
	 * @return width of first character
	 * @deprecated Use getWidthOfFirstCharacter(), which now calculates width if it isn't available (so this method does too).
	 */
	@Deprecated
	public Double getScaledWidth() {
		return getWidthOfFirstCharacter();
	}

	/**
	 * @param guessWidth
	 * @return width of first character
	 * @deprecated Use getWidthOfFirstCharacter(), which now calculates width if it isn't available (so this method does too).
	 */
	@Deprecated
	public Double getScaledWidth(boolean guessWidth) {
		return getWidthOfFirstCharacter();
	}

	/** 
	 * Get separation between two characters.
	 * <p>
	 * This is from the end of "this" to the start of nextText.
	 * 
	 * @param nextText
	 * @return
	 */
	public Double getSeparation(SVGText nextText) {
		Double separation = null;
		Double x = getX();
		Double xNext = (nextText == null ? null : nextText.getX());
		Double scaledWidth = getScaledWidth();
		if (x != null && xNext != null && scaledWidth != null) {
			separation = xNext - (x + scaledWidth); 
		}
		return separation;
	}
	
	/** 
	 * Will be zero if fontSize is zero.
	 * 
	 * @return
	 */
	public Double getScaledWidthOfEnSpace() {
		Double fontSize = getFontSize();
		return (fontSize == null ? null : N_SPACE * fontSize);
	}
	

	public Double getEnSpaceCount(SVGText nextText) {
		Double separation = getSeparation(nextText);
		Double enSpace = getScaledWidthOfEnSpace();
		Double scaledWidth = nextText.getScaledWidthOfEnSpace();
		enSpace = enSpace == null || scaledWidth == null ? null : Math.max(enSpace, scaledWidth);
		return (separation == null || enSpace == null || Math.abs(enSpace) < MIN_FONT_SIZE ? null : separation / enSpace);
	}

	/**
	 * @param newCharacters
	 * @param endOfLastCharacterX
	 * @param templateText to copy attributes from
	 */
	public SVGText createSpaceCharacterAfter() {
		SVGText spaceText = new SVGText();
		XMLUtil.copyAttributesFromTo(this, spaceText);
		spaceText.setText(" ");
		spaceText.setX(getCalculatedTextEndX());
		spaceText.setSVGXFontWidth(SPACE_WIDTH1000);
		return spaceText;
	}

	public Double getCalculatedTextEndX() {
		Double scaledWidth = getScaledWidth(); 
		Double x = getX();
		return (x == null || scaledWidth == null ? null : x + scaledWidth);
	}

	public String getString() {
		String s = "";
		List<SVGTSpan> tspans = getChildTSpans();
		if (tspans == null|| tspans.size() == 0) {
			s += toXML();
		} else {
			for (SVGTSpan tspan : tspans) {
				s += tspan.toXML()+"\n";
			}
		}
		return s;
	}

	/** 
	 * Get centre point of text.
	 * <p>
	 * Only works for single character.
	 * 
	 * @param i position of character (currently only 0)
	 * @return
	 */
	public Real2 getCentrePointOfFirstCharacter() {
		getWidthOfFirstCharacter();
		heightOfFirstCharacter = getFontSize();
		Real2 delta = new Real2(widthOfFirstCharacter / 2.0, -heightOfFirstCharacter / 2.0); 
		Real2 xy = getXY();
		return xy.plus(delta);
	}

	public Double getWidthOfFirstCharacter() {
		Double scaledWidth = null;
		Double width = getSVGXFontWidth();
		Double fontSize = getFontSize();
		if (width == null) {
			GlyphVector glyphVector = getGlyphVector();
			if (glyphVector != null) {
				scaledWidth = glyphVector.getGlyphLogicalBounds(0).getBounds2D().getWidth();
			}
		} else if (fontSize != null) {
			scaledWidth = width * SCALE1000 * fontSize;
		}
		return (widthOfFirstCharacter = scaledWidth);
	}
	
	public Double getHeightOfFirstCharacter() {
		return (heightOfFirstCharacter = getFontSize());
	}
	
	public Double getRadiusOfFirstCharacter() {
		getWidthOfFirstCharacter();
		getHeightOfFirstCharacter();
		return (heightOfFirstCharacter == null || widthOfFirstCharacter == null ? null :
			Math.sqrt(heightOfFirstCharacter * heightOfFirstCharacter + widthOfFirstCharacter * widthOfFirstCharacter) / 2.0);
	}

	public BufferedImage createImage(int width, int height) {
		BufferedImage image = ImageUtil.createARGBBufferedImage(width, height);
		if (image == null) return null;
		Graphics2D g = (Graphics2D) image.createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, width, height);
		int fontStyle = (isItalic()) ? Font.ITALIC : Font.PLAIN;
		String fontFamily = getFontFamily();
		int fontSize = (int)(double)getFontSize();
		Font font = new Font(fontFamily, fontStyle, fontSize);
		g.setFont(font);
		g.setColor(Color.BLACK);
		String value = getValue();
		float x = (float) (double) getX();
		float y = (float) (double) getY();
		g.drawString(value, x, y);
		return image;
	}

	public void removeAttributes() {
		int natt = this.getAttributeCount();
		for (int i = 0; i < natt; i++) {
			this.getAttribute(0).detach();
		}
	}
	
	@Override
	public String toString() {
		return "["+this.getText()+"("+this.getXY()+")"+"]";
	}

	public void unrotateRot90() {
		
	}
	/** rotates text about current text x,y point
	 * does NOT transform the point
	 * generally called after the coordinate transformation
	 * @param angle
	 */
	public void rotateText(Angle angle) {
		// current Rt matrix
		Transform2 transform2orig = this.getTransform();
		if (transform2orig == null) {
			transform2orig = new Transform2(); // unit matrix
		}
		Angle angleOrig = transform2orig.getAngleOfRotation();
		Real2 centre = transform2orig.getCentreOfRotation();
		Angle newAngle = angleOrig.plus(angle);
		// final angle is zero, so remove transform attribute
		if (Real.isEqual(newAngle.getRadian(), 0.0, ANGLE_EPS)) {
			this.removeAttribute(TRANSFORM);
		} else {
			// non zero angle, apply it
			Transform2 transform2 = new Transform2(new Vector2(centre));
			transform2 = transform2.concatenate(new Transform2(newAngle));
			transform2 = transform2.concatenate(new Transform2(new Vector2(centre.multiplyBy(-1.0))));
			setTransform(transform2);
		}
	}

	public static List<String> extractStrings(List<SVGText> textList) {
		List<String> stringList = null;
		if (textList != null) {
			stringList = new ArrayList<String>();
			for (SVGText text : textList) {
				stringList.add(text.getText());
			}
		}
		return stringList;
	}

	/** utility to draw a list of text.
	 * 
	 * @param textList
	 * @param file
	 */
	public static void drawTextList(List<? extends SVGElement> textList, File file) {
		SVGG g = new SVGG();
		for (AbstractCMElement text : textList) {
			g.appendChild(text.copy());
		}
		SVGSVG.wrapAndWriteAsSVG(g, file);
	}

	public static List<SVGText> getRotatedTexts(List<SVGText> texts, Angle angle, double eps) {
		List<SVGElement> elements = SVGElement.getRotatedElementList(texts, angle, eps);
		List<SVGText> textList = new ArrayList<SVGText>();
		if (elements != null) {
			for (AbstractCMElement element : elements) {
				textList.add((SVGText) element);
			}
		}
		return textList;
	}

	public static List<SVGText> findHorizontalOrRot90Texts(List<SVGText> texts, LineDirection direction, double eps) {
		List<SVGText> textList = new ArrayList<SVGText>();
		Angle angle = null;
		if (direction.isHorizontal()) {
			angle = new Angle(0.0);
		} else if (direction.isVertical()) {
			angle = new Angle(Math.PI/2.0, Units.RADIANS);
		}
		if (angle != null) {
			textList = getRotatedTexts(texts, angle, eps);
		}
		return textList;
	}

	/** removes characters whose horizontal range does not intersect this.boundingBox.
	 * 
	 * @param range
	 * @return
	 */
	public static List<SVGText> removeStringsCompletelyOutsideRange(List<SVGText> texts, RealRange range) {
		List<SVGText> textList = new ArrayList<SVGText>();
		for (SVGText text : texts) {
			if (text.getBoundingBox().getXRange().intersects(range)) {
				textList.add(text);
			}
		}
		return textList;
	}
	
	/** Texts outside y=0 are not part of the plot but confuse calculation of
	 * bounding box 
	 * @param TextList
	 * @return
	 */
	public static List<SVGText> removeTextsWithNegativeY(List<SVGText> textList) {
		List<SVGText> newTexts = new ArrayList<SVGText>();
		for (SVGText text : textList) {
			Real2Range bbox = text.getBoundingBox();
			if (bbox.getYMax() >= 0.0) {
				newTexts.add(text);
			}
		}
		return newTexts;
	}

	/** remove elements with empty content
	 * remove elements with null content or empty string
	 * 
	 * @param removeWhiteSpace if true remove elements with whitespace
	 * @param textList
	 * @return
	 */
	public static List<SVGText> removeTextsWithEmptyContent(List<SVGText> textList, boolean removeWhiteSpace) {
		List<SVGText> newTexts = new ArrayList<SVGText>();
		for (SVGText text : textList) {
			String value = text.getValue();
			if (value != null && !"".equals(value) && !"null".equals(value)) {
				if (!Character.isWhitespace(value.charAt(0))) {
					newTexts.add(text);
				} else if (!removeWhiteSpace) {
					newTexts.add(text);
				}
			} else {
				// empty text
			}
		}
		return newTexts;
	}

	public boolean isRot90() {
		boolean rot90 = ROT90.isEqualTo(this.getAngleOfRotation(), ANGLE_EPS);
		return rot90;
	}

	public void setX(RealArray array) {
		this.addAttribute(new Attribute(X, array.getStringArray()));
	}

	public void setY(RealArray array) {
		this.addAttribute(new Attribute(Y, array.getStringArray()));
	}

	public void rotateTextAboutPoint(Real2 centre, Transform2 t90) {
		Real2 textXY = getXY();
		Real2 delta = textXY.subtract(centre);
		delta.transformBy(t90);
		Real2 textXY1 = centre.plus(delta);
		setXY(textXY1);
		removeAttribute("transform");
	}

	public HtmlSup createSuperscript() {
		HtmlSup sup = new HtmlSup();
		sup.appendChild(getText());
		return sup;
	}

	public HtmlSub createSubscript() {
		HtmlSub sub = new HtmlSub();
		sub.appendChild(getText());
		return sub;
	}

	/** creates textwith default pont-size and fill.
	 * 
	 * @param xy
	 * @param string
	 * @return
	 */
	public static SVGText createDefaultText(Real2 xy, String value) {
		String cssStyle = "fill:red;font-size:3;";
		SVGText text = SVGText.createText(xy, value, cssStyle);
		return text;
	}

	public static SVGText createText(Real2 xy, String value, String cssValue) {
		SVGText text = new SVGText(xy, value);
		text.setCSSStyle(cssValue);
		return text;
	}

	public static List<SVGText> readAndCreateTexts(List<File> svgFiles) {
		List<SVGText> texts = new ArrayList<SVGText>();
		for (File svgFile : svgFiles) {
			List<SVGText> texts0 = SVGText.extractSelfAndDescendantTexts(SVGElement.readAndCreateSVG(svgFile));
			texts.addAll(texts0);
		}
		return texts;
	}

	/** convenience method that reads SVGFiles in directory and extracts all texts
	 * 
	 * @param indir
	 * @return
	 */
	public static List<SVGText> readSVGFilesAndCreateTexts(File indir) {
		File[] files = indir.listFiles();
		List<File> svgFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.toString().endsWith(".svg")) {
				svgFiles.add(file);
			}
		}
		List<SVGText> svgTexts = SVGText.readAndCreateTexts(svgFiles);
		return svgTexts;
	}

	/**
	 * adds character/s to end of current string
	 * slightly inefficient as should use StringBuilder
	 * @param unicode
	 */
	public void appendText(String unicode) {
		String text = this.getText();
		if (text == null) {
			text = unicode;
		} else {
			text += unicode;
		}
		this.setText(text);
	}

	/** adds an x coordinate to X array.
	 * 
	 * @param x2
	 */
	public void appendX(double x) {
		RealArray xArray = getXArray();
		xArray.addElement(x);
		this.setX(xArray);
	}

	public void removeCharacter(int i) {
		String textS = getText();
		if (i >= 0 && i < textS.length()) {
			setText(textS.substring(0, i)+textS.substring(i+1));
			RealArray xArray = getXArray();
			try {
				xArray.deleteElement(i);
				this.setX(xArray);
			} catch (ArrayIndexOutOfBoundsException aioobe) {
				LOG.error("BUG in SVGText delete");
			}
		}
	}

	public void removeLeadingSpaces() {
		while(true) {
			String text = getText();
			if (text != null && text.startsWith(" ")) {
				removeCharacter(0);
			} else {
				break;
			}
		}
	}

	/** somettimes the font styles and weights are not set properly.
	 * 
	 * this uses the fontName/family to look for substrings such as "ital'
	 */
	public void addEmpiricalStylesFromFont() {
		String name = getSVGXFontName();
		String family = getFontFamily();
		String nameFamily = (String.valueOf(name)+String.valueOf(family)).toLowerCase();
		String weight = String.valueOf(getFontWeight()).toLowerCase();  // bold or normal
		String style = String.valueOf(getFontStyle()).toLowerCase();  // italic or normall
		if (style.equals(FontStyle.NORMAL.toString().toLowerCase()) && nameFamily.indexOf("ital") != -1) {
			setFontStyle(FontStyle.ITALIC);
		}
		if (weight.equals(FontWeight.NORMAL.toString().toLowerCase()) && nameFamily.indexOf("bold") != -1) {
			setFontWeight(FontWeight.BOLD.toString());
		}
	}

	/** use when you want a text that has default font-family
	 * 
	 * @param xy
	 * @param text
	 * @param fontSize
	 * @param stroke
	 * @return
	 */
	public static SVGText createDefaultText(Real2 xy, String text, int fontSize, String stroke) {
		return createText(xy, text, "font-size:"+fontSize+";stroke:"+stroke+";"+"font-family:Helvetica");
	}

	/** reorders the text.
	 * 
	 * new string has charAt(i) == text.charAt(intSet.elementAt(i))
	 * @param intSet index of new order
	 */
	public void reorderByIndex(IntSet intSet) {
		String text = getText();
		int indexSize = intSet.size();
		if (text.length() != indexSize) {
			LOG.error("Cannot reorder unequal arrays; this="+text.length()+"; idx="+indexSize);
		} else {
//			LOG.debug("OK");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < indexSize; i++) {
				sb.append(text.charAt(intSet.elementAt(i)));
			}
			this.setText(sb.toString());
		}
	}

	/** gets the string contents as text
	 * 
	 * @param texts
	 * @return list of string content
	 */
	public static List<String> getTextStrings(List<SVGText> texts) {
		List<String> textList = new ArrayList<String>();
		for (SVGText text : texts) {
			textList.add(text.getText());
		}
		return textList;
		
	}

	public static void sortByX(List<SVGText> textList) {
		Collections.sort(textList, new XComparator());
	}

	/** if svgText has sibling rect treat as a bounding box.
	 * normally used when third party programs create this box
	 */
	public void addBBoxAttributeFromSiblingRect() {
		List<Element> rects = XMLUtil.getQueryElements(this, "../*[local-name()='"+SVGRect.TAG+"']");
		if (rects.size() > 0) {
			Real2Range bbox = ((SVGRect)rects.get(0)).getBoundingBox();
			this.addAttribute(new Attribute(SVGElement.BOUNDING_BOX, bbox.toString()));
		}
	}

	


}
class XComparator implements Comparator<SVGText> {

	@Override
	public int compare(SVGText t1, SVGText t2) {
		if (t1 == null || t2 == null) return 0;
		return (int)(t1.getX() - t2.getX());
	}
}