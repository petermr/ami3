/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.graphics.svg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Real2Range.BoxDirection;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.eucl.euclid.RealRangeArray;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.objects.SVGArrow;
import org.contentmine.graphics.svg.objects.SVGTriangle;
import org.contentmine.graphics.svg.text.SVGWord;
import org.contentmine.graphics.svg.text.SVGWordBlock;
import org.contentmine.graphics.svg.text.SVGWordLine;
import org.contentmine.graphics.svg.text.SVGWordPage;
import org.contentmine.graphics.svg.text.SVGWordPageList;
import org.contentmine.graphics.svg.text.SVGWordPara;
import org.contentmine.graphics.svg.text.SVGWordPhrase;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.canonical.Canonicalizer;

/** 
 * Base class for lightweight generic SVG element.
 * <p>
 * No checking - i.e. can take any name or attributes.
 * 
 * @author pm286
 */
public class SVGElement extends GraphicsElement {



	private static final String PARENT_ID = "parentId";
	public static final Logger LOG = Logger.getLogger(SVGElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}


	private static final int EXTRA_TRANSFORM_PRECISION = 2;
	private static final Double DEFAULT_FONT_SIZE = 8.0;

	public final static String ALL_ELEMENT_XPATH = "//svg:*";

	public final static String IMPROPER = "improper";
	public final static String IMPROPER_TRUE = "true";
	public final static String MATRIX = "matrix";
	public final static String ROTATE = "rotate";
	public final static String SCALE = "scale";
	public static final String TRANSFORM = "transform";
	public static final String WIDTH = "width";
	public final static String TRANSLATE = "translate";
	public final static String X = "x";
	public final static String Y = "y";
	public final static String CX = "cx";
	public final static String CY = "cy";
	public final static String YMINUS = "-Y";
	public final static String YPLUS = "Y";
	public static final String SVG = ".svg";
	public final static String SUBTYPE = "subtype"; // subclassed elements which are not full SVG

	public static final String STROKE_DASHARRAY = "stroke-dasharray";



	public final static Set<String> COMMON_ATT_NAMES = new HashSet<String>();

	static final String URL = "url";
	static {
		createCommonAttNameSet();
	}
	private static void createCommonAttNameSet() {
		COMMON_ATT_NAMES.add(StyleBundle.STROKE);
		COMMON_ATT_NAMES.add(StyleBundle.STROKE_WIDTH);
		COMMON_ATT_NAMES.add(StyleBundle.FILL);
		COMMON_ATT_NAMES.add(StyleBundle.FONT_FAMILY);
		COMMON_ATT_NAMES.add(StyleBundle.FONT_WEIGHT);
		COMMON_ATT_NAMES.add(StyleBundle.FONT_SIZE);
	}

	public static final String BOUNDING_BOX = "boundingBox";
	
	private Element userElement;
	private String strokeSave;
	private String fillSave;

	protected Real2Range boundingBox = null;
	protected boolean boundingBoxCached = false;
	//private AffineTransform savedAffineTransform;
	
	
	/** 
	 * Constructor.
	 * 
	 * @param name
	 */
	public SVGElement(String name) {
		super(name,SVG_NAMESPACE);
	}

	public SVGElement(SVGElement element) {
        super(element);
        this.userElement = element.userElement;
	}
	
	public SVGElement(SVGElement element, String tag) {
        super(element, tag);
        this.userElement = element.userElement;
	}
	
	/** 
	 * Copy constructor from non-subclassed elements
	 */
	public static SVGElement readAndCreateSVG(Element element) {
		SVGElement newElement = null;
		String tag = element.getLocalName();
		if (tag == null || tag.equals(S_EMPTY)) {
			throw new RuntimeException("no tag");
		} else if (tag.equals(SVGCircle.TAG)) {
			newElement = new SVGCircle();
		} else if (tag.equals(SVGClipPath.TAG)) {
			newElement = new SVGClipPath();
		} else if (tag.equals(SVGDefs.TAG)) {
			newElement = new SVGDefs();
		} else if (tag.equals(SVGDesc.TAG)) {
			newElement = new SVGDesc();
		} else if (tag.equals(SVGEllipse.TAG)) {
			newElement = new SVGEllipse();
		} else if (tag.equals(SVGG.TAG)) {
			newElement = createSVGGOrClasses(element);
		} else if (tag.equals(SVGImage.TAG)) {
			newElement = new SVGImage();
		} else if (tag.equals(SVGLine.TAG)) {
			newElement = createSVGLineOrClasses(element);
		} else if (tag.equals(SVGPath.TAG)) {
			newElement = new SVGPath();
		} else if (tag.equals(SVGPattern.TAG)) {
			newElement = new SVGPattern();
		} else if (tag.equals(SVGPolyline.TAG)) {
			newElement = new SVGPolyline();
		} else if (tag.equals(SVGPolygon.TAG)) {
			newElement = createSVGPolygonOrClasses(element);
		} else if (tag.equals(SVGRect.TAG)) {
			newElement = new SVGRect();
		} else if (tag.equals(SVGScript.TAG)) {
			newElement = new SVGScript();
		} else if (tag.equals(SVGSVG.TAG)) {
			newElement = new SVGSVG();
		} else if (tag.equals(SVGText.TAG)) {
			newElement = new SVGText();
		} else if (tag.equals(SVGTSpan.TAG)) {
			newElement = new SVGTSpan();
		} else if (tag.equals(SVGTitle.TAG)) {
			newElement = new SVGTitle();
		} else {
			newElement = new SVGG();
			newElement.setSVGClassName(tag);
			LOG.trace("unsupported svg element: "+tag);
		}
		if (newElement != null) {
	        newElement.copyAttributesFrom(element);
	        createSubclassedChildren(element, newElement);
		}
        return newElement;
	}

	private static SVGElement createSVGGOrClasses(Element element) {
		SVGElement newElement;
		String clazz = getClassAttributeValue(element);
		// word stuff
		if (SVGWordPara.CLASS.equals(clazz)) {
			newElement = new SVGWordPara();
		} else if (SVGWord.CLASS.equals(clazz)) {
			newElement = new SVGWord();
		} else if (SVGWordBlock.CLASS.equals(clazz)) {
			newElement = new SVGWordBlock();
		} else if (SVGWordLine.CLASS.equals(clazz)) {
			newElement = new SVGWordLine();
		} else if (SVGWordPage.CLASS.equals(clazz)) {
			newElement = new SVGWordPage();
		} else if (SVGWordPageList.CLASS.equals(clazz)) {
			newElement = new SVGWordPageList();
		} else if (SVGWordPhrase.CLASS.equals(clazz)) {
			newElement = new SVGWordPhrase();
		} else {
			newElement = new SVGG();
		}
		return newElement;
	}

	private static SVGElement createSVGLineOrClasses(Element element) {
		SVGElement newElement;
		String clazz = getClassAttributeValue(element);
		if (SVGArrow.ARROW.equals(clazz)) {
			newElement = new SVGArrow();
		} else {
			newElement = new SVGLine();
		}
		return newElement;
	}

	private static SVGElement createSVGPolygonOrClasses(Element element) {
		SVGElement newElement;
		String clazz = getClassAttributeValue(element);
		if (SVGTriangle.TRIANGLE.equals(clazz)) {
			newElement = new SVGTriangle();
		} else {
			newElement = new SVGPolygon();
		}
		return newElement;
	}

	/** 
	 * Converts an SVG file to SVGElement
	 * 
	 * @param file
	 * @return
	 */
	public static SVGElement readAndCreateSVG(File file) {
		Element element = (file == null) ? null : XMLUtil.parseQuietlyToDocument(file).getRootElement();
		return (element == null ? null : readAndCreateSVG(element));
	}
	
	/** 
	 * Converts an SVG file to SVGElement
	 * 
	 * @param file
	 * @return
	 */
	public static AbstractCMElement readAndCreateSVG(InputStream is) {
		Element element = XMLUtil.parseQuietlyToDocument(is).getRootElement();
		return (element == null ? null : readAndCreateSVG(element));
	}
	
