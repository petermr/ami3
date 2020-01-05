package org.contentmine.pdf2svg2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.shading.AxialShadingPaint;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.PDTextState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.GraphicsElement;
import org.contentmine.graphics.svg.GraphicsElement.FontStyle;
import org.contentmine.graphics.svg.GraphicsElement.FontWeight;
import org.contentmine.graphics.svg.SVGClipPath;
import org.contentmine.graphics.svg.SVGDefs;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGPathPrimitive;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGText.RotateText;
import org.contentmine.graphics.svg.SVGUtil;
import org.contentmine.graphics.svg.path.ClosePrimitive;
import org.contentmine.graphics.svg.path.CubicPrimitive;
import org.contentmine.graphics.svg.path.LinePrimitive;
import org.contentmine.graphics.svg.path.MovePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;
import org.eclipse.jetty.util.log.Log;

import boofcv.misc.UnsupportedException;
import nu.xom.Attribute;

/** intercepts graphics primitives sent to Java AWT
 *  
 *  NOTE: all subclassed methods must call super as this keeps the state.
 *  We believe that getGraphicsState() will capture all the state for us.
 *  
 *  Clipping and defs not supported
 *  
 * @author pm286
 */
/** merges PageParserZero and PageParserOne
 * 
 * 
 * @author pm286
 *
 */
