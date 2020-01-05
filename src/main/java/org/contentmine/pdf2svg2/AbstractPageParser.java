package org.contentmine.pdf2svg2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.path.PathPrimitiveList;

import nu.xom.Attribute;

/** supports subclasses of PageDrawer (example from PDFBox2) which renders PDFStream
 * to AWT. AMIPDFParser intercepts these and builds SVG.
 * Only the key methods for SVG are included here
 * 
 * @author pm286
 *
 */
public abstract class AbstractPageParser extends PageDrawer {
	private static final Logger LOG = Logger.getLogger(AbstractPageParser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final String ILLEGAL_CHAR = "?";
	protected static final String CODE = "code";
	private static final String UNICODE = "unicode";
	protected static final String MATRIX = "matrix";
	
	public static String toRGB(Color javaColor) {
		return javaColor == null ? null : "rgb(" + (int) javaColor.getRed()+","+javaColor.getGreen()+","+javaColor.getBlue()+")";
	}

	protected Set<PDFont> fontSet = new HashSet<>();
	protected Set<Stroke> strokeSet = new HashSet<>();
	protected Set<Color> colorSet = new HashSet<>();
	protected Map<String, PDFont> pdFontByName;
	protected SVGG svgg;
	protected PageSerial pageSerial;

	protected int ndec = 2;
	protected int nxydec = 3;
	protected int matdec = 5;
	protected double eps = 0.000000001;
	protected Map<String, BufferedImage> imageByTitle;
	protected double yMax;

	protected AMIDebugParameters debugParams;
	protected PathPrimitiveList pathPrimitiveList;
	protected PDRectangle pageSize;

	protected Color awtStrokeColor;
	protected Color awtFillColor;
	protected Stroke currentStroke;
	protected Double currentLineWidth;
	protected RenderingMode textRenderingMode;
	protected SVGG currentTextPhrase;
	protected Real2Range mediaBox;
	protected int pageRotation;
	BufferedImage renderedImage;
	protected Real2Range viewBox;
	protected PDFDocumentProcessor documentProcessor;

	protected AbstractPageParser(PageDrawerParameters parameters, AMIDebugParameters debugParams) throws IOException {
		super(parameters);
        this.debugParams = debugParams;
		init();
	}
	
	void init() {
    	imageByTitle = new HashMap<String, BufferedImage>();
    	fontSet = new HashSet<>();
    	strokeSet = new HashSet<>();
    	colorSet = new HashSet<>();
    	svgg = new SVGG();

	}
	
//	public SVGElement getSVGElement() {
//		return svgg;
//	}

	public SVGG getSVGG() {
		return svgg;
	}
    

	
	/** the following should be used to trap the graphics stream by overriding
	 */
    /**
     * Draws the page to the requested context.
     * 
     * @param g The graphics context to draw onto.
     * @param pageSize The size of the page to draw.
     * @throws IOException If there is an IO error while drawing the page.
     * 
     * not mandatory
     */
	@Override
    public void drawPage(Graphics g, PDRectangle pageSize) throws IOException {
    	this.pageSize = pageSize;
    	super.drawPage(g, pageSize);
    	mediaBox = new Real2Range(
    			new RealRange(pageSize.getLowerLeftX(), pageSize.getUpperRightX()),
    			new RealRange(pageSize.getLowerLeftY(), pageSize.getUpperRightY())
    	);
    	yMax = pageSize.getUpperRightY();
    	pageRotation = getPage().getRotation() % 360;
    	if (pageRotation != 0) {
    		LOG.warn("Rotated page: "+pageRotation);
    	}

    };

//==== TEXT ==========    

    /** start of text. I think this should always be balanced by endText() 
     * use beginText ... endText if you want to chunk the text in the way the 
     * authors have 
     * not mandatory */
    @Override
    public void beginText() throws IOException {
    	super.beginText();
    }
    
    /** end of text. Should always balance startText()? 
     * not mandatory*/
    @Override
    public void endText() throws IOException {
    	super.endText();
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
    /** captures the character, its Font and code and width 
     * the stroke/fill is captured from PDGraphics and distinction is made through the renderingMode() 
     * */
    @Override
    protected void showFontGlyph(Matrix matrix, PDFont font, int code, String unicode, Vector displacement) throws IOException {
    	super.showFontGlyph(matrix, font, code, unicode, displacement);
    }

// ===== APPEND RECTANGLE =========

    /** not sure what this does - maybe sets a window
     * not mandatory */
    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
    	super.appendRectangle(p0, p1, p2, p3);
    }
    
// ===== PATHS ========
    
    /**  this does the actual stroking 
     * gets stroke and Stroke from PDGraphics 
     * this is where we capture the stroke and stroke-width then create the SVGPath and dispatch it
     * does not reset fill*/
    @Override
    public void strokePath() throws IOException {
    	super.strokePath();
    }

    /** gets fill from PDGraphics.
     * does not reset the stroke
     * this is where we capture the fill and then create the SVGPath and dispatch it
     * probably add it to a SVGG.
     * 
     * @param windingRule
     * @throws IOException
     */
    @Override
    public void fillPath(int windingRule) throws IOException {
    	super.fillPath(windingRule);
    }

    /**
     * Fills and then strokes the path.
     * this is when the stroke width interacts with the fill and changes the latter Shape
     * (I think)
     * 
     * @param windingRule The winding rule this path will use.
     * @throws IOException If there is an IO error while filling the path.
     */
    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
    	super.fillAndStrokePath(windingRule);
    }

