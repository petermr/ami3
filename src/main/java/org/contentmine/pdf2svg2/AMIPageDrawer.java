package org.contentmine.pdf2svg2;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.state.PDGraphicsState;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.GraphicsElement.FontStyle;
import org.contentmine.graphics.svg.GraphicsElement.FontWeight;
import org.contentmine.graphics.svg.path.ClosePrimitive;
import org.contentmine.graphics.svg.path.CubicPrimitive;
import org.contentmine.graphics.svg.path.LinePrimitive;
import org.contentmine.graphics.svg.path.MovePrimitive;
import org.contentmine.graphics.svg.path.PathPrimitiveList;

import nu.xom.Attribute;

/**
 * Example PageDrawer subclass with custom rendering.
 * John Hewson
 * modified by pm286
 * 
 */
public class AMIPageDrawer extends PageDrawer {
	private static final Logger LOG = Logger.getLogger(AMIPageDrawer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String CODE = "code";
	private static final String UNICODE = "unicode";
	private static final String WIDTH = "width";
	private static final String MATRIX = "matrix";
	
    private AMIDebugParameters debugParams;
	private FontGlyph currentFontGlyph;
	private Color javaStrokeColor;
	private Color javaFillColor;
	private Stroke currentStroke;
	private Set<PDFont> fontSet = new HashSet<>();
	private Set<Stroke> strokeSet = new HashSet<>();
	private Set<Color> colorSet = new HashSet<>();
	private SVGG gTop;
	private SVGG currentTextPhrase;
	private Double currentLineWidth;
	private Real2 currentPoint;
	private PDPage currentPage;
	private Graphics2D lastGraphics;
	private GeneralPath lastPath;
	private int ndec = 2;
	private PathPrimitiveList pathPrimitiveList;
	private Composite currentComposite;
	private PDRectangle pageSize;
	private double EPS = 0.000000001;
	private Map<String, PDFont> pdFontByName;
	private RenderingMode renderingMode;

	AMIPageDrawer(PageDrawerParameters parameters, AMIDebugParameters debugParams) throws IOException {
        super(parameters);
        init();
        this.debugParams = debugParams;
    }
    
    private void init() {
    	fontSet = new HashSet<>();
    	strokeSet = new HashSet<>();
    	colorSet = new HashSet<>();
    	gTop = new SVGG();
	}

	public void setDebugParameters(AMIDebugParameters params) {
    	this.debugParams = params;
    }
    
    public AMIDebugParameters getDebugParameters() {
    	return debugParams;
    }

    /**
     * Draws the page to the requested context.
     * 
     * @param g The graphics context to draw onto.
     * @param pageSize The size of the page to draw.
     * @throws IOException If there is an IO error while drawing the page.
     */
    public void drawPage(Graphics g, PDRectangle pageSize) throws IOException
    {
    	if (debugParams.showDrawPage) System.out.println(">drawPage");

        this.pageSize = pageSize;
    	super.drawPage(g, pageSize);
    	unsetFillStroke();
    }


    /**
     * Color replacement.
     * creates/extracts Color from PDColor but does not set in graphics2D
     * only used in:
     *     // returns the stroking AWT Paint
    private Paint getStrokingPaint() throws IOException
    {
        return applySoftMaskToPaint(
                getPaint(getGraphicsState().getStrokingColor()),
                getGraphicsState().getSoftMask());
    }
    which is called from 
    -drawGlyph2D(Glyph2D, PDFont, int, Vector, AffineTransform) private to PageDrawer
	- strokePath()

    // returns the non-stroking AWT Paint
    private Paint getNonStrokingPaint() throws IOException
    {
        return applySoftMaskToPaint(
                getPaint(getGraphicsState().getNonStrokingColor()),
                getGraphicsState().getSoftMask());
    }
    which is called from
    - drawGlyph2D(Glyph2D, PDFont, int, Vector, AffineTransform) private to PageDrawer
	- drawImage(PDImage) (2 matches)
	- fillPath(int)
    
    
     */
    /** don't actually think this is used publicly in Demo
     * i.e. it is used in private calls from PageDrawer, but not outside
     * 
     */
    @Override
    protected Paint getPaint(PDColor pdColor) throws IOException {
//    	if (debugParams.showPaint) System.out.println(">paint");
        Paint paint = super.getPaint(pdColor);
//    	Color javaColor = null;
//        PDColorSpace colorSpace = pdColor.getColorSpace();
//        if (!(colorSpace instanceof PDPattern)) {
//            float[] rgb = colorSpace.toRGB(pdColor.getComponents());
//            javaColor = new Color(clampColor(rgb[0]), clampColor(rgb[1]), clampColor(rgb[2]));
////            if (!javaColor.equals(javaColor)) {
////            	if (debugParams.showPaint) System.out.println("COL change "+toRGB(javaColor) +" ==> "+toRGB(javaColor));
//////            	change = true;
////            }
//        } else {
//        	throw new RuntimeException("PDPattern not supported");
//        }

    	
        return paint;
    }


    @Override
    public void beginText() throws IOException {
    	if (debugParams.showBeginText) System.out.println(">beginText");

    	// does not affect plot?
    	super.beginText();
    	currentTextPhrase = new SVGG();
    	currentTextPhrase.addAttribute(new Attribute("begin","text"));
//        setClip();
//        beginTextClip();
    }

    @Override
    public void endText() throws IOException {
    	if (debugParams.showEndText) System.out.println(">endText");
    	if (debugParams.debugGraphics) debugGraphics("endText");
    	super.endText();
    	
    	if (currentTextPhrase == null) {
    		throw new RuntimeException("textPhrase not opened");
    	}
    	gTop.appendChild(currentTextPhrase);
    	currentTextPhrase = null;
    	unsetFillStroke();
//        endTextClip();
    }
    
    private void unsetFillStroke() {
    	javaFillColor = null;
    	javaStrokeColor = null;
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

    	if (debugParams.showChar) {
    		System.out.print("["+unicode+"|"+(int)code+"]");
    	}
    	registerFont(font);
    	
    	double x = matrix.getTranslateX();
    	double y = transformY(matrix.getTranslateY());
    	SVGText text = new SVGText(new Real2(x, y).format(2), unicode == null ? PageParser.ILLEGAL_CHAR : unicode);
    	TextParameters textParameters = new TextParameters(matrix, font);
    	if (!textParameters.hasNormalOrientation()) {
    		// NOT RIGHT YET - WORK THIS OUT
//    		text.setTransform(textParameters.getTransform2());
    	}
    	text.setStroke(renderingMode.isStroke() ? getJavaStrokeRGB() : "none");
    	text.setFill(renderingMode.isFill() ? getJavaFillRGB() : "none");
    	registerColor(javaFillColor);
    	registerColor(javaStrokeColor);
    	text.setFontSize(Util.format((double)matrix.getScaleY(), ndec));
    	text.setFontFamily(getName(font));
    	if (unicode == null) {
    		text.addAttribute(new Attribute(CODE, String.valueOf(code)));
    	}
    	text.setSVGXFontWidth(Util.format((double)displacement.getX(), ndec));
    	if (!Real.isZero(matrix.getShearX(), EPS) || !Real.isZero(matrix.getShearY(), EPS )) {
    		text.addAttribute(new Attribute(MATRIX, String.valueOf(matrix)));
    	}
    	currentTextPhrase.appendChild(text);
    	if (debugParams.debugGraphics) debugGraphics("showFontGlyph");
    }



    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
//    	createPathAndFlush();
    	if (debugParams.showAppendRectangle) {
    		System.out.println(">appRect ["+format(p0, ndec)+"/"+format(p1, ndec)+"/"+format(p2, ndec)+"/"+format(p3, ndec)+"]");
    	}
    	super.appendRectangle(p0, p1, p2, p3);

    }