public class PageParserTwo extends AbstractPageParser {
	private static final Logger LOG = Logger.getLogger(PageParserTwo.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String CLIP_PATH = "clipPath";
	private static final Graphics2D TEXT = null;
	private static final String SVG_RHMARGIN = "svg_rhmargin";
	private static final double YMAX = 800.;

//	private SVGG svgg;
	private double yEpsilon = 0.05; // guess
	private double scalesEpsilon = 0.1; // guess
	private int nPlaces = 3;
	private double angleEps = 0.005;
	
//	private SVGText currentSVGText;
//	private TextParameters currentTextParameters;
//	private Double currentX;
//	private Double currentY;
//	private TextParameters textParameters;
//	private Real2 currentScales;
//	private SVGPath currentSvgPath;
	private String clipString;
//	private Real2 currentDisplacement;
//	private Real2 currentXY;
//	private int codepoint;
	// clipping
	private Map<String, Integer> integerByClipStringMap;
	private Set<String> clipStringSet;
	// defs
	private SVGElement defs1;
	private Point2D currentPoint;
//	private PathPrimitiveList currentPathPrimitiveList;

	PageParserTwo(PageDrawerParameters parameters, int iPage, AMIDebugParameters debugParams) throws IOException        {
        super(parameters, debugParams);
        init();
    	this.pageSerial = PageSerial.createFromZeroBasedPage(iPage);
    }

    void init() {
    	super.init();
    	LOG.trace("created parserPageDrawer");
    	svgg = new SVGG();
    	svgg.setFill("none");
    	integerByClipStringMap = new HashMap<String, Integer>();
    	yMax = YMAX; // hopefully overwritten by mediaBox
    	setDefaults();

	}

	private void setDefaults() {
		viewBox = new Real2Range(new RealRange(-100, 1000), new RealRange(-100, 1000));
//		maxPrimitives = 5000; /// 
	}

    /**
     * Glyph bounding boxes.
     * This is actually where the codepoint is passed in
     */
    // ZERO

//    @Override
//    /** would be best to set up temporary array for text coordinates and values.
//     * also deal with rotated text here
//     */
//    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
//                             Vector displacementxy) throws IOException    {
//    	super.showGlyph(textRenderingMatrix, font, code, unicode, displacementxy);
//    	this.codepoint = code;
//
//    	Vector v = font.getDisplacement(codepoint);
//    	if (v.getY() != 0.0) {
//    		throw new RuntimeException(" unprocessed y fontDisplacement: "+v); // y doesn't seem to be used
//    	}
//    	textParameters = new TextParameters(textRenderingMatrix, font);
//    	Real2 scales = textParameters.getScales().format(nPlaces);
//    	Angle rotAngle = textParameters.getAngle();
//    	Double x = Util.format((double) textRenderingMatrix.getTranslateX(), nPlaces);
//    	Double y = createY(Util.format((double) textRenderingMatrix.getTranslateY(), nPlaces));
//    	// this is necessary for documents without explicit spaces.
//    	// if occasionally gives problems when there is a leading space (I think)
//    	addSpaceIfSpaceLargerThanRatio(x, y);
//    	currentXY = new Real2(x, y);
//    	float xDisplacement = displacementxy.getX();
//		currentDisplacement = new Real2(
//    			xDisplacement * scales.getX(), displacementxy.getY() * scales.getY()).format(nPlaces);
//    	boolean newText = true;
//    	if (currentSVGText == null || currentTextParameters == null) {
//    		createAndAddEmptyCurrentSVGText();
//    	} else if (!currentTextParameters.hasEqualFont(textParameters)) {
//    		createAndAddEmptyCurrentSVGText();
//    	} else if (isYChanged(y)) {
//    		createAndAddEmptyCurrentSVGText();
//    	} else if (isScaleChanged(scales)) {
//    		createAndAddEmptyCurrentSVGText();
//    	} else {
//    		newText = false;
//    	}
//    	if (unicode == null) {
//    		System.err.print("?");
//    		unicode = ILLEGAL_CHAR;
//    	}
//    	if (unicode.length() > 1) {
//    		LOG.trace("Unicode length > 1: "+unicode);
//    	}
//    	
//    	try {
//    		currentSVGText.appendText(unicode);
//    	} catch (Exception e) {
//    		LOG.error("Cannot add character: "+e);
//    		currentSVGText.appendText(ILLEGAL_CHAR);
//    	}
//    	currentSVGText.appendX(x);
//    	currentSVGText.setY(y);
//    	if (newText) {
//    		addCurrentTextAttributes(currentSVGText);
//    	}
//    	addRightTextLimitToCurrentText(x, v.getX());
//    	processNonZeroRotations(rotAngle);
//
////        // bbox in EM -> user units
//        Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
//        AffineTransform at = textRenderingMatrix.createAffineTransform();
//        bbox = at.createTransformedShape(bbox);
//                
//        saveUpdatedParameters(scales, x, y);
//    }

	/**
     * from PageDrawer
     *    static final float[] DEFAULT_SINGLE =
    {
        1,0,0,  //  a  b  0     sx hy 0    note: hx and hy are reversed vs. the PDF spec as we use
        0,1,0,  //  c  d  0  =  hx sy 0          AffineTransform's definition x and y shear
        0,0,1   //  tx ty 1     tx ty 1
    };
     */

	// ONE
    @Override
    protected void showFontGlyph(Matrix matrix, PDFont font, int code, String unicode,
                                 Vector displacement) throws IOException {
    	if (debugParams.showFontGlyph) System.out.println(">showFontGlyph");
    	super.showFontGlyph(matrix, font, code, unicode, displacement);
    	
        updateRenderingColorsStroke("showFontGlyph");
        
        Transform2 t2 = new Transform2(matrix.createAffineTransform());
        Angle angleOfRotation = t2.getAngleOfRotation();
        
    	if (debugParams.showChar) {
    		System.out.println("["+unicode+"|"+(int)code+"]");
    	}
    	registerFont(font);
    	
    	double x = Util.format(matrix.getTranslateX(), nxydec);
    	double y = Util.format(transformY(matrix.getTranslateY()), nxydec);
    	Real2 xy = new Real2(x, y).format(nxydec);
		
		SVGText text = new SVGText(xy, unicode == null ? AbstractPageParser.ILLEGAL_CHAR : unicode);
    	TextParameters textParameters = new TextParameters(matrix, font);
    	if (!textParameters.hasNormalOrientation()) {
    		text.rotateTextAboutPoint(xy, angleOfRotation.multiplyBy(-1.0), matdec);
    		RealSquareMatrix mat = new RealSquareMatrix(t2.extractSubMatrixData(0, 1, 0, 1));
    		double scalesq = mat.elementAt(0, 0) * mat.elementAt(1, 1) - mat.elementAt(0, 1) * mat.elementAt(1, 0);
    		double fontSize = Util.format(Math.sqrt(scalesq), ndec);
			text.setFontSize(fontSize);
    	} else {
        	text.setFontSize(Util.format((double)matrix.getScaleY(), ndec));
    	}
    	text.setStroke(textRenderingMode.isStroke() ? getJavaStrokeRGB() : "none");
    	text.setFill(textRenderingMode.isFill() ? getJavaFillRGB() : "none");
    	registerColor("fontGlyph", "fill", awtFillColor);
    	registerColor("fontGlyph", "stroke", awtStrokeColor);
    	text.setFontFamily(getName(font));
    	if (unicode == null) {
    		text.addAttribute(new Attribute(CODE, String.valueOf(code)));
    	}
    	text.setSVGXFontWidth(Util.format((double)displacement.getX(), ndec));
    	if (!Real.isZero(matrix.getShearX(), eps) || !Real.isZero(matrix.getShearY(), eps)) {
    		text.addAttribute(new Attribute(MATRIX, String.valueOf(matrix)));
    	}
    	currentTextPhrase.appendChild(text);
    }
    
	

	//==== TEXT ==========    

    // ONE+ZERO
    @Override
    public void beginText() throws IOException {
    	if (debugParams.showBeginText) System.out.println(">beginText");

    	// does not affect plot?
    	super.beginText();
    	currentTextPhrase = new SVGG();
    	currentTextPhrase.addAttribute(new Attribute("begin","text"));
    }

    @Override
    public void endText() throws IOException {
    	if (debugParams.showEndText) System.out.println(">endText");
    	super.endText();
    	
    	if (currentTextPhrase == null) {
    		throw new RuntimeException("textPhrase not opened");
    	}
    	svgg.appendChild(currentTextPhrase);
    	currentTextPhrase = null;
    }
    

    // ZERO
//	/**
//     * Filled path bounding boxes.
//     * getting the fill color is still a problem
//     */
//    @Override
//    public void fillPath(int windingRule) throws IOException {
//    	// NEVER call this here - it leaves iterators unset (I think this is a bug)
////    	super.fillPath(windingRule);
//    	GeneralPath generalPath = getLinePath();
//		currentSvgPath = new SVGPath(generalPath);
//		addCurrentPathAttributes(currentSvgPath);
//		// no justification for this - we need to sort out fill color
//		currentSvgPath.setFill(GraphicsElement.NONE);
//
//		svgg.appendChild(currentSvgPath);
//		
//    	
//        // draw path (note that getLinePath() is now reset)
//        super.fillPath(windingRule);
//        
//    }

    // ONE
    // ===== ANNOTATION =====
    /**
     * Custom annotation rendering.
     */
    @Override
    public void showAnnotation(PDAnnotation annotation) throws IOException {
    	if (debugParams.showAnnotation) {System.out.println(">annotation "+format(annotation));}
    	super.showAnnotation(annotation);
    }
    
    // ===== FORM =====    

   	/**
        * {@inheritDoc}
        */
       @Override
       public void showForm(PDFormXObject form) throws IOException {
       	if (debugParams.showForm) {System.out.println(">showForm "+format(form));}
       	super.showForm(form);
       }

    // ===== TRANSPARENCY =====    
       
       @Override
       public void showTransparencyGroup(PDTransparencyGroup form) throws IOException {
       	if (debugParams.showTransGrp) {System.out.println(">showTransparencyGroup "+form);}
           super.showTransparencyGroup(form);
       }

   // ===== MARKED CONTENT =====    

       /**
        * {@inheritDoc}
        */
       @Override
       public void beginMarkedContentSequence(COSName tag, COSDictionary properties) {
       	if (debugParams.showBeginMarked) {System.out.println(">beginMarkedContentSequence "+tag+"/"+properties);}
       	super.beginMarkedContentSequence(tag, properties);
       }

       /**
        * {@inheritDoc}
        */
       @Override
       public void endMarkedContentSequence() {
       	if (debugParams.showEndMarked) {System.out.println(">endMarkedContentSequence");}
       	super.endMarkedContentSequence();
       }
       
       // ===== Painting =====
       
       @Override
       public void shadingFill(COSName shadingName) throws IOException {
       	if (debugParams.showShadingFill) {System.out.println(">shadingFill "+shadingName);}
       	super.shadingFill(shadingName);
       }



    
//    @Override
//    public void moveTo(float x, float y) {
//    	super.moveTo(x, y);
//    	MovePrimitive mp = new MovePrimitive(new Real2(x, createY((double)y)));
////    	LOG.debug(" MOVE "+mp);
//    	if (currentPathPrimitiveList == null) {
//    		currentPathPrimitiveList = new PathPrimitiveList();
//    	}
//    	checkNotNullAndAdd(mp);
//    }
//
//    @Override
//    public void lineTo(float x, float y) {
//    	super.lineTo(x, y);
//    	LinePrimitive lp = new LinePrimitive(new Real2(x, createY((double)y)));
////    	LOG.debug(" LINE "+lp);
//    	checkNotNullAndAdd(lp);
//    }
//
//    @Override
//    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3)    {
//    	super.curveTo(x1, y1, x2, y2, x3, y3);
//    	String coordString = x1+","+createY((double)y1)+","+x2+","+createY((double)y2)+","+x3+","+createY((double)y3);
//    	Real2Array r2a = Real2Array.createFromPairs(coordString, ",");
//    	CubicPrimitive cp = new CubicPrimitive(r2a); 
////    	LOG.debug(" CURVE "+cp);
//    	checkNotNullAndAdd(cp);
//    }
//
//    @Override
//    public Point2D getCurrentPoint()    {
//    	currentPoint = super.getCurrentPoint();
//    	return currentPoint;
//    }
//
//    @Override
//    public void closePath()    {
//    	super.closePath();
//    	ClosePrimitive cp = new ClosePrimitive();
////    	System.out.print(" CLOSE ");
//    	checkNotNullAndAdd(cp);
//    }


//    @Override
//    public void endPath()    {
//    	super.endPath();
//    	if (currentPathPrimitiveList == null) {
//    		LOG.trace("WARN: cannot close path on non-existent primitiveList");
//    	}  else {
//    		currentSvgPath = new SVGPath(currentPathPrimitiveList);
//    		addCurrentPathAttributes(currentSvgPath);
//    		// fixes bug - probably shouldn't fill at all //BUG
//    		currentSvgPath.setFill(GraphicsElement.NONE);
//    		
//    		svgg.appendChild(currentSvgPath);
//        }
//    	currentPathPrimitiveList = null;
//		currentSvgPath = null;
//		
//    }


    // ONE + ZERO
    @Override
    public void drawImage(PDImage pdImage) throws IOException    {
    	super.drawImage(pdImage);
    	extractImage(pdImage);

    }

    // ONE + ZERO
    /**
     * Draws the page to the requested context.
     * 
     * @param g The graphics context to draw onto.
     * @param pageSize The size of the page to draw.
     * @throws IOException If there is an IO error while drawing the page.
     */
    @Override
    public void drawPage(Graphics g, PDRectangle pageSize) throws IOException
    {
    	super.drawPage(g, pageSize);
    	if (debugParams.showDrawPage) System.out.println(">drawPage");
    	mediaBox = new Real2Range(
    			new RealRange(pageSize.getLowerLeftX(), pageSize.getUpperRightX()),
    			new RealRange(pageSize.getLowerLeftY(), pageSize.getUpperRightY())
    	);
    	yMax = pageSize.getUpperRightY();
    	pageRotation = getPage().getRotation() % 360;
    	if (pageRotation != 0) {
    		LOG.warn("Rotated page: "+pageRotation);
    	}
    }



    // This doesn't mark the canvas so not used
    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
    	if (debugParams.showAppendRectangle) {
    		System.out.println(">appRect ["+format(p0, ndec)+"/"+format(p1, ndec)+"/"+format(p2, ndec)+"/"+format(p3, ndec)+"]");
    	}
    	super.appendRectangle(p0, p1, p2, p3);
    	
    }

// ===== PATHS ========
    