    /** captures move and adds to PathPrimitiveList
     * mandatory
     * will add M to path
     * @param x
     * @param y
     */
    @Override
     public void moveTo(float x, float y) {
    	 super.moveTo(x, y);
     };

    /** captures lineTo and adds to PathPrimitiveList
     * mandatory
     * will add L to path
     * @param x
     * @param y
     */
    @Override
    public void lineTo(float x, float y) {
    	super.lineTo(x, y);
    }

    /**captures curveTo and adds to PathPrimitive list.
     * mandatory
     * will add C's to path
     */
    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
    	super.curveTo(x1, y1, x2, y2, x3, y3);
    }
    
    /** may be useful for complex paths. Not sure
     *  */
    @Override
    public Point2D getCurrentPoint() {
    	return super.getCurrentPoint();
    }

    /** close the path with a line 
     * mandatory
     * will add Z to path
    not the same as endPath
    
    */
    @Override
    public void closePath() {
    	super.closePath();
    }

    /** ends the path 
     * used as a time to flush the primitive list to SVG
     * 
     */
    @Override
    public void endPath() {
    	super.endPath();
    }

// ===== IMAGE ======
    /** captures the image as a bitmap and can write as file
     * mandatory
     * @param pdImage
     * @throws IOException
     */
    @Override
    public void drawImage(PDImage pdImage) throws IOException {
    	super.drawImage(pdImage);
    }
    
 // ===== TRANSPARENCY =====  ignored
    public void showTransparencyGroup(PDTransparencyGroup form) throws IOException {
    	super.showTransparencyGroup(form);
    }
    