    /**  this does the actual stroking 
     */
    @Override
    public void strokePath() throws IOException {
    	if (debugParams.showStrokePath) System.out.println(">strokePath");
    	updateRenderingColorsStroke("strokePath");
    	super.strokePath();
    	javaFillColor = null;
    	registerColor(javaStrokeColor);
    	createPathAndFlush("strokePath");
    	
    }

	@Override
    public void fillPath(int windingRule) throws IOException {
    	if (debugParams.showFillPath) {System.out.println(">fillPath("+windingRule+")");}
    	updateRenderingColorsStroke("fillPath");
    	super.fillPath(windingRule);
//    	updateRenderingColorsStroke();
//    	javaStrokeColor = null;
		registerColor(javaFillColor);
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
    	super.fillAndStrokePath(windingRule);
    	if (debugParams.showFillAndStrokePath) {
    		System.out.println(">fillAndStrokePath("+windingRule+")");
    	}
    	createPathAndFlush("fillAndStroke");
    	throw new RuntimeException("fillAndStrokePath NYI");
    }

    @Override
    public void clip(int windingRule) {
    	super.clip(windingRule);
    	if (debugParams.showClip) {
    		System.out.println("clip("+windingRule+")");
    	}
//    	createPathAndFlush();
    }

    @Override
    public void moveTo(float x, float y) {
    	if (debugParams.showMoveTo) {
    		System.out.println("M"+format(x, y, ndec));
    	}
    	super.moveTo(x, y);
    	ensurePathPrimitiveList().add(new MovePrimitive(new Real2(x, transformY(y)).format(ndec)));
    	currentPoint = new Real2(x, transformY(y));
    }