    /**  this does the actual stroking 
     */
    @Override
    public void strokePath() throws IOException {
    	if (debugParams.showStrokePath) System.out.println(">strokePath");
    	updateRenderingColorsStroke("strokePath");
    	super.strokePath();
    	registerColor("strokePath", "stroke", awtStrokeColor);
    	awtFillColor = null;
    	createPathAndFlush("strokePath");
    	
    }

	@Override
    public void fillPath(int windingRule) throws IOException {
    	if (debugParams.showFillPath) {System.out.println(">fillPath("+windingRule+")");}
    	updateRenderingColorsStroke("fillPath");
    	super.fillPath(windingRule);
		registerColor("fillPath", "fill", awtFillColor);
		awtStrokeColor = null;
    	createPathAndFlush("fillPath");
    }

    /**
     * Fills and then strokes the path.
     *
     * @param windingRule The winding rule this path will use.
     * @throws IOException If there is an IO error while filling the path.
     */
    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
    	if (debugParams.showFillAndStrokePath) {System.out.println(">fillAndStrokePath("+windingRule+")");}
    	super.fillAndStrokePath(windingRule);
    	updateRenderingColorsStroke("fillAndStrokePath");
    	super.fillPath(windingRule);
    	super.strokePath();
    	createPathAndFlush("fillAndStroke");
    	LOG.debug("fillAndStrokePath");
    }

    @Override
    public void clip(int windingRule) {
    	if (debugParams.showClip) {System.out.println("clip("+windingRule+")");}
    	super.clip(windingRule);
    }

    @Override
    public void moveTo(float x, float y) {
    	if (debugParams.showMoveTo) {System.out.println("M"+format(x, y, ndec));}
    	super.moveTo(x, y);
    	Real2 point = new Real2(x, transformY(y));
		MovePrimitive movePrimitive = new MovePrimitive(point.format(ndec));
    	if (checkViewBoxAndMaxPrimitives(movePrimitive)) {
			ensurePathPrimitiveList().add(movePrimitive);
    	}
    }

	@Override
    public void lineTo(float x, float y) {
    	if (debugParams.showLineTo) {System.out.println("L"+format(x, y, ndec));}
    	super.lineTo(x, y);
    	Real2 point = new Real2(x, transformY(y));
		LinePrimitive linePrimitive = new LinePrimitive(point.format(ndec));
    	if (checkViewBoxAndMaxPrimitives(linePrimitive)) {
			ensurePathPrimitiveList().add(linePrimitive);
    	}
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
    	if (debugParams.showCurveTo) {System.out.println("C"+format(x1, y1, x2, y2, x3, y3, 2));}
    	super.curveTo(x1, y1, x2, y2, x3, y3);
    	Real2Array xyArray = new Real2Array(Arrays.asList(new Real2[] {
    			new Real2(x1, transformY(y1)).format(ndec),
    			new Real2(x2, transformY(y2)).format(ndec),
    			new Real2(x3, transformY(y3)).format(ndec)
    		}
    		));
    	CubicPrimitive cubicPrimitive = new CubicPrimitive(xyArray);
    	if (checkViewBoxAndMaxPrimitives(cubicPrimitive)) {
			ensurePathPrimitiveList().add(cubicPrimitive);
    	}
    }

	@Override
    public Point2D getCurrentPoint() {
		currentPoint = super.getCurrentPoint();
//    	if (debugParams.showCurrentPoint) {System.out.println("CURPT"+format(currentPoint, ndec));}
    	return currentPoint;
    }

	@Override
    public void closePath() {
    	if (debugParams.showClosePath) {System.out.println(">closePath");}
    	super.closePath();    	
    	ensurePathPrimitiveList().add(new ClosePrimitive());
    }

    @Override
    public void endPath() {
    	if (debugParams.showEndPath) {System.out.println(">endPath");}
    	super.endPath();
    }

    // ============ UTILITIES ============

