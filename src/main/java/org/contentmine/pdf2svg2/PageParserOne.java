package org.contentmine.pdf2svg2;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealSquareMatrix;
import org.contentmine.eucl.euclid.Transform2;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.GraphicsElement.FontStyle;
import org.contentmine.graphics.svg.GraphicsElement.FontWeight;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.path.ClosePrimitive;
import org.contentmine.graphics.svg.path.CubicPrimitive;
import org.contentmine.graphics.svg.path.LinePrimitive;
import org.contentmine.graphics.svg.path.MovePrimitive;

import nu.xom.Attribute;

/**
 * Example PageDrawer subclass with custom rendering.
 * John Hewson
 * modified by pm286
 * 
 */
public class PageParserOne extends AbstractPageParser {
	static final Logger LOG = Logger.getLogger(PageParserOne.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String WIDTH = "width";
	PageParserOne(PageDrawerParameters parameters, AMIDebugParameters debugParams) throws IOException {
        super(parameters, debugParams);
        init();
    }
    
    void init() {
    	super.init();
	}

	public void setDebugParameters(AMIDebugParameters params) {
    	this.debugParams = params;
    }
    
    

    // ========= BEGIN OVERIDE ==================
    
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
    	if (debugParams.showDrawPage) System.out.println(">drawPage");
    	LOG.debug("PS "+this.getPageSerial());
    	super.drawPage(g, pageSize);
    }


//==== TEXT ==========    

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
    
	/**
     * from PageDrawer
     *    static final float[] DEFAULT_SINGLE =
    {
        1,0,0,  //  a  b  0     sx hy 0    note: hx and hy are reversed vs. the PDF spec as we use
        0,1,0,  //  c  d  0  =  hx sy 0          AffineTransform's definition x and y shear
        0,0,1   //  tx ty 1     tx ty 1
    };
     */

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

// ===== APPEND RECTANGLE =========

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
    	ensurePathPrimitiveList().add(new MovePrimitive(new Real2(x, transformY(y)).format(ndec)));
    }

	@Override
    public void lineTo(float x, float y) {
    	if (debugParams.showLineTo) {System.out.println("L"+format(x, y, ndec));}
    	super.lineTo(x, y);
    	ensurePathPrimitiveList().add(new LinePrimitive(new Real2(x, transformY(y)).format(ndec)));
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
    	ensurePathPrimitiveList().add(new CubicPrimitive(xyArray));
    }

//	@Override
//    public Point2D getCurrentPoint() {
//		Point2D currentPoint = super.getCurrentPoint();
////    	if (debugParams.showCurrentPoint) {System.out.println("CURPT"+format(currentPoint, ndec));}
//    	return currentPoint;
//    }

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

// ===== IMAGE ======
    @Override
    public void drawImage(PDImage pdImage) throws IOException {
    	if (debugParams.showDrawImage) {System.out.println(">drawImage "+pdImage);}
    	super.drawImage(pdImage);
    	LOG.debug("page serial: "+pageSerial);
//    	super.setPageSerial(pageSerial);
    	extractImage(pdImage);
    }

 // ===== Painting =====
    
    @Override
    public void shadingFill(COSName shadingName) throws IOException {
    	if (debugParams.showShadingFill) {System.out.println(">shadingFill "+shadingName);}
    	super.shadingFill(shadingName);
    }

// ===== ANNOTATION =====    
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
    
    // ========= END OVERIDE ==================

    /** not used, but may need this to set font weights , etc.
     * 
     * @param font
     */
    
	private SVGText createNewText(TextParameters textParameters, double minBoldWeight) {
		
		SVGText newText = new SVGText();
		newText.setFontFamily(textParameters.getFontFamily());
		newText.setFontStyle(
				(textParameters.isItalic() ? FontStyle.ITALIC.toString() : FontStyle.NORMAL.toString()));
		newText.setFontWeight(textParameters.isForceBold() ? FontWeight.BOLD : FontWeight.NORMAL);
		if (!newText.isBold()) {
			newText.addBoldFromFontWeight(textParameters.getFontWeight(), minBoldWeight);
		}
		
		Real2 scales = textParameters.getScales().format(3);
		double yScale = scales.getY();
		
		newText.setFontSize(yScale);
		RealArray xArray = new RealArray();
		newText.setX(xArray);
//		addCurrentTextAttributes(newText);
		return newText;
	}





}