	@Override
    public void lineTo(float x, float y) {
    	if (debugParams.showLineTo) {
    		System.out.println("L"+format(x, y, ndec));
    	}
    	super.lineTo(x, y);
    	ensurePathPrimitiveList().add(new LinePrimitive(new Real2(x, transformY(y)).format(ndec)));
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
    	if (debugParams.showCurveTo) {
    		System.out.println("C"+format(x1, y1, x2, y2, x3, y3, 2));
    	}
    	super.curveTo(x1, y1, x2, y2, x3, y3);
    	Real2Array xyArray = new Real2Array(Arrays.asList(new Real2[] {
    			new Real2(x1, transformY(y1)).format(ndec),
    			new Real2(x2, transformY(y2)).format(ndec),
    			new Real2(x3, transformY(y3)).format(ndec)
    		}
    		));
    	ensurePathPrimitiveList().add(new CubicPrimitive(xyArray));
    }

	@Override
    public Point2D getCurrentPoint() {
		Point2D currentPoint = super.getCurrentPoint();
    	if (debugParams.showCurrentPoint) {
//			System.out.println("CURPT"+format(currentPoint, ndec));
    	}
    	return currentPoint;
    }

	@Override
    public void closePath() {
    	if (debugParams.showClosePath) {
    		System.out.println(">closePath");
    	}
    	super.closePath();    	
    	ensurePathPrimitiveList().add(new ClosePrimitive());
    }

    @Override
    public void endPath() {
    	if (debugParams.showEndPath) {
    		System.out.println(">endPath");
    	}
    	super.endPath();
//    	createPathAndFlush();
    }

	private void createPathAndFlush(String source) {
		if (pathPrimitiveList != null) {
			LOG.debug(source + ": flush");
	    	SVGPath path = new SVGPath(pathPrimitiveList);
	    	updateRenderingColorsStroke("createPathAndFlush");
	    	String javaStrokeRGB = getJavaStrokeRGB();
			path.setStroke(javaStrokeRGB);
	    	String javaFillRGB = getJavaFillRGB();
			path.setFill(javaFillRGB);
	    	path.setStrokeWidth(currentLineWidth);
	    	path.format(3);
	    	LOG.debug("createPathFlush: "+path);
	    	gTop.appendChild(path);
	    	pathPrimitiveList = null;
	    	unsetFillStroke();
		} else {
			System.out.println("createPathAndFlush: no primitives");
		}
	}
    