//	private void checkNotNullAndAdd(SVGPathPrimitive p) {
//	if (currentPathPrimitiveList == null) {
//		LOG.warn("cannot close path on non-existent primitiveList");
//	} else {
//		checkViewBoxAndMaxPrimitives(p);
//	}
//}

	private boolean checkViewBoxAndMaxPrimitives(SVGPathPrimitive p) {
		Real2Range bb = p.getBoundingBox();
		return checkViewBoxAndMaxPrimitives(p, bb);
	}

	private boolean checkViewBoxAndMaxPrimitives(SVGPathPrimitive p, Real2Range bb) {
		if (!viewBox.includes(bb) ) {
			if (debugParams.checkViewBox) {
				System.err.println(p+" is not inside "+viewBox);
			}
			return false;
		}
		if (documentProcessor != null && ensurePathPrimitiveList().size() > documentProcessor.getMaxPrimitives()) {
			if (debugParams.maxPrimitives) {
				System.err.println("primitiveList exceeded: "+pathPrimitiveList.size());
			}
			return false;
		}
		return true;
	}


// ZERO
    
//	private void addCurrentPathAttributes(SVGElement element) {
//		element.setFill(getAMIFill());
//		element.setStroke(getAMIStroke());
//		element.setStrokeWidth(getAMIStrokeWidth());
//		setDashArray(element);
//	}
//	
//	private void addCurrentTextAttributes(SVGText text) {
//		setFillAndStrokeFromGraphics2D(text);
//		Real2 scales = textParameters.getScales();
//		// at present I think only the PDFont matters
//		PDTextState textState = getGraphicsState().getTextState();
//		PDFont font = textState.getFont();
//		PDFontDescriptor fontDescriptor = font.getFontDescriptor();
//
//		/** not sure how this works
//		 * sometimes fontSize2 is 1.0, and we need scales
//		 */
//		setFontSize(text, scales, textState);
//
//		String fontName = font.getName();
//		fontName = removeArbitraryPrefix(fontName);
//		text.setFontFamily(fontName);
//		
//		// these *might* be useful
//		try {
//			font.getDisplacement(codepoint);
//			font.getBoundingBox();
////			font.getPositionVector(code); // pnly vertical
//		} catch (Exception e) {throw new RuntimeException(e);}
//		
//		// these *might* be useful
//		if (fontDescriptor != null) {
//			fontDescriptor.getItalicAngle();
//			fontDescriptor.isAllCap();
//			fontDescriptor.isForceBold();
//			fontDescriptor.isItalic();
//			fontDescriptor.isSmallCap();
//		}
//	}
//
//	/** get fill and stroke from graphics2D.
//	 * if null defaults to fill = black and stroke = none;
//	 * @param text
//	 */
//	private void setFillAndStrokeFromGraphics2D(SVGText text) {
//		PDGraphicsState graphicsState = getGraphicsState();
//		String fillColor = translatePDColorToRGBCSSString(graphicsState.getNonStrokingColor());
//		text.setFill(fillColor);
//		setStrokeIfFillIsNone(text, graphicsState, fillColor);
//		
//	}
//
//	private void setStrokeIfFillIsNone(SVGText text, PDGraphicsState graphicsState, String fillColor) {
//		if (fillColor == null || fillColor.equals("none")) {
//			String strokeColor = translatePDColorToRGBCSSString(graphicsState.getStrokingColor());
//			Double width = getAMIStrokeWidth();
//			text.setStroke(strokeColor);
//			text.setStrokeWidth(width);
//		}
//	}
//
//	private void setFontSize(SVGText text, Real2 scales, PDTextState textState) {
//		Double fontSize = new Double(scales.getY());
//		float fontSize2 = textState.getFontSize();
//		if (fontSize2 > 1.01) {
//			fontSize = Double.valueOf(fontSize2);
//		}
//		text.setFontSize(Util.format(fontSize, fontPrecision));
//	}
//
//	private String removeArbitraryPrefix(String fontName) {
//		if (fontName != null) {
//			fontName = fontName.replace("^[A-Z]*\\+", "");
//		}
//		return fontName;
//	}

 // ZERO
    