	protected static void createSubclassedChildren(Element oldElement, AbstractCMElement newElement) {
		if (oldElement != null) {
			for (int i = 0; i < oldElement.getChildCount(); i++) {
				Node node = oldElement.getChild(i);
				Node newNode = null;
				if (node instanceof Text) {
					String value = node.getValue();
					newNode = new Text(value);
				} else if (node instanceof Comment) {
					newNode = new Comment(node.getValue());
				} else if (node instanceof ProcessingInstruction) {
					newNode = new ProcessingInstruction((ProcessingInstruction) node);
				} else if (node instanceof Element) {
					newNode = readAndCreateSVG((Element) node);
				} else {
					throw new RuntimeException("Cannot create new node: "+node.getClass());
				}
				newElement.appendChild(newNode);
			}
		}
	}
	
	public boolean isEqualTo(AbstractCMElement element) {
		boolean equals = false;
		if (element.getClass().equals(this.getClass())) {
			XMLUtil.equalsCanonically(element, this, true);
		}
		return equals;
	}
	
	public String getCanonicalizedXML() {
		OutputStream out = new ByteArrayOutputStream();
		Canonicalizer canonicalizer = new Canonicalizer(out, false);
		String s = null;
		try {
			canonicalizer.write(this);
			s = out.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return s;
	}
	
	/**
	 * @return the sVG_NAMESPACE
	 */
	public static String getSVG_NAMESPACE() {
		return SVG_NAMESPACE;
	}

	/**
	 * @param g2d
	 */
	public void draw(Graphics2D g2d) {
		drawElement(g2d);
	}
	
	/** draws children recursively
	 * 
	 * @param g2d
	 */
	protected void drawElement(Graphics2D g2d) {
		saveGraphicsSettingsAndApplyTransform(g2d);
		Elements gList = this.getChildElements();
		for (int i = 0; i < gList.size(); i++) {
			SVGElement svge = (SVGElement) gList.get(i);
			svge.drawElement(g2d);
		}
		restoreGraphicsSettingsAndTransform(g2d);
	}
	
	/**
	 * @return the transform
	 */
	public Transform2 getTransform() {
		Transform2 t2 = ensureTransform();
		return t2;
	}
	/**
	 * @param transform the transform to set
	 */
	public void setTransform(Transform2 transform) {
		processTransform(transform);
	}
	
	protected void processTransform(Transform2 transform) {
		double[] matrix = transform.getMatrixAsArray();
		this.addAttribute(new Attribute(TRANSFORM, MATRIX+"(" +
				matrix[0] +"," +
				matrix[3] +"," +
				matrix[1] +"," +
				matrix[4] +"," +
				matrix[2]+","+
				matrix[5]+
			")"));
	}

	/** applies any transform attribute and removes it.
	 * not yet hierarchical, so only use on lines, text, etc.
	 */
	public void applyTransformAttributeAndRemove() {
		Attribute transformAttribute = this.getAttribute(TRANSFORM);
		if (transformAttribute != null) {
			Transform2 transform2 = createTransform2FromTransformAttribute(transformAttribute.getValue());
			this.applyTransformPreserveUprightText(transform2);
			getAttribute(TRANSFORM).detach();
			double det = transform2.determinant();
			// improper rotation ?
			if (det < 0) {
				Transform2 t = new Transform2(
					new double[] {
							1.0,  0.0,  0.0,
							0.0, -1.0,  0.0,
							0.0,  0.0,  1.0,
					});
				transform2 = transform2.concatenate(t);
				this.addAttribute(new Attribute(IMPROPER, IMPROPER_TRUE));
			}
			// is object rotated?
			Angle angle = transform2.getAngleOfRotation();
			if (angle.getRadian() > Math.PI/4.) {
				this.addAttribute(new Attribute(ROTATE, YPLUS));
			}
			if (angle.getRadian() < -Math.PI/4.) {
				this.addAttribute(new Attribute(ROTATE, YMINUS));
			}
		}
	}
	
	/**
	 * uses transform attribute to get angle, 0 if absent
	 * @return angle in degrees
	 */
	public Double getAngleOfRotationFromTransformInDegrees() {
		Double angle = 0.0;
		Angle rotationAngle = getAngleOfRotation();
		if (rotationAngle != null) {
			angle = rotationAngle.getDegrees();
		}
		return angle;
	}

	/** get the angle described by the Transform attribute.
	 * 
	 * @return
	 */
	public Angle getAngleOfRotation() {
		Transform2 transform = this.getTransform();
		Angle rotationAngle = new Angle(0.0);
		if (transform != null) {
			rotationAngle = transform.getAngleOfRotation();
		}
		return rotationAngle;
	}
	
	/**
	 * uses transform attribute to get angle, 0 if absent
	 * @return angle in degrees
	 */
	public Double getAngleOfRotationInRadiansFromTransform() {
		Angle angle = getAngleOfRotation();
		return angle == null ? null : angle.getRadian();
	}
	
	/**
	 * uses transform attribute to get angle, 0 if absent
	 * @return angle in degrees
	 */
	public Double getAngleOfRotationInDegreesFromTransform() {
		Angle angle = getAngleOfRotation();
		return angle == null ? null : angle.getDegrees();
	}
	
	/** currently a no-op.
	 * subclassed by elements with coordinates
	 * @param transform
	 */
	public void applyTransformPreserveUprightText(Transform2 transform) {
		if (this instanceof SVGDefs) { // maybe add others
		} else {
			LOG.info("No transform applied to: "+this.getClass());
		}
	}
	
	public static Transform2 createTransform2FromTransformAttribute(String transformAttributeValue) {
/**
    * matrix(<a> <b> <c> <d> <e> <f>)
    * translate(<tx> [<ty>])
    * scale(<sx> [<sy>]),
    * rotate(<rotate-angle> [<cx> <cy>])
    * skewX(<skew-angle>)
    * skewY(<skew-angle>)
 */
		Transform2 transform2 = null;
		if (transformAttributeValue != null) {
			transform2 = new Transform2();
			List<Transform2> transformList = new ArrayList<Transform2>();
			String s = transformAttributeValue.trim();
			while (s.length() > 0) {
				int lb = s.indexOf(XMLConstants.S_LBRAK);
				int rb = s.indexOf(XMLConstants.S_RBRAK);
				if (lb == -1 || rb == -1 || rb < lb) {
					throw new RuntimeException("Unbalanced or missing brackets in transform");
				}
				String kw = s.substring(0, lb);
				String values = s.substring(lb + 1, rb);
				// remove unwanted spaces
				values = values.replaceAll("  *", " ");
				s = s.substring(rb+1).trim();
				Transform2 t2 = makeTransform(kw, values);
				transformList.add(t2);
			}
			for (Transform2 t2 : transformList) {
				transform2 = transform2.concatenate(t2);
			}
		}
		return transform2;
	}
	
	private static Transform2 makeTransform(String keyword, String valueString) {
		// remove unwanted space
		valueString = valueString.replace(S_SPACE+S_PLUS, S_SPACE);
		valueString = valueString.replace(S_COMMA+S_SPACE, S_COMMA);
		valueString = valueString.replace(S_PIPE+S_SPACE, S_PIPE);
		LOG.trace("Transform "+valueString);
		Transform2 t2 = new Transform2();
		String[] vv = valueString.trim().split(S_COMMA+S_PIPE+S_SPACE);
		RealArray ra = new RealArray(vv);
		double[] raa = ra.getArray();
		double[][] array = t2.getMatrix();
		if (keyword.equals(SCALE) && ra.size() > 0) {
			array[0][0] = raa[0];
			if (ra.size() == 1) {
				array[1][1] = raa[0];
			} else if (ra.size() == 2) {
				array[1][1] = raa[1];
			} else if (ra.size() != 1){
				throw new RuntimeException("Only 1 or 2 scales allowed");
			}
		} else if (keyword.equals(TRANSLATE) && ra.size() > 0) {
			array[0][2] = raa[0];
			if (ra.size() == 1) {
				array[1][2] = 0.0;
			} else if (ra.size() == 2) {
				array[1][2] = raa[1];
			} else {
				throw new RuntimeException("Only 1 or 2 translate allowed");
			}
		} else if (keyword.equals(ROTATE) && ra.size() == 1) {
			double c = Math.cos(raa[0]*Math.PI/180.);
			double s = Math.sin(raa[0]*Math.PI/180.);
			array[0][0] = c;
			array[0][1] = s;
			array[1][0] = -s;
			array[1][1] = c;
		} else if (keyword.equals(ROTATE) && ra.size() == 3) {
			throw new RuntimeException("rotate about point not yet supported");
		} else if (keyword.equals(MATRIX) && ra.size() == 6) {
			t2 = createTransformFrom1D(ra.getArray());
		} else {
			throw new RuntimeException("Unknown/unsuported transform keyword: "+keyword);
		}

		return t2;
	}

	private static Transform2 createTransformFrom1D(double[] raa) {
		double[][] array = new double[3][];
		for (int i = 0; i < 3; i++) {
			array[i] = new double[3];
		}
		array[0][0] = raa[0];
		array[0][1] = raa[2];
		array[0][2] = raa[4];
		array[1][0] = raa[1];
		array[1][1] = raa[3];
		array[1][2] = raa[5];
		array[2][0] = 0.0;
		array[2][1] = 0.0;
		array[2][2] = 1.0;
		return new Transform2(new RealSquareMatrix(array));	
	}
	/**
	 * 
	 * @param s
	 */
	public void setScale(double s) {
		Transform2 transform = ensureTransform();
		Transform2 t = new Transform2(
				new double[]{
				s, 0., 0.,
				0., s, 0.,
				0., 0., 1.
				});
		transform = transform.concatenate(t);
		processTransform(transform);
	}

	protected Transform2 ensureTransform() {
		Transform2 t2 = new Transform2();
		String t2Value = this.getAttributeValue(TRANSFORM);
		if (t2Value != null) {
			t2 = createTransform2FromTransformAttribute(t2Value);		
		}
		return t2;
	}

	/**
	 */
	public void setCumulativeTransformRecursively() {
		setCumulativeTransformRecursively("set");
	}

	/**
	 */
	public void clearCumulativeTransformRecursively() {
		setCumulativeTransformRecursively(null);
	}
	
	/**
	 * @param value if null clear the transform else concatenate
	 * may be overridden by children such as Text
	 */
	protected void setCumulativeTransformRecursively(Object value) {
		if (value != null) {
			Transform2 thisTransform = this.getTransform2FromAttribute();
			ParentNode parentNode = this.getParent();
			Transform2 parentTransform = (parentNode instanceof GraphicsElement) ?
					((GraphicsElement) parentNode).getCumulativeTransform() : new Transform2();
			this.cumulativeTransform = (thisTransform == null) ? parentTransform : parentTransform.concatenate(thisTransform);
			for (int i = 0; i < this.getChildElements().size(); i++) {
				Node child = this.getChild(i);
				if (child instanceof SVGElement) {
					((SVGElement) child).setCumulativeTransformRecursively(value);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param attName
	 * @return color
	 */
	public Color getColor(String attName) {
		String attVal = this.getAttributeValue(attName);
		Double opacity = this.getOpacity();
		Color color = getJava2DColor(attVal, opacity);
		return color;
	}

	/**
	 * transforms xy
	 * messy
	 * @param xy is transformed
	 * @param transform
	 * @return transformed xy
	 */
	public static Real2 transform(Real2 xy, Transform2 transform) {
		xy.transformBy(transform);
		return xy;
	}

	/**
	 * transforms xy
	 * messy
	 * @param xy is transformed
	 * @param transform
	 * @return transformed xy
	 */
	public static Double transform(Double d, Transform2 transform) {
		RealArray ra = transform.getScales();
		d = (ra == null) ? d : d * ra.get(0);
		return d;
	}

	protected double getDouble(String attName) {
		String attVal = this.getAttributeValue(attName);
		double xx = Double.NaN;
		if (attVal != null) {
			try {
				xx = Double.parseDouble(attVal);
			} catch (NumberFormatException e) {
				throw e;
			}
		}
		return xx;
	}

	/**
	 * uses attribute value to calculate transform
	 * @return current transform or null
	 */
	public Transform2 getTransform2FromAttribute() {
		Transform2 t = null;
		String ts = this.getAttributeValue(TRANSFORM);
		if (ts != null) {
			if (!ts.startsWith(MATRIX+"(")) {
				throw new RuntimeException("Bad transform: "+ts);
			}
			ts = ts.substring((MATRIX+"(").length());
			ts = ts.substring(0, ts.length() - 1);
			ts = ts.replace(S_COMMA, S_SPACE);
			RealArray realArray = new RealArray(ts);
			t = createTransformFrom1D(realArray.getArray());
		}
		return t;
	}
	
	public Transform2 ensureTransform2() {
		Transform2 t = getTransform2FromAttribute();
		if (t == null) {
			t = new Transform2();
			setTransform(t);
		}
		return t;
	}
	
	/**
	 * sets attribute value from transform
	 * @param transform
	 */
	public void setAttributeFromTransform2(Transform2 transform) {
		if (transform != null) {
			double[] dd = transform.getMatrixAsArray();
			String ts = MATRIX+
			S_LBRAK+
			dd[0]+S_COMMA+
			dd[1]+S_COMMA+
			dd[3]+S_COMMA+
			dd[4]+S_COMMA+
			dd[2]+S_COMMA+
			dd[5]+
			S_RBRAK;
			this.addAttribute(new Attribute(TRANSFORM, ts));
		}
	}
	
	/**
	 */
	@Deprecated
	public void draw() {
	}

	/**
	 * 
	 * @param xy
	 */
	public void translate(Real2 xy) {
		Transform2 transform = ensureTransform();
		Transform2 t = new Transform2(
			new double[] {
			1., 0., xy.getX(),
			0., 1., xy.getY(),
			0., 0., 1.
		});
		transform = transform.concatenate(t);
		processTransform(transform);
	}

	public void addDashedStyle(double bondWidth) {
		String style = this.getAttributeValue(STYLE);
		style += STROKE_DASHARRAY + " : "+bondWidth*2+" "+bondWidth*2+";";
		this.addAttribute(new Attribute(STYLE, style));
	}
	
	public void toggleFill(String fill) {
		this.fillSave = this.getFill();
		this.setFill(fill);
	}
    
	public void toggleFill() {
		this.setFill(fillSave);
	}
	
	public void toggleStroke(String stroke) {
		this.strokeSave = this.getStroke();
		this.setStroke(stroke);
	}
    
	public void toggleStroke() {
		this.setStroke(strokeSave);
	}
    
	public void applyAttributes(Graphics2D g2d) {
		applyStrokeColor(g2d);
//		applyFillColor(g2d);
	}

	/**
	 * 
	 * @param filename
	 * @throws IOException
	 */
	public static void test(String filename) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		SVGSVG svg = new SVGSVG();
		GraphicsElement g = new SVGG();
		g.setFill("yellow");
		svg.appendChild(g);
		GraphicsElement line = new SVGLine(new Real2(100, 200), new Real2(300, 50));
		line.setFill("red");
		line.setStrokeWidth(3.);
		line.setStroke("blue");
		g.appendChild(line);
		GraphicsElement circle = new SVGCircle(new Real2(300, 150), 20);
		circle.setStroke("red");
		circle.setFill("yellow");
		circle.setStrokeWidth(3.);
		g.appendChild(circle);
		GraphicsElement text = new SVGText(new Real2(50, 100), "Foo");
		text.setFontFamily("TimesRoman");
		text.setStroke("green");
		text.setFill("red");
		text.setStrokeWidth(1.5);
		text.setFontSize(new Double(20.));
		text.setFontStyle(FontStyle.ITALIC);
		text.setFontWeight(FontWeight.BOLD);
		g.appendChild(text);
		SVGUtil.debug(svg, fos, 2);
		fos.close();		
	}
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length > 0) {
			test(args[0]);
		}
	}

	public Element getUserElement() {
		return userElement;
	}

	public void setUserElement(Element userElement) {
		this.userElement = userElement;
	}

	protected void applyStrokeColor(Graphics2D g2d) {
		String colorS = "black";
		String stroke = this.getStroke();
		if (stroke != null) {
			colorS = stroke;
		}
		Color color = colorMap.get(colorS);
		if (color != null && g2d != null) {
			g2d.setColor(color);
		}
	}
	
	protected void applyFillColor(Graphics2D g2d) {
		String colorS = "black";
		String fill = this.getFill();
		if (fill != null) {
			colorS = fill;
		}
		Color color = colorMap.get(colorS);
		if (color != null && g2d != null) {
			g2d.setColor(color);
		}
	}

	/**
	 * get double value of attribute.
	 * the full spec includes units but here we expect only numbers. Maybe later...
	 * if coordinate is not given defaults to ZERO.
	 * 
	 * @param attName
	 * @return
	 */
	public double getCoordinateValueDefaultZero(String attName) {
		double d = Double.NaN;
		String v = this.getAttributeValue(attName);
		if (v == null) {
//			LOG.warn("DEFAULT ZERO "+attName);
			d = 0.0;
		} else {
			try {
				d = Double.parseDouble(v);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Cannot parse SVG coordinate "+v);
			}
		}
		return d;
	}
	
	/** subclassed to tidy format.
	 * by default formats children
	 * @param places decimal places
	 */
	public void format(int places) {
//		formatCommonAttributes(places);
		formatTransform(places + EXTRA_TRANSFORM_PRECISION);
		List<SVGElement> childElements = SVGUtil.getQuerySVGElements(this,  "./svg:*");
		for (SVGElement childElement : childElements) {
			childElement.format(places);
		}
	}
	// be careful as transforms require several places in matrix
	private void formatCommonAttributes(int places) {
		formatTransform(places);
		formatFontSize(places);
		// maybe more later
	}

	public void formatTransform(int places) {
		Transform2 t2 = this.getTransform();
		if (t2 != null) {
			if (!t2.isUnit()) {
				t2 = formatTransform(t2, places);
				this.setTransform(t2);
			}
		}
	}

	public void formatTransformRecursively(int places) {
		this.formatTransform(places);
		List<SVGElement> childElements = SVGElement.generateElementList(this, ".//svg:*");
		for (SVGElement childElement : childElements) {
			childElement.formatTransformRecursively(places);
		}
	}

	private Transform2 formatTransform(Transform2 t2, int places) {
		RealArray ra = new RealArray(t2.getMatrixAsArray());
		ra.format(places);
		t2 = new Transform2(ra.getArray());
		return t2;
	}

	public Double getX() {
		// manage arrays ...
		return this.getCoordinateValueDefaultZero(X);
	}

	public Double getY() {
		return this.getCoordinateValueDefaultZero(Y);
	}

	public Double getCX() {
		return this.getCoordinateValueDefaultZero(CX);
	}

	public Double getCY() {
		return this.getCoordinateValueDefaultZero(CY);
	}
	
	public void setBoundingBoxAttribute(Integer decimalPlaces) {
		Real2Range r2r = this.getBoundingBox();
		if (r2r != null) {
			if (decimalPlaces != null) {
				r2r.format(decimalPlaces);
			}
			SVGUtil.setSVGXAttribute(this, BOUNDING_BOX, r2r.toString());
		}
	}

	/**
	 * @param x1 the x1 to set
	 */
	public void setCXY(Real2 x1) {
		this.setCX(x1.getX());
		this.setCY(x1.getY());
	}

	public void setCX(double x) {
		this.addAttribute(new Attribute(CX, String.valueOf(x)));
	}

	public void setCY(double y) {
		this.addAttribute(new Attribute(CY, String.valueOf(y)));
	}

	public Real2 getCXY() {
		return new Real2(this.getCX(), this.getCY());
	}

	public void setX(double x) {
		this.addAttribute(new Attribute(X, String.valueOf(x)));
	}

	public void setY(double y) {
		this.addAttribute(new Attribute(Y, String.valueOf(y)));
	}
	
	public Real2 getXY() {
		Double x = this.getX();
		Double y = this.getY();
		return new Real2(x, y);
	}
	
	public void setXY(Real2 xy) {
		setX(xy.getX());
		setY(xy.getY());
	}
	
	public Double getWidth() {
		String w = this.getAttributeValue(WIDTH);
		w = SVGUtil.convertUnits(w);
		return (w == null) ? null : Double.valueOf(w);
	}
	
	public Double getHeight() {
		String h = this.getAttributeValue("height");
		return (h == null) ? null : Double.valueOf(h);
	}

	public void setWidth(double w) {
		this.addAttribute(new Attribute(WIDTH, String.valueOf(w)));
	}
	
	public void setHeight(double h) {
		this.addAttribute(new Attribute("height", String.valueOf(h)));
	}
	
	/** set class.
	 * case sensitive
	 * overwrites previous value/s.
	 * 
	 * 
	 * @param clazz
	 */
	public void setSVGClassName(String clazz) {
		if (clazz != null) {
			clazz = clazz.trim();
			this.addAttribute(new Attribute(CLASS, clazz));
		}
	}

	/** multiple classes are allowed. duplicates are ignored.
	 * class is split at spaces so multiple labels can be added.
	 * 
	 * @param clazz class or classes to add; 
	 */
	public void addSVGClassName(String clazz) {
		addLabel(CLASS, clazz);
	}

	/** multiple labels are allowed. duplicates are ignored.
	 * newLabel is split at spaces so multiple labels can be added.
	 * @param labelValue label or labels to add;
	 */
	private void addLabel(String labelName, String labelValue) {
		if (labelValue != null) {
			labelValue = labelValue.trim().replaceAll("\\s+", " "); // remove unnecessary spaces
			String originalAttValue = this.getAttributeValue(labelName);
			if (originalAttValue != null) {
				// pad with blanks
				if ((" "+originalAttValue+" ").contains(" "+labelValue+" ")) {
					LOG.trace("duplicate class: "+labelValue);
				} else {
					this.addAttribute(new Attribute(labelName, originalAttValue+" "+labelValue));
				}
			} else {
				this.addAttribute(new Attribute(labelName, labelValue));
			}
		}
	}
	
	public String getSVGClassNameString() {
		return this.getAttributeValue(CLASS);
	}

	public String getAncestorIDString() {
		return this.getAttributeValue(ANCESTOR);
	}

	public List<String> getSVGClassNames() {
		return getLabelValues(CLASS);
	}

	public List<String> getAncestorIDs() {
		return getLabelValues(ANCESTOR);
	}

	private List<String> getLabelValues(String attName) {
		List<String> classes = new ArrayList<String>();
		String attValue = this.getAttributeValue(attName);
		if (attValue != null) {
			classes = Arrays.asList(attValue.split("\\s+"));
		}
		return classes;
	}

	/** traverse all children recursively
	 * often copied to subclasses to improve readability
	 * 
	 * @return null by default
	 */
	public Real2Range getBoundingBox() {
		if (boundingBoxNeedsUpdating()) {
			aggregateBBfromSelfAndDescendants();
		}
		return boundingBox;
	}
	
	/** return unrooted x-y range of element.
	 * 
	 * @return the (integer) ranges of the element.
	 */
	public java.awt.Dimension getDimension() {
		Real2 real2 = getReal2Dimension();
		return new Dimension((int) real2.getX(), (int) real2.getY());
	}

	/** return unrooted x-y range of element.
	 * 
	 * @return
	 */
	public Real2 getReal2Dimension() {
		Real2 dimension = null;
		getBoundingBox();
		if (boundingBox != null) {
			RealRange xrange = boundingBox.getXRange();
			RealRange yrange = boundingBox.getYRange();
			if (xrange != null && yrange != null) {
				dimension = new Real2(xrange.getRange(),  yrange.getRange());
			}
		}
		return dimension;
	}

	protected void aggregateBBfromSelfAndDescendants() {
		Nodes childNodes = this.query("./svg:*", XMLConstants.SVG_XPATH);
		if (childNodes.size() > 0) {
			boundingBox = new Real2Range();
		}
		for (int i = 0; i < childNodes.size(); i++) {
			SVGElement child = (SVGElement) childNodes.get(i);
			LOG.trace(child.toXML());
			child.setBoundingBoxCached(false);
			Real2Range childBoundingBox = child.getBoundingBox();
			LOG.trace("CHILD BBOX "+childBoundingBox);
			if (childBoundingBox != null) {
				if (!childBoundingBox.isValid()) {
					LOG.trace("invalid child BBox: "+"parent: "+child.getClass()+"; "+childBoundingBox);
				} else {
					boundingBox = boundingBox.plus(childBoundingBox);
				}
			}
		}
	}

	protected boolean boundingBoxNeedsUpdating() {
		return boundingBox == null || !boundingBoxCached;
	}
	
	/**  recalculation of bounding box.
	 *  calculation of BBox can be time consuming. This asserts that the bbox 
	 *  will not change and the box can be re-used without recalculating.
	 *  A crude form of memo-isation. Up to the caller to recalculate when the objects
	 *  are likely to change.
	 *  
	 * @param boundingBoxCached 
	 */
	public void setBoundingBoxCached(boolean boundingBoxCached) {
		this.boundingBoxCached = boundingBoxCached;
	}
	
	public static void setBoundingBoxCached(List<? extends SVGElement> elementList, boolean boundingBoxCached) {
		for (SVGElement element : elementList) {
			element.setBoundingBoxCached(boundingBoxCached);
		}
	}

	public SVGElement createGraphicalBoundingBox() {
		Real2Range r2r = this.getBoundingBox();
		SVGRect rect = createGraphicalBox(r2r, getBBStroke(), getBBFill(), getBBStrokeWidth(), getBBOpacity());
		if (this.getAttribute(TRANSFORM) != null) {
			Transform2 t2 = this.getTransform();
			if (t2 != null) {
				if (!t2.isUnit()) {
					Real2 txy = t2.getTranslation();
					rect.setTransform(t2);
				}
			}
		}
		return rect;
	}
	
	public static SVGRect createGraphicalBox(Real2Range r2r, String stroke, String fill, Double strokeWidth, Double opacity) {
		SVGRect rect = null;
		if (r2r != null) {
			RealRange xr = r2r.getXRange();
			RealRange yr = r2r.getYRange();
			if (xr != null && yr != null) {
				double dx = (xr.getRange() < Real.EPS) ? 1.0 : 0.0; 
				double dy = (yr.getRange() < Real.EPS) ? 1.0 : 0.0; 
				rect = createGraphicalBox(xr, yr, dx, dy);
				rect.setStrokeWidth(strokeWidth);
				rect.setStroke(stroke);
				rect.setFill(fill);
				rect.setOpacity(opacity);
			}
		}
		return rect;
	}
	
	public static SVGRect createGraphicalBox(Real2Range r2r, double dx, double dy) {
		RealRange xr = r2r.getXRange();
		RealRange yr = r2r.getYRange();
		return createGraphicalBox(xr,  yr, dx, dy);
	}

	private static SVGRect createGraphicalBox(RealRange xr, RealRange yr, double dx, double dy) {
		SVGRect rect;
		rect = new SVGRect(new Real2(xr.getMin()-dx, yr.getMin()-dy), new Real2(xr.getMax()+dx, yr.getMax()+dy));
		return rect;
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default none
	 */
	protected String getBBFill() {
		return GraphicsElement.NONE;
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default red
	 */
	protected String getBBStroke() {
		return "red";
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default 1.0
	 */
	protected double getBBStrokeWidth() {
		return 0.4;
	}

	/** property of graphic bounding box
	 * can be overridden
	 * @return default 1.0
	 */
	protected double getBBOpacity() {
		return 1.0;
	}

	public static void drawBoundingBoxes(List<SVGElement> elements, AbstractCMElement svgParent, String stroke, String fill, double strokeWidth, double opacity) {
		for (SVGElement element : elements) {
			SVGRect svgBox = SVGElement.drawBox(element.getBoundingBox(), svgParent, stroke, fill, strokeWidth, opacity);
		}
	}
	public static void drawBoundingBoxes(List<SVGElement> elements, String stroke, String fill, double strokeWidth, double opacity) {
		for (SVGElement element : elements) {
			SVGRect svgBox = SVGElement.drawBox(element.getBoundingBox(), null, stroke, fill, strokeWidth, opacity);
		}
	}
	
	public static void drawBoxes(List<Real2Range> boxes, AbstractCMElement svgParent, String stroke, String fill, double strokeWidth, double opacity) {
		for (Real2Range box : boxes) {
			SVGRect svgBox = SVGElement.drawBox(box, svgParent, stroke, fill, strokeWidth, opacity);
		}
	}
	public static SVGRect drawBox(Real2Range box, AbstractCMElement svgParent,
			String stroke, String fill, double strokeWidth, double opacity) {
		SVGRect svgBox = createGraphicalBox(box, stroke, fill, strokeWidth, opacity);
		if (svgBox != null && svgParent != null) {
			svgParent.appendChild(svgBox);
		}
		return svgBox;
	}
	
	public static List<Real2Range> createBoundingBoxList(List<? extends SVGElement> elements) {
		List<Real2Range> boxes = new ArrayList<Real2Range>();
		for (SVGElement elem : elements) {
			Real2Range bbox = elem.getBoundingBox();
			boxes.add(bbox);
		}
		return boxes;
	}

	public SVGRect drawBox(String stroke, String fill, double strokeWidth, double opacity) {
		return SVGElement.drawBox(getBoundingBox(), this, stroke, fill, strokeWidth, opacity);
	}

	public static void applyTransformsWithinElementsAndFormat(AbstractCMElement svgElement) {
		List<SVGElement> elementList = generateElementList(svgElement, ".//svg:*[@" + TRANSFORM + "]");
		for (SVGElement element : elementList) {
			element.applyTransformAttributeAndRemove();
			element.format(2);
		}
	}

	/**
	 * @return
	 */
	public static List<SVGElement> generateElementList(Element element, String xpath) {
		Nodes childNodes = element.query(xpath, XMLConstants.SVG_XPATH);
		List<SVGElement> elementList = new ArrayList<SVGElement>();
		for (int i = 0; i < childNodes.size(); i++) {
			elementList.add((SVGElement) childNodes.get(i));
		}
		return elementList;
	}
	
	public void setTitle(String title) {
		addAttribute(new Attribute(TITLE, title));
	}
	
	public String getTitle() {
		return getAttributeValue(TITLE);
	}
	
	public void setId(String id) {
		if (id != null) {
			addAttribute(new Attribute(ID, id));
		}
	}
	
	public String getId() {
		return getAttributeValue(ID);
	}

	/** removes all transformation attributes
	 * @transform
	 * THIS IS NORMALLY ONLY DONE AFTER APPLYING CUMULATIVE TRANSFORMATIONS
	 * also dangerous as the ancestor may govern other descendants
	 */
	public void removeAncestorTransformations() {
		Nodes ancestorAttributes = query("ancestor::*/@transform");
		for (int i = 0; i < ancestorAttributes.size(); i++) {
			ancestorAttributes.get(i).detach();
		}
	}

	public void removeEmptySVGG() {
		List<SVGElement> emptyGList = SVGUtil.getQuerySVGElements(this, ".//svg:g[(count(*)+count(svg:*))=0]");
		for (AbstractCMElement g : emptyGList) {
			g.detach();
		}
		LOG.trace("removed emptyG: "+emptyGList.size());
	}

	/** tests whether element is geometricallyContained within this
	 * for most elements uses this.getBoundingBox()
	 * can be overridden for special cases such as circle
	 * @param element
	 * @return
	 */
	public boolean includes(SVGElement element) {
		Real2Range thisBbox = this.getBoundingBox();
		Real2Range elementBox = (element == null) ? null : element.getBoundingBox();
		return thisBbox != null && thisBbox.includes(elementBox);
	}

	public boolean isIncludedBy(RealRangeArray mask, Direction direction) {
		Real2Range bbox = this.getBoundingBox();
		RealRange range = Direction.HORIZONTAL.equals(direction) ? bbox.getXRange() : bbox.getYRange();
		return mask.includes(range);
	}
	
	public boolean isIncludedBy(Real2Range bbox) {
		return bbox == null ? false : bbox.includes(this.getBoundingBox());
	}

	public boolean isIncludedBy(RealRange mask, Direction direction) {
		Real2Range bbox = this.getBoundingBox();
		RealRange range = Direction.HORIZONTAL.equals(direction) ? bbox.getXRange() : bbox.getYRange();
		return mask.includes(range);
	}
	
	public static List<SVGElement> extractElementsContainedInBox(List<? extends SVGElement> elements, Real2Range bbox) {
		List<SVGElement> containedElements = new ArrayList<SVGElement>();
		for (SVGElement element : elements) {
			if (bbox.includes(element.getBoundingBox())) {
				containedElements.add(element);
			}
		}
		return containedElements;
	}

	/** filters out elements larger than boxSize.
	 * compares the boundingBoxes of elements and selects if either range is larger than
	 * corresponding range in box size
	 * 
	 * @param elements modifies list by removing failures
	 * @param boxSize
	 * @return list of boxes larger in either x or y (never null) 
	 */
	public static void removeElementsLargerThanBox(List<? extends SVGElement> elements, Real2 boxSize) {
		for (int i = elements.size() - 1; i >= 0; i--) {
			SVGElement element = elements.get(i);
			Real2Range bbox = element.getBoundingBox();
			if (bbox.getXRange().getRange() > boxSize.x ||
			    bbox.getYRange().getRange() > boxSize.y) {
				elements.remove(i);
			}
		}
	}

	/** filters out elements smaller than boxSize.
	 * compares the boundingBoxes of elements and selects if either range is smaller than
	 * corresponding range in box size
	 * 
	 * @param elements modifies list by removing failures
	 * @param boxSize
	 * @return list of boxes smaller in either x or y (never null) 
	 */
	public static void removeElementsSmallerThanBox(List<? extends SVGElement> elements, Real2 boxSize) {
		for (int i = elements.size() - 1; i >= 0; i--) {
			SVGElement element = elements.get(i);
			Real2Range bbox = element.getBoundingBox();
			if (bbox.getXRange().getRange() < boxSize.x ||
			    bbox.getYRange().getRange() < boxSize.y) {
				elements.remove(i);
			}
		}
	}

	public void detachDescendantElementsOutsideBox(Real2Range bbox) {
		List<SVGElement> descendants = SVGElement.extractSelfAndDescendantElements(this);
		for (SVGElement descendant : descendants) {
			if (descendant != this) {
				if (!bbox.includes(descendant.getBoundingBox())) {
					descendant.detach();
				}
			}
		}
	}

	/** elements filtered by yrange
	 * 
	 * @param textList
	 * @param yrange
	 * @return
	 */
	public static List<? extends SVGElement> getElementListFilteredByRange(
			List<? extends SVGElement> elemList, RealRange range, RealRange.Direction dir) {
		List<SVGElement> elemList0 = new ArrayList<SVGElement>();
		for (SVGElement elem : elemList) {
			RealRange range0 = getRange(elem, dir);
			if (range.includes(range0)) {
				elemList0.add(elem);
			}
		}
		return elemList0;
	}

	private static RealRange getRange(SVGElement elem, RealRange.Direction dir) {
		Real2Range bbox = elem.getBoundingBox();
		RealRange range = (RealRange.Direction.HORIZONTAL.equals(dir)) ? 
				bbox.getXRange() : bbox.getYRange();
		return range;
	}

	public static RealRangeArray getRealRangeArray(List<? extends SVGElement> elementList, RealRange.Direction dir) {
//		List<? extends SVGElement> elementList0 = getElementListFilteredByRange(elementList, dir);
		RealRangeArray realRangeArray = new RealRangeArray();
		for (SVGElement element : elementList) {
			RealRange range = getRange(element, dir);
			realRangeArray.add(range);
		}
		return realRangeArray;
	}
	
	/** returns elements which are included in mask
	 * 
	 * @param elementList
	 * @param direction
	 * @param mask
	 * @return
	 */
	public static List<? extends SVGElement> filter(List<? extends SVGElement> elementList, Direction direction, RealRangeArray mask) {
		List<SVGElement> eList = new ArrayList<SVGElement>();
		for (SVGElement element : elementList) {
			if (element.isIncludedBy(mask, direction)) {
				eList.add(element);
			}
		}
		return eList;
	}

	public static List<? extends SVGElement> filterHorizontally(List<? extends SVGElement> elementList, RealRangeArray horizontalMask) {
		return filter(elementList, Direction.HORIZONTAL, horizontalMask);
	}
	
	public static List<? extends SVGElement> filterVertically(List<? extends SVGElement> elementList, RealRangeArray verticalMask) {
		return filter(elementList, Direction.VERTICAL, verticalMask);
	}
	
	/**
	 * Creates a mask (list of RealRanges) that mirror the elements added
	 * Example
	 *   SVGRect(new Real2(0., 10.), new Real2(30., 40.) 
	 *   SVGRect(new Real2(40., 10.), new Real2(50., 40.) 
	 *   SVGRect(new Real2(45., 10.), new Real2(55., 40.) 
	 *   creates a horizontal mask RealRange(0., 30.),  RealRange(40., 55.),
	 * @param elementList elements to create mask
	 * @return RealRange array corresponding to (overlapped) ranges of elements
	 */
	public static RealRangeArray createMask(List<? extends SVGElement> elementList, Direction direction) {
		RealRangeArray realRangeArray = new RealRangeArray();
		for (SVGElement element : elementList) {
			Real2Range bbox = element.getBoundingBox();
			realRangeArray.add((Direction.HORIZONTAL.equals(direction) ? bbox.getXRange() : bbox.getYRange()));
		}
		realRangeArray.sortAndRemoveOverlapping();
		return realRangeArray;
	}

	public static RealRangeArray createMask(List<SVGElement> elementList, Direction direction, double tolerance) {
			RealRangeArray realRangeArray = new RealRangeArray();
			for (SVGElement element : elementList) {
				Real2Range bbox = element.getBoundingBox();
				RealRange range = (Direction.HORIZONTAL.equals(direction) ? bbox.getXRange() : bbox.getYRange());
				range.extendBothEndsBy(tolerance);
				realRangeArray.add((Direction.HORIZONTAL.equals(direction) ? bbox.getXRange() : bbox.getYRange()));
			}
			realRangeArray.sortAndRemoveOverlapping();
			return realRangeArray;
	}
	
	public final static Real2Range createBoundingBox(List<? extends SVGElement> elementList) {
		Real2Range bbox = null;
		if (elementList != null && elementList.size() > 0) {
			bbox = new Real2Range(elementList.get(0).getBoundingBox());
			for (int i = 1; i < elementList.size(); i++) {
				SVGElement element = elementList.get(i);
				bbox = bbox.plus(element.getBoundingBox());
			}
		}
		return bbox;
	}
	

	public RealRange getRealRange(Direction direction) {
		Real2Range bbox = this.getBoundingBox();
		return bbox == null ? null : bbox.getRealRange(direction);
	}

	public Real2 getCentreForClockwise90Rotation() {
		Real2Range bbox = getBoundingBox();
		double yRange = bbox.getYRange().getRange();
		Real2 centre = bbox.getLLURCorners()[0].plus(new Real2(yRange/2.0, yRange/2.0));
		return centre;
	}

	public void rotateAndAlsoUpdateTransforms(Real2 centreOfRotation, Angle angle) {
		Transform2 t2 = Transform2.getRotationAboutPoint(angle, centreOfRotation);
		Transform2 oldT2 = getTransform();
		if (oldT2 != null) {
			t2 = t2.concatenate(oldT2);
		}
		setTransform(t2);
		this.applyTransformAttributeAndRemove();
	}

	public static List<SVGElement> extractSelfAndDescendantElements(SVGElement element) {
		return SVGUtil.getQuerySVGElements(element, ALL_ELEMENT_XPATH);
	}

	public static List<SVGElement> getRotatedDescendantElements(SVGElement svgElement, Angle angle, double eps) {
		List<SVGElement> elementList = SVGElement.extractSelfAndDescendantElements(svgElement);
		List<SVGElement> filteredList = getRotatedElementList(elementList, angle, eps);
		return filteredList;
	}

	/**
	 * 
	 * @param elements list to filter
	 * @param angle that elements should be rotated by
	 * @param eps tolerance in radians
	 * @return
	 */
	public static List<SVGElement> getRotatedElementList(List<? extends SVGElement> elements, Angle angle, double eps) {
		List<SVGElement> filteredList = new ArrayList<SVGElement>();
		if (elements != null) {
			for (SVGElement svgElem : elements) {
				if (angle.isEqualTo(svgElem.getAngleOfRotation(), eps)) {
					filteredList.add(svgElem);
				}
			}
		}
		return filteredList;
	}

	public static void rotateAndAlsoUpdateTransforms(
			List<? extends SVGElement> svgList, Real2 centreForRotation, Angle angle) {
		for (SVGElement svgElement : svgList) {
			svgElement.rotateAndAlsoUpdateTransforms(centreForRotation, angle);
		}
		
	}

	public void rotateAndAlsoUpdateTransforms(Angle angle) {
		Real2 centreOfRotation = this.getCentreForClockwise90Rotation();
		this.rotateAndAlsoUpdateTransforms(centreOfRotation, angle);
	}

	public static void format(List<? extends SVGElement> elementList, int nplaces) {
		for (SVGElement element : elementList) {
			element.format(nplaces);
		}
	}
	
	public boolean isBold() {
		return StyleBundle.isBold(this);
	}
	
	public boolean isItalic() {
		return StyleBundle.isItalic(this);
	}

	public String getFontFamily() {
		String value = StyleBundle.getFontFamily(this);
		return (value != null) ? value : this.getAttributeValue(StyleBundle.FONT_FAMILY);
	}

//	public String getFontName() {
//		String value = StyleBundle.getFontName(this);
//		return (value != null) ? value : this.getAttributeValue(StyleBundle.FONT_NAME);
//	}

	public String getFontWeight() {
		String value = StyleBundle.getFontWeight(this);
		return (value != null) ? value : this.getAttributeValue(StyleBundle.FONT_WEIGHT);
	}

	public String getFontStyle() {
		String value = StyleBundle.getFontStyle(this);
		return (value != null) ? value : this.getAttributeValue(StyleBundle.FONT_STYLE);
	}

	public String getFill() {
		String value = StyleBundle.getFill(this);
		return (value != null) ? value : this.getAttributeValue(StyleBundle.FILL);
	}

	public String getStroke() {
		String value = StyleBundle.getStroke(this);
		return (value != null) ? value : this.getAttributeValue(StyleBundle.STROKE);
	}

	/** get opacity from opacity attribute and fall through to style attribute
	 * 
	 * @return 0.0 if not found
	 */

	public Double getStrokeWidth() {
		Double value = StyleBundle.getStrokeWidth(this);
		if (value == null) {
			String attVal = this.getAttributeValue(StyleBundle.STROKE_WIDTH);
			value = attVal == null ? null : StyleBundle.getDouble(attVal);
		}
		return value == null ? 0.0 : value;
	}

	/** get opacity from opacity attribute and fall through to style attribute
	 * 
	 * @return 1.0 if not found
	 */

	public Double getOpacity() {
		Double value = StyleBundle.getOpacity(this);
		if (value == null) {
			String attVal = this.getAttributeValue(StyleBundle.OPACITY);
			value = attVal == null ? null : StyleBundle.getDouble(attVal);
		}
		return value == null ? 1.0 : value;
	}

	/** get fontSize from font-size attribute and fall through to style attribute
	 * 
	 * @return null if none
	 */
	public Double getFontSize() {
		this.convertOldStyleToStyle();
		Double value = StyleBundle.getFontSize(this);
		if (value == null) {
			LOG.trace("missing font-size: "+this.getStyle());
			String attVal = getAttributeFromStyle(StyleBundle.FONT_SIZE);
			if (attVal == null) {
				attVal = this.getAttributeValue(StyleBundle.FONT_SIZE);
			}
			value = attVal == null ? DEFAULT_FONT_SIZE : StyleBundle.getDouble(attVal);
		}
		return value;
	}

	private String getAttributeFromStyle(String attName) {
		StyleAttributeFactory styleAttributeFactory = this.getOrCreateStyleAttributeFactory();
		return styleAttributeFactory == null ? null : styleAttributeFactory.getAttributeValue(attName);
	}

	/** remove stroke, stroke-width, fill from element.
	 *  
	 * @param element
	 */
	public static void removeStyleAttributes(GraphicsElement element) {
		removeAttributeByName(element, STROKE);
		removeAttributeByName(element, STROKE_WIDTH);
		removeAttributeByName(element, FILL);
	}

	/** remove a no-namespace attribute if it exists else no-op.
	 * 
	 * @param element
	 * @param name
	 */
	public static void removeAttributeByName(GraphicsElement element, String name) {
		Attribute attribute = element.getAttribute(name);
		if (attribute != null) {
			attribute.detach();
		}
	}

	/** remove elements from list if inside the box.
	 * uses svgElement.isIncludedBy(real2Range)
	 * @param svgList
	 * @param real2Range box
	 */
	public static void removeElementsInsideBox(List<? extends SVGElement> svgList, Real2Range real2Range) {
		for (int i = svgList.size() - 1; i >= 0; i--) {
			SVGElement svgElement = svgList.get(i);
			if (svgElement.isIncludedBy(real2Range)) {
				svgList.remove(i);
			} else {
//				LOG.trace("keep "+svgElement.toXML());
			}
		}
	}

	/** remove elements from list if inside the box.
	 * uses !svgElement.isIncludedBy(real2Range)
	 * 
	 * @param svgList
	 * @param real2Range box
	 */
	public static void removeElementsOutsideBox(List<? extends SVGElement> svgList, Real2Range bbox) {
		for (int i = svgList.size() - 1; i >= 0; i--) {
			SVGElement svgElement = svgList.get(i);
			if (!svgElement.isIncludedBy(bbox)) {
				svgList.remove(i);
			} else {
//				LOG.trace("keep "+svgElement.toXML());
			}
		}
	}
	
	public void addTitle(String t) {
		SVGTitle title = new SVGTitle(t);
		this.appendChild(title);
	}
	
	public SVGElement createElementWithRotatedDescendants(Angle angle) {
		List<SVGElement> svgElementList = SVGElement.generateElementList(this, "*");
		Real2Range bbox = getBoundingBox();
		Real2 centre = bbox.getSquareBoxCentre(BoxDirection.LEFT);
		SVGElement g = new SVGG();
		Transform2 rotationTransform = new Transform2(angle);
		Transform2 rotationTranslationTransform = Transform2.getRotationAboutPoint(angle, centre);
		for (AbstractCMElement svgElement : svgElementList) {
			SVGElement elementCopy = (SVGElement) svgElement.copy();
			if (svgElement instanceof SVGText) {
				SVGText text1 = (SVGText) elementCopy;
//				text1.rotateTextAboutPoint(centre, rotationTransform);
				text1.applyTransformPreserveUprightText(rotationTranslationTransform);
				text1.removeAttribute(TRANSFORM);
				g.appendChild(text1);
			} else {
				Class<?> clazz = elementCopy.getClass();
				elementCopy.applyTransformPreserveUprightText(rotationTranslationTransform);
				g.appendChild(elementCopy);
			}
		}
		return SVGSVG.wrapAsSVG(g);		
	}
	
	/**  probably obsolete.
	 * 
	 * @param centre
	 * @param t90
	 */
	private void rotateElementAboutPoint(Real2 centre, Transform2 t90) {
		Real2 textXY = getXY();
		Real2 delta = textXY.subtract(centre);
		delta.transformBy(t90);
		Real2 textXY1 = centre.plus(delta);
		setXY(textXY1);
		removeAttribute(TRANSFORM);
	}



	public String createURLIdRef() {
		String urlIdRef = null;
		String id = this.getId();
		if (id != null) {
			urlIdRef = URL + "(#"+id+")";
		}
		return urlIdRef;
	}

	public void appendChildren(List<? extends SVGElement> elements) {
		for (AbstractCMElement element : elements) {
			this.appendChild(element);
		}
	}

	public void appendChildCopies(List<? extends SVGElement> elements) {
		for (AbstractCMElement element : elements) {
			this.appendChild(element.copy());
		}
	}

	/** convenience method to parse SVG text.
	 * 
	 * @param svgXml
	 * @return
	 */
	public static AbstractCMElement readAndCreateSVG(String svgXml) {
		return SVGElement.readAndCreateSVG(XMLUtil.parseXML(svgXml));
	}

	/** sets styles on all elements.
	 * 
	 * @param rects
	 * @param cssValue
	 */
	public static void setCSSStyle(List<? extends SVGElement> elements, String cssValue) {
		for (SVGElement element: elements) {
			element.setCSSStyle(cssValue);
		}
	}

	/** extract all files with extension *.svg in directory.
	 * 
	 * @param dir
	 * @return
	 */
	public static List<File> extractSVGFiles(File dir) {
		List<File> svgFiles = new ArrayList<File>(Arrays.asList(dir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.toString().endsWith(SVG);
			}
		})));
		Collections.sort(svgFiles);
		return svgFiles;
	}

	public static Real2Range createTotalBox(List<? extends SVGElement> elementList) {
		Real2Range bbox = null;
		if (elementList.size() > 0) {
			bbox = ((SVGElement)elementList.get(0)).getBoundingBox();
			for (int i = 1; i < elementList.size(); i++) {
				SVGElement element = elementList.get(i);
				Real2Range box1 = element.getBoundingBox();
				bbox = bbox.plusEquals(box1);
			}
		}
		return bbox;
	}

	/** extracts all elements generated by xpath on llist of elements.
	 * 
	 * @param elementList extracted elements
	 * @param xpath applied to all elements in elementLlist
	 * @return aggregate list of all extracted elements
	 */
	public static List<SVGElement> extractElementList(List<? extends SVGElement> elementList, String xpath) {
		List<SVGElement> newElementList = new ArrayList<SVGElement>();
		for (AbstractCMElement element : elementList) {
			List<SVGElement> newElements = SVGElement.generateElementList(element, xpath);
			newElementList.addAll(newElements);
		}
		return newElementList;
	}

	/** appends copies of all elements to svgElement.
	 * 
	 * @param g
	 * @param elementList
	 */
	public static void appendCopy(SVGG g, List<? extends SVGElement> elementList) {
		for (AbstractCMElement element : elementList) {
			g.appendChild(element.copy());
		}
	}

	/** query descendant tree for complete qords in class attribute.
	 * "foo" will match "foo junk", "junk foo", but not "foojunk" or "myfoo junk"
	 * 
	 * @param queryClass
	 * @return
	 */
	public List<SVGElement> querySelfAndDescendantsForClass(String queryClass) {
		String clazz = CLASS;
		List<SVGElement> results = querySelfAndDescendantsForClass(clazz, queryClass);
		return results;
	}

	/** query descendant tree for attribute attName with given value(s).
	 * typical attNames are "id" and "class"
	 * this allows searching for whitespace-separated values, e.g.
	 *   class="foo bar"
	 *   will be matched by value = "foo" or "bar but not "foobar", or "myfoo"
	 *   
	 * @param attName
	 * @param queryClass
	 * @return
	 */
	public List<SVGElement> querySelfAndDescendantsForClass(String attName, String value) {
		String xpath = ".//*[contains(concat(' ',@" + attName + ",' '),concat(' ','"+value+"',' '))]";
		return SVGUtil.getQuerySVGElements(this, xpath);
	}

	/** returns a single element with given complete word value in class attribute.
	 * 
	 * @param value to search for as complete word
	 * @return null if 0 or >1 hits
	 */
	public SVGElement getSingleElementWithClassValue(String value) {
		List<SVGElement> elements = this.querySelfAndDescendantsForClass(CLASS, value);
		return elements.size() != 1 ? null : elements.get(0);
	}
	
	/** returns text value of single element with given complete word value in class attribute.
	 * 
	 * @param value to search for as complete word
	 * @return null if 0 or >1 hits
	 */
	public String getSingleValueWithClassValue(String value) {
		SVGElement element = this.getSingleElementWithClassValue(value);
		return element == null ? null : element.getValue();
	}
	
	public void setSubtype(String subtype) {
		this.addAttribute(new Attribute(SUBTYPE, subtype));
	}
	
	public String getSubtype() {
		return this.getAttributeValue(SUBTYPE);
	}

	public void setParentID(String id) {
		if (id != null) {
			this.addAttribute(new Attribute(PARENT_ID, id));
		}
	}

	public void formatFontSize(int size) {
		if (this != null) {
			setFontSize(Util.format(getFontSize(), size));
		}
	}
	public void formatOpacity(int size) {
		if (this != null) {
			setOpacity(Util.format(getOpacity(), size));
		}
	}

	/** adds copy of attribute to every element
	 * 
	 * @param list
	 * @param attribute
	 */
	public static List<? extends SVGElement>  addAttribute(List<? extends SVGElement> elementList, Attribute attribute) {
		for (SVGElement element : elementList) {
			element.addAttribute(new Attribute(attribute));
		}
		return elementList;
	}

	/** adds copy of attributeList to every element
	 * 
	 * @param list
	 * @param attribute
	 */
	public static List<? extends SVGElement> addAttributes(List<? extends SVGElement> elementList, Attribute ... attributeList) {
		for (Attribute attribute : attributeList) {
			addAttribute(elementList, attribute);
		}
		return elementList;
	}

	/** gets elements with specific attribute values (not floats) 
	 * allows for style bundle
	 * 
	 * @param elementList
	 * @param attName
	 * @param attVal
	 * @return
	 */
	public static List<SVGElement> getElementsWithAttribute(List<? extends SVGElement> elementList, String attName, String attVal) {
	List<SVGElement> newElementList = new ArrayList<>();
		for (SVGElement element : elementList) {
			String elementAttVal = null;
			Object obj = element.getSubStyle(attName);
			elementAttVal = obj == null ? null : String.valueOf(obj);
			elementAttVal = elementAttVal != null ? elementAttVal : element.getAttributeValue(attVal) ;
			if (attVal.equals(elementAttVal)) {
//				LOG.debug("a "+attName+"/"+attVal);
				newElementList.add(element);
			} else {
//				LOG.debug("NOT "+attName+"/"+elementAttVal);
			}
		}
		return newElementList;
	}



}