// ===== MARKED CONTENT =====  ignored  
    
    public int getNdec() {
		return ndec;
	}
	public void setNdec(int ndec) {
		this.ndec = ndec;
	}
	public int getNxydec() {
		return nxydec;
	}
	public void setNxydec(int nxydec) {
		this.nxydec = nxydec;
	}
	public int getMatdec() {
		return matdec;
	}
	public void setMatdec(int matdec) {
		this.matdec = matdec;
	}
	public double getEps() {
		return eps;
	}
	public void setEps(double eps) {
		this.eps = eps;
	}
	// AMI interface
	public Set<PDFont> getFontSet() {
		return fontSet;
	}
	public Set<Stroke> getStrokeSet() {
		return strokeSet;
	}
	public Set<Color> getColorSet() {
		return colorSet;
	}
	
	public void createPageSerial(int pageIndex) {
		PageSerial pageSerial = PageSerial.createFromZeroBasedPage(pageIndex);
		this.setPageSerial(pageSerial);
	}

	public PageSerial getPageSerial() {
		return pageSerial;
	}
	
	/** called from drawImage() */
	protected void extractImage(PDImage pdImage) throws IOException {
		if (pageSerial == null) {
			throw new RuntimeException("null pageSerial");
		}
		PageSerial imageSerial = PageSerial.createFromZeroBasedPages(
			pageSerial.getZeroBasedPage(), imageByTitle.size());
		BufferedImage bufferedImage = pdImage.getImage();
		// FIXME should write image to disk here
		System.out.print("["+"."+imageByTitle.size()+"]");
		
		SVGRect rect = getBoundingRect();
		rect.format(3);
		String serialTitle = pageSerial.getOneBasedSerialString();
		rect.setTitle(serialTitle);
		svgg.appendChild(rect);
		Int2Range box = new Int2Range(rect.getBoundingBox());
		int width = (int)(double)rect.getWidth();
		int height = (int)(double)rect.getHeight();
		String oneBasedSerialString = imageSerial.getOneBasedSerialString();
		String title = createTitle(box, oneBasedSerialString);
		imageByTitle.put(title,bufferedImage);
		SVGText text = new SVGText(rect.getBoundingBox().getLLURCorners()[0], title);
		text.addSVGClassName("image");
		text.setId("image."+oneBasedSerialString);
		text.addAttribute(new Attribute("width", ""+width));
		text.addAttribute(new Attribute("height", ""+height));
		svgg.appendChild(text);
	}
	private SVGRect getBoundingRect() {
		Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
	    AffineTransform at = ctm.createAffineTransform();
	    Rectangle2D unitRect = new Rectangle2D.Float(0, 0, 1, 1);
	    Rectangle2D bounds = at.createTransformedShape(unitRect).getBounds2D();
		Real2Range box = new Real2Range(
	    		new RealRange(bounds.getMinX(), bounds.getMaxX()),
	    		new RealRange(yMax - bounds.getMaxY(), yMax - bounds.getMinY())
	    		).format(3);
	    SVGRect rect = SVGRect.createFromReal2Range(box);
	    rect.setStroke("blue").setStrokeWidth(0.3);
		return rect;
	}
	private String createTitle(Int2Range box, String oneBasedSerialString) {
		return "image."+oneBasedSerialString+"."+box.getXRange().getMin()+"_"+box.getXRange().getMax()+"."+box.getYRange().getMin()+"_"+box.getYRange().getMax();
	}

	public AMIDebugParameters getDebugParameters() {
		return debugParams;
	}

	protected PathPrimitiveList ensurePathPrimitiveList() {
		if (pathPrimitiveList == null) {
			pathPrimitiveList = new PathPrimitiveList();
		}
		return pathPrimitiveList;
	}
	
	protected double transformY(float y) {
		return pageSize.getHeight() - y;
	}

	protected String getJavaStrokeRGB() {
		return awtStrokeColor == null ? "none" : toRGB(awtStrokeColor);
	}

	protected String getJavaFillRGB() {
		return awtFillColor == null ? "none" : toRGB(awtFillColor);
	}

	protected void registerColor(String source, String fillStroke, Color color) {
		if (!colorSet .contains(color)) {
			colorSet.add(color);
		} else {
		}
	}

	protected String format(Point2D point, int ndec) {
		return "("+Util.format(point.getX(), ndec)+","+Util.format(point.getY(), ndec)+")";
	}

	protected String format(float x, float y, int ndec) {
		String s = "("+Util.format(x, ndec)+","+Util.format(y,  ndec)+")";
		return s;
	}

	protected String format(PDAnnotation annotation) {
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

	protected String format(PDFormXObject xobject) {
		String s = "";
		int form = xobject.getFormType();
		PDRectangle rect = xobject.getBBox();
		PDResources resources = xobject.getResources();
		s += "form:"+form+";rect:"+rect+";res:"+resources;
		return s;
	}

	void appendNonNull(StringBuilder sb, String title, Object object) {
		if (object != null) {
			sb.append(title+":"+object);
		}
	}

	protected void updateRenderingColorsStroke(String source) {
		textRenderingMode = getGraphicsState().getTextState().getRenderingMode();
		try {
	        awtStrokeColor = (Color) getPaint(getGraphicsState().getStrokingColor());
	        awtFillColor = (Color) getPaint(getGraphicsState().getNonStrokingColor());
		} catch (IOException e) {
			throw new RuntimeException("cannot extract colors", e);
		}
	    updateCurrentStroke();
	}

	protected void createPathAndFlush(String source) {
		if (pathPrimitiveList != null) {
	    	SVGPath path = new SVGPath(pathPrimitiveList);
	    	path.setStroke(getJavaStrokeRGB());
	    	path.setFill(getJavaFillRGB());
	    	path.setStrokeWidth(Util.format(currentLineWidth, ndec));
	    	path.format(ndec);
	    	svgg.appendChild(path);
	    	pathPrimitiveList = null;
		} else {
			LOG.trace("createPathAndFlush: no primitives");
		}
	}

	protected void registerFont(PDFont font) {
		getOrCreatePDFontByName();
		String name = getName(font);
		if (!pdFontByName.containsKey(name)) {
			pdFontByName.put(name, font);
		}
	}

	Map<String, PDFont> getOrCreatePDFontByName() {
		if (pdFontByName == null) {
			pdFontByName = new HashMap<>();
		}
		return pdFontByName;
	}

	protected String getName(PDFont font) {
		String name = font == null ? null : font.getName();
		if (name != null && name.contains("+")) {
			name = name.split("\\+")[1];
		}
		return name;
	}

	protected String format(float x1, float y1, float x2, float y2, float x3, float y3,
			int ndec) {
				return "["+format(x1,y1,ndec)+","+format(x2,y2,ndec)+","+format(x3,y3,ndec)+"]";
			}

	/**
	private void updateColorCompositeStroke() {
		updateCurrentColor();
		updateCurrentComposite();
		updateCurrentStroke();
	}
	*/
	void updateCurrentStroke() {
		BasicStroke basicStroke = (BasicStroke) getGraphics().getStroke();
		if (!basicStroke.equals(currentStroke)) {
			currentLineWidth = (double) basicStroke.getLineWidth();
			currentStroke = basicStroke;
			currentLineWidth = (double) basicStroke.getLineWidth();
		}
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

	public Map<String, BufferedImage> getOrCreateRawImageMap() {
		if (imageByTitle == null) {
			imageByTitle = new HashMap<String, BufferedImage>();
		}
		return imageByTitle;
	}
}