// 	private double createY(double y) {
// 		return yMax - y;
// 	}
 //
// 	private boolean isScaleChanged(Real2 scales) {
// 		if (currentScales == null || scales == null) {
// 			return true;
// 		}
// 		return (!currentScales.isEqualTo(scales, scalesEpsilon));
// 	}
 //
// 	private boolean isYChanged(Double y) {
// 		if (currentY == null || y == null) {
// 			return true;
// 		}
// 		double delta = Math.abs(currentY - y);
// 		return delta > yEpsilon;
// 	}

// 	private void createAndAddEmptyCurrentSVGText() {
// 		/* from an email
// 		 * I have tried the DrawPrintTextLocations.java
 //> example on 3 PDFs, and I see that the best way to get the display font 
 //> size for letters in any PDF is by using “text.getXScale()”. Am I right? ... Yes
 //
 //PMR have not yet looked into this.
// 		 */
// 		
// 		drawRHMargin();
// 		currentSVGText = new SVGText();
// 		currentSVGText.setFontFamily(textParameters.getFontFamily());
// 		currentSVGText.setFontStyle(
// 				(textParameters.isItalic() ? FontStyle.ITALIC.toString() : FontStyle.NORMAL.toString()));
// 		currentSVGText.setFontWeight(textParameters.isForceBold() ? FontWeight.BOLD : FontWeight.NORMAL);
// 		if (!currentSVGText.isBold()) {
// 			currentSVGText.addBoldFromFontWeight(textParameters.getFontWeight(), minBoldWeight);
// 		}
// 		
// 		Real2 scales = textParameters.getScales().format(3);
// 		double yScale = scales.getY();
// 		
// 		currentSVGText.setFontSize(yScale);
// 		RealArray xArray = new RealArray();
// 		currentSVGText.setX(xArray);
// 		addCurrentTextAttributes(currentSVGText);
// 		svgg.appendChild(currentSVGText);
// 	}
 //