    @Override
    public void drawImage(PDImage pdImage) throws IOException {
    	if (debugParams.showDrawImage) {
    		System.out.println(">drawImage "+pdImage);
    	}
    	super.drawImage(pdImage);

    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException {
    	if (debugParams.showShadingFill) {
    		System.out.println(">shadingFill "+shadingName);
    	}
    	super.shadingFill(shadingName);
    }

    @Override
    public void showAnnotation(PDAnnotation annotation) throws IOException {
    	if (debugParams.showAnnotation) {
    		System.out.println(">annotation "+
    	    format(annotation));
    	}
    	super.showAnnotation(annotation);
    }


	/**
     * {@inheritDoc}
     */
    @Override
    public void showForm(PDFormXObject form) throws IOException {
    	if (debugParams.showForm) {
    		System.out.println(">showForm "+format(form));
    	}
    	super.showForm(form);
    }

    @Override
    public void showTransparencyGroup(PDTransparencyGroup form) throws IOException {
    	if (debugParams.showTransGrp) {
    		System.out.println(">showTransparencyGroup "+form);
    	}
        super.showTransparencyGroup(form);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void beginMarkedContentSequence(COSName tag, COSDictionary properties) {
    	if (debugParams.showBeginMarked) {
    		System.out.println(">beginMarkedContentSequence "+tag+"/"+properties);
    	}
    	super.beginMarkedContentSequence(tag, properties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endMarkedContentSequence() {
    	if (debugParams.showEndMarked) {
    		System.out.println(">endMarkedContentSequence");
    	}
    	super.endMarkedContentSequence();
    }
    
    // ===========================
    
	private void updateRenderingColorsStroke(String source) {
		renderingMode = getGraphicsState().getTextState().getRenderingMode();
		try {
	        javaStrokeColor = (Color) getPaint(getGraphicsState().getStrokingColor());
	        javaFillColor = (Color) getPaint(getGraphicsState().getNonStrokingColor());
		} catch (IOException e) {
			throw new RuntimeException("cannot extract colors", e);
		}
        System.out.println("PDGraphics " + source + ": AWTstroke: "+javaStrokeColor);
        System.out.println("PDGraphics " + source + ": AWTfill: "+javaFillColor);
        updateCurrentStroke();
	}



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



	private void registerFont(PDFont font) {
		getOrCreatePDFontByName();
		String name = getName(font);
		if (!pdFontByName.containsKey(name)) {
			pdFontByName.put(name, font);
			LOG.debug("font: "+pdFontByName);
		}
	}

	private Map<String, PDFont> getOrCreatePDFontByName() {
		if (pdFontByName == null) {
			pdFontByName = new HashMap<>();
		}
		return pdFontByName;
	}

	private String getName(PDFont font) {
		String name = font == null ? null : font.getName();
		if (name != null && name.contains("+")) {
			name = name.split("\\+")[1];
		}
		return name;
	}

/**
	private void updateColorCompositeStroke() {
		updateCurrentColor();
    	updateCurrentComposite();
    	updateCurrentStroke();
	}
*/
	private void updateCurrentStroke() {
		BasicStroke basicStroke = (BasicStroke) getGraphics().getStroke();
    	if (!basicStroke.equals(currentStroke)) {
    		currentLineWidth = (double) basicStroke.getLineWidth();
			printNewStroke(basicStroke);
    		currentStroke = basicStroke;
    		currentLineWidth = (double) basicStroke.getLineWidth();
    	}
	}
/**
	private void updateCurrentComposite() {
		Composite newComposite = getGraphics().getComposite();
    	if (!newComposite.equals(currentComposite)) {
			printNewComposite(newComposite);
    		currentComposite = newComposite;
    	}
	}
*/
	private void updateCurrentColor() {
		/**
    	Color newStrokeColor = getGraphics().getColor();
    	if (!newStrokeColor.equals(javaStrokeColor)) {
			registerColor(newStrokeColor);
    		javaStrokeColor = newStrokeColor;
    	}
    	*/
	}

	private double transformY(float y) {
		return pageSize.getHeight() - y;
	}

	private void debugGraphics(String msg) {
		AMIGraphics2D amiGraphics = new AMIGraphics2D(getGraphics(), getLinePath());
		Graphics2D graphics = getGraphics();
		if (!graphics.equals(lastGraphics)) {
			System.out.println("*****************"+msg+"*********************");
			System.out.println("lastGraphics: "+debug(lastGraphics)+" !=\n"+debug(graphics));
			lastGraphics = graphics;
		}
	}
		
	private String debug(Graphics2D graphics) {
		StringBuilder sb = new StringBuilder();
		if (graphics == null) {
			sb.append("null");
		} else {
			sb.append("back "+graphics.getBackground());
			sb.append("; col "+graphics.getColor());
			sb.append("; compos "+format(graphics.getComposite()));
			sb.append("; font "+graphics.getFont());
			sb.append("; paint "+graphics.getPaint()); // paint and color are the same?
			sb.append("; stroke "+graphics.getStroke());
			sb.append("; trans "+graphics.getTransform());
		}

		return sb.toString();
	}

	private void debugPath(String msg) {
		GeneralPath path = getLinePath();
		if (!path.equals(lastPath)) {
			System.out.println("*****************"+msg+"*********************");
			System.out.println("lastPath: "+debug(lastPath)+" !=\n"+debug(path));
			lastPath = path;
		}
	}
		

	private String debug(GeneralPath path) {
		StringBuilder sb = new StringBuilder();
		if (path == null) {
			sb.append("null");
		} else {
			sb.append("wind "+path.getWindingRule());
			sb.append("bounds "+path.getBounds2D());
			sb.append("path "+SVGPath.constructDString(path));
		}

		return sb.toString();
	}

	private String getJavaStrokeRGB() {
		return javaStrokeColor == null ? "none" : toRGB(javaStrokeColor);
	}

	private String getJavaFillRGB() {
		return javaFillColor == null ? "none" : toRGB(javaFillColor);
	}

    // ===========================

	private PathPrimitiveList ensurePathPrimitiveList() {
		if (pathPrimitiveList == null) {
			pathPrimitiveList = new PathPrimitiveList();
		}
		return pathPrimitiveList;
	}


	private void printNewFont(PDFont font) {
		String name = font.getName();
		name = name.contains("+") ? name.split("\\+")[1] : name;
		if (fontSet.contains(font)) {
			System.out.print("+");
		} else {
			fontSet.add(font);
			System.out.println("\n"+name);
		}
	}

	private void printNewComposite(Composite composite) {
		System.out.println("comp: "+composite);
	}
	
	private void printNewStroke(BasicStroke basicStroke) {
		if (!strokeSet .contains(basicStroke)) {
			System.out.println("\n"+format(basicStroke));
			strokeSet.add(basicStroke);
		} else {
			System.out.print("|");
		}
	}
	
	private String format(BasicStroke basicStroke) {
		String s = "";
		s += "dashArray:"+basicStroke.getDashArray()+";";
		s += "lwidth:"+basicStroke.getLineWidth()+";";
		return s;
	}

	private void registerColor(Color color) {
		if (!colorSet .contains(color)) {
    		System.out.println("\nnew col:"+Integer.toHexString(color.getRGB()));
			colorSet.add(color);
		} else {
			System.out.print("\ncol "+Integer.toHexString(color.getRGB()));
		}
	}


	public static String toRGB(Color javaColor) {
		return javaColor == null ? null : "rgb(" + (int) javaColor.getRed()+","+javaColor.getGreen()+","+javaColor.getBlue()+")";
	}

    private String format(Matrix mat, int ndec) {
    	return ""+
    	    	"s("+Util.format(mat.getScaleX(), ndec)+","+Util.format(mat.getScaleY(), ndec)+")"+" "+
    	    	"t("+Util.format(mat.getTranslateX(), ndec)+","+Util.format(mat.getTranslateY(), ndec)+")"+
    	    	"";
	}

	private String format(Vector vec, int ndec) {
    	return ""+
    	    	"s("+Util.format(vec.getX(), ndec)+","+Util.format(vec.getY(), ndec)+")"+
    	    	"";
	}

    private String format(float x, float y, int ndec) {
    	String s = "("+Util.format(x, ndec)+","+Util.format(y,  ndec)+")";
    	return s;
	}

    private String format(float x1, float y1, float x2, float y2, float x3, float y3, int ndec) {
    	return "["+format(x1,y1,ndec)+","+format(x2,y2,ndec)+","+format(x3,y3,ndec)+"]";
    }
    
    private String format(Point2D point, int ndec) {
    	return "("+Util.format(point.getX(), ndec)+","+Util.format(point.getY(), ndec)+")";
	}

    private String format(PDAnnotation annotation) {
    	if ("Link".equals(annotation.getSubtype().toString())) {
    		return "Link";
    	}
    	StringBuilder sb = new StringBuilder();
    	int fl = annotation.getAnnotationFlags(); if (fl != 0) {
    		sb.append("flags:"+fl);
    	}
    	appendNonNull(sb, "name", annotation.getAnnotationName());
    	appendNonNull(sb, "appear", annotation.getAppearance());
    	appendNonNull(sb, "appearState", annotation.getAppearanceState());
    	appendNonNull(sb, "border", annotation.getBorder());
    	appendNonNull(sb, "color", annotation.getColor());
    	appendNonNull(sb, "contents", annotation.getContents());
    	appendNonNull(sb, "cosObject", annotation.getCOSObject());
    	appendNonNull(sb, "modDate", annotation.getModifiedDate());
    	appendNonNull(sb, "page", annotation.getPage());
    	appendNonNull(sb, "subtype", annotation.getSubtype());

		return sb.toString();
	}

	private void appendNonNull(StringBuilder sb, String title, Object object) {
		if (object != null) {
			sb.append(title+":"+object);
		}
	}

    private String format(Composite composite) {
    	String s = "";
    	float alpha = ((AlphaComposite)composite).getAlpha();
    	int rule = ((AlphaComposite)composite).getRule();
    	s += "alpha:"+alpha+"; rule:"+rule;
    	return s;
	}

    private String format(PDFormXObject xobject) {
    	String s = "";
    	int form = xobject.getFormType();
    	PDRectangle rect = xobject.getBBox();
    	PDResources resources = xobject.getResources();
    	s += "form:"+form+";rect:"+rect+";res:"+resources;
    	return s;
	}

	public SVGElement getSVGElement() {
		return gTop;
	}

	public void setCurrentPage(PDPage currentPage) {
		this.currentPage = currentPage;
		PDRectangle cropBox = currentPage.getCropBox();
	}

    private float clampColor(float color)
    {
        return color < 0 ? 0 : (color > 1 ? 1 : color);        
    }

	private void showAMIGDiff(AMIGraphics2D currentAMIG) {
		AMIGraphics2D newAMIG = createCurrentAMIG();
//    	System.out.println("??? "+newAMIG.getNonNullFields());
    	AMIGraphics2D diff = newAMIG.createDiffGraphics2D(currentAMIG);
    	String nonNull = diff.getNonNullFields().trim();
    	
    	if (!"".equals(nonNull)) {
    		System.out.println(">>> "+nonNull);
    	}
	}

    private AMIGraphics2D createCurrentAMIG() {
    	return new AMIGraphics2D(getGraphics(), getLinePath());
	}




}