// 	private void drawRHMargin() {
// 		if (currentSVGText != null) {
// 			Double yLast = currentSVGText.getY();
// 			if (yLast == null) {
// 				LOG.trace("curr: "+currentSVGText.toXML().toString());
// 			}
// 			Double xLast = Double.valueOf(currentSVGText.getAttributeValue(SVG_RHMARGIN));
// 			if (yLast != null && xLast != null) {
//// 				drawMargin(yLast, xLast, "red");
// 				RealArray xArray = currentSVGText.getXArray();
//// 				drawMargin(yLast, xArray.getLast(), "blue");
// 			}
// 		}
// 	}
 //
// 	private void drawMargin(Double yLast, Double xLast, String stroke) {
// 		Real2 l0 = new Real2(xLast, yLast);
// 		Real2 l1 = new Real2(xLast, yLast - currentSVGText.getCurrentFontSize());
// 		SVGLine line = new SVGLine(l0, l1);
// 		line.setWidth(0.5);
// 		line.setStroke(stroke);
// 		svgg.appendChild(line);
// 	}
    
	// ZERO
//  /** create rightmost extent of text.
//   * add characterWidth (displacement) * fontSize to X-coord
//   * This is invisible "right margin" of text
//   * @param x
//   */
//	private void addRightTextLimitToCurrentText(double currentX, float displacement) {
//		double xLimit = Util.format(currentX + textParameters.getFontSize() * displacement, nPlaces);
//		currentSVGText.addAttribute(new Attribute(SVG_RHMARGIN, String.valueOf(xLimit)));
//	}
//
//	private void saveUpdatedParameters(Real2 scales, Double x, Double y) {
//		currentX = x;
//      currentY = y;
//      currentTextParameters = textParameters;
//		currentScales = scales;
//	}
//
//	private void processNonZeroRotations(Angle rotAngle) {
//		if (rotAngle != null && !rotAngle. isEqualTo(0.0, angleEps)) {
//  		Transform2 t2 = Transform2.getRotationAboutPoint(rotAngle, currentXY);
//  		currentSVGText.applyTransform(t2, RotateText.FALSE);
//  		currentSVGText.format(nPlaces);
//  	}
//	}
//
//	/** use when PDF has no explicit spaces. Messy */
//	private void addSpaceIfSpaceLargerThanRatio(Double x, Double y) {
//		Double deltaX = (currentX == null || x == null) ? null : Util.format(x - currentX, 2);
//  	Double spaceOffsetRatio = (deltaX == null || currentDisplacement == null) ? null : deltaX / currentDisplacement.getX();
//  	if (spaceOffsetRatio != null && spaceOffsetRatio > maxSpaceRatio ) {
//      	currentSVGText.appendText(" ");
//      	currentSVGText.appendX(currentX + currentDisplacement.getX());
//      	currentSVGText.setY(y);
//  	}
//	}

	private String getAMIFill() {
		PDColor nonStrokingColor = getGraphicsState().getNonStrokingColor();
		String rgb = translatePDColorToRGBCSSString(nonStrokingColor);
		Paint paintP = getGraphics().getPaint();
		if (paintP instanceof Color) {
			Color paint = (Color) paintP;
			rgb = getCSSColor(paint);
		} else if (paintP instanceof AxialShadingPaint) {
			AxialShadingPaint axp = (AxialShadingPaint) paintP;
			LOG.trace(axp);
		}
		return rgb;
	}

	private String getAMIStroke() {
		PDColor strokingColor = getGraphicsState().getStrokingColor();
        Paint paint = getGraphics().getPaint();
		String rgb = translatePDColorToRGBCSSString(strokingColor);
		return rgb;
	}

	private Double getAMIStrokeWidth() {
		float width = getGraphicsState().getLineWidth();
		return new Double(width);
	}

	private String translatePDColorToRGBCSSString(PDColor color) {
		String col = null;
		if (color != null) {
			try {
				col = String.format("#%06x", color.toRGB());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnsupportedOperationException uoe) {
				LOG.trace("UNSUPPORTED OPERATION");
//				uoe.printStackTrace();
			}
		}
		return col == null ? SVGText.NONE : col;
	}

	private String getAndFormatClipPath() {
		Shape shape = getGraphicsState().getCurrentClippingPath();
		SVGPath path = new SVGPath(shape);
		path.format(nPlaces);
		clipString = path.getDString();
		// old approach
		ensureClipStringSet();
		clipStringSet.add(clipString);
		// new approach
		ensureIntegerByClipStringMap();
		if (!integerByClipStringMap.containsKey(clipString)) {
			integerByClipStringMap.put(clipString, integerByClipStringMap.size()+1); // count from 1
		}
		return clipString;
	}
    
	private void setDashArray(SVGElement svgElement) {
		@SuppressWarnings("unchecked")
		PDLineDashPattern pattern = getGraphicsState().getLineDashPattern();
		if (pattern != null) {
			float[] fDashArray =  pattern.getDashArray();
			StringBuilder sb = new StringBuilder("");
			if (fDashArray != null && fDashArray.length > 0) {
				for (int i = 0; i < fDashArray.length; i++) {
					if (i > 0) {
						sb.append(" ");
					}
					double d = fDashArray[i]; 
					sb.append(Util.format(d, nPlaces));
				}
				svgElement.setStrokeDashArray(sb.toString());
			}
		}
	}
	
	private void setClipPath(SVGElement svgElement, String clipString, Integer clipPathNumber) {
		String urlString = "url(#clipPath"+clipPathNumber+")";
		svgElement.setClipPath(urlString);
	}
	
	private void createDefsForClipPaths() {
	//   <clipPath clipPathUnits="userSpaceOnUse" id="clipPath14">
//		    <path stroke="black" stroke-width="0.5" fill="none" d="M0 0 L89.814 0 L89.814 113.7113 L0 113.7113 L0 0 Z"/>
//		  </clipPath>
		ensureIntegerByClipStringMap();
		ensureDefs1();
		for (String pathString : integerByClipStringMap.keySet()) {
			Integer serial = integerByClipStringMap.get(pathString);
			SVGClipPath clipPath = new SVGClipPath();
			clipPath.setId(CLIP_PATH+serial);
			defs1.appendChild(clipPath);
			SVGPath path = new SVGPath();
			path.setDString(pathString);
			clipPath.appendChild(path);
		}
	}
	
	private void ensureIntegerByClipStringMap() {
		if (integerByClipStringMap == null) {
			integerByClipStringMap = new HashMap<String, Integer>();
		}
	}

	private void ensureClipStringSet() {
		if (clipStringSet == null) {
			clipStringSet = new HashSet<String>();
		}
	}

	/** translates java color to CSS RGB
	 * 
	 * @param paint
	 * @return CCC as #rrggbb (alpha is currently discarded)
	 */
	private static String getCSSColor(Paint paint) {
		String colorS = null;
		if (paint instanceof Color) {
			int r = ((Color) paint).getRed();
			int g = ((Color) paint).getGreen();
			int b = ((Color) paint).getBlue();
			// int a = ((Color) paint).getAlpha();
			int rgb = (r<<16)+(g<<8)+b;
			colorS = String.format("#%06x", rgb);
			if (rgb != 0) {
//				LOG.trace("Paint "+rgb+" "+colorS);
			}
		}
		return colorS;
	}

	private void ensureDefs1() {
/*
<svg fill-opacity="1" 
xmlns="http://www.w3.org/2000/svg">
  <defs id="defs1">
   <clipPath clipPathUnits="userSpaceOnUse" id="clipPath1">
    <path stroke="black" stroke-width="0.5" fill="none" d="M0 0 L595 0 L595 793 L0 793 L0 0 Z"/>
   </clipPath>
   </defs>
 */
		List<SVGElement> defList = SVGUtil.getQuerySVGElements(svgg, "/svg:g/svg:defs[@id='defs1']");
		defs1 = (defList.size() > 0) ? defList.get(0) : null;
		if (defs1 == null) {
			defs1 = new SVGDefs();
			defs1.setId("defs1");
			svgg.insertChild(defs1, 0);
		}
	}

	public Map<String, BufferedImage> getOrCreateRawImageMap() {
		if (imageByTitle == null) {
			imageByTitle = new HashMap<String, BufferedImage>();
		}
		return imageByTitle;
	}

	/** sets the serial number of the page.
	 * normally the serial of the pages as iterated through the PDDocument.
	 * 
	 * @param iPage
	 */
	public void setPageSerial(PageSerial pageSerial) {
		if (pageSerial == null) {
			throw new RuntimeException("null pageSerial");
		}
		this.pageSerial = pageSerial;
	}

	public void setRenderedImage(BufferedImage renderImage) {
		this.renderedImage = renderImage;
	}
	
	public BufferedImage getRenderedImage() {
		return renderedImage;
	}

	public Real2Range getViewBox() {
		return viewBox;
	}

	public void setViewBox(Real2Range viewBox) {
		this.viewBox = viewBox;
	}

	public void setDocumentProcessor(PDFDocumentProcessor documentProcessor) {
		this.documentProcessor = documentProcessor;
	}

	public Set<PDFont> getFontSet() {throw new UnsupportedException("no fontSet");}
	public Set<Stroke> getStrokeSet() {throw new UnsupportedException("no strokeSet");}
	public Set<Color> getColorSet()  {throw new UnsupportedException("no ColorSet");}


	
}
