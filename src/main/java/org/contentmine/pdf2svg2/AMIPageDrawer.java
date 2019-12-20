package org.contentmine.pdf2svg2;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.graphics.image.PDImage;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;
import org.contentmine.eucl.euclid.Util;

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
	
    private AMIDebugParameters debugParams;
	private FontGlyph currentFontGlyph;
	private Color currentColor;
	private Stroke currentStroke;
	private Set<String> nameSet = new HashSet<>();
	private Set<String> strokeSet = new HashSet<>();

	AMIPageDrawer(PageDrawerParameters parameters, AMIDebugParameters debugParams) throws IOException {
        super(parameters);
        this.debugParams = debugParams;
    }
    
    public void setDebugParameters(AMIDebugParameters params) {
    	this.debugParams = params;
    }
    
    public AMIDebugParameters getDebugParameters() {
    	return debugParams;
    }

    /**
     * Color replacement.
     */
    @Override
    protected Paint getPaint(PDColor color) throws IOException {
    	float[] components = color.getComponents();
    	if (debugParams.showColor) System.out.println("Col["+Util.toString(components, 2)+"]");
        return super.getPaint(color);
    }

//    /**
//     * Glyph bounding boxes.
//     */
//    @Override
//    protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
//                             Vector displacement) throws IOException
//    {
//
//    	System.out.println("glyph "+font+"/"+code+"/"+unicode+"/"+displacement);
//    	// JUST AN EXAMPLE
//
//        // draw glyph
//        super.showGlyph(textRenderingMatrix, font, code, unicode, displacement);
//        
//        // draw box round glyph
//        // bbox in EM -> user units
//        Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
//        AffineTransform at = textRenderingMatrix.createAffineTransform();
//        bbox = at.createTransformedShape(bbox);
//        
//        // save
//        Graphics2D graphics = getGraphics();
//        System.out.println("Graphics "+graphics);
//        Color color = graphics.getColor();
//        Stroke stroke = graphics.getStroke();
//        Shape clip = graphics.getClip();
//
//        // draw
//        graphics.setClip(graphics.getDeviceConfiguration().getBounds());
//        graphics.setColor(Color.RED);
//        graphics.setStroke(new BasicStroke(.5f));
//        graphics.draw(bbox);
//
//        // restore
//        graphics.setStroke(stroke);
//        graphics.setColor(color);
//        graphics.setClip(clip);
//    }

//    /**
//     * Filled path bounding boxes.
//     */
//    @Override
//    public void fillPath(int windingRule) throws IOException
//    {
//    	GeneralPath generalPath = this.getLinePath();
//    	System.out.println("general path "+generalPath);
//    	// JUST AN EXAMPLE
//
//        // bbox in user units
//        Shape bbox = getLinePath().getBounds2D();
//        
//        // draw path (note that getLinePath() is now reset)
//        super.fillPath(windingRule);
//        
//        // save
//        Graphics2D graphics = getGraphics();
//        Color color = graphics.getColor();
//        Stroke stroke = graphics.getStroke();
//        Shape clip = graphics.getClip();
//
//        // draw
//        graphics.setClip(graphics.getDeviceConfiguration().getBounds());
//        graphics.setColor(Color.GREEN);
//        graphics.setStroke(new BasicStroke(.5f));
//        graphics.draw(bbox);
//
//        // restore
//        graphics.setStroke(stroke);
//        graphics.setColor(color);
//        graphics.setClip(clip);
//    }

//    /**
//     * Custom annotation rendering.
//     */
//    @Override
//    public void showAnnotation(PDAnnotation annotation) throws IOException
//    {
//    	// JUST AN EXAMPLE
//
//        // save
//        saveGraphicsState();
//        
//        // 35% alpha
//        getGraphicsState().setNonStrokeAlphaConstant(0.35);
//        super.showAnnotation(annotation);
//        
//        // restore
//        restoreGraphicsState();
//    }
    
    // ============================================================
    /**
     * Draws the pattern stream to the requested context.
     *
     * @param g The graphics context to draw onto.
     * @param pattern The tiling pattern to be used.
     * @param colorSpace color space for this tiling.
     * @param color color for this tiling.
     * @param patternMatrix the pattern matrix
     * @throws IOException If there is an IO error while drawing the page.
     */
//    void drawTilingPattern(Graphics2D g, PDTilingPattern pattern, PDColorSpace colorSpace,
//                                  PDColor color, Matrix patternMatrix) throws IOException
//    {
//        Graphics2D savedGraphics = graphics;
//        graphics = g;
//
//        GeneralPath savedLinePath = linePath;
//        linePath = new GeneralPath();
//        int savedClipWindingRule = clipWindingRule;
//        clipWindingRule = -1;
//
//        Area savedLastClip = lastClip;
//        lastClip = null;
//        Shape savedInitialClip = initialClip;
//        initialClip = null;
//
//        boolean savedFlipTG = flipTG;
//        flipTG = true;
//
//        setRenderingHints();
//        processTilingPattern(pattern, color, colorSpace, patternMatrix);
//
//        flipTG = savedFlipTG;
//        graphics = savedGraphics;
//        linePath = savedLinePath;
//        lastClip = savedLastClip;
//        initialClip = savedInitialClip;
//        clipWindingRule = savedClipWindingRule;
//    }

    @Override
    public void beginText() throws IOException {
    	if (debugParams.showBeginText) System.out.println("beginText");
    	super.beginText();
//        setClip();
//        beginTextClip();
    }

    @Override
    public void endText() throws IOException {
    	
    	if (debugParams.showEndText) System.out.println("endText");
    	super.endText();
//        endTextClip();
    }
    
    @Override
    protected void showFontGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                                 Vector displacement) throws IOException {
    	FontGlyph fontGlyph = new FontGlyph(textRenderingMatrix, font, code, unicode, displacement);
    	PDFont newFont = (currentFontGlyph == null || !currentFontGlyph.getFont().equals(font)) ? font : null; 
    	if (newFont != null && debugParams.showFontGlyph) {
//    		System.out.println("showFontGlyph "+format(textRenderingMatrix, 2)+"/"+
//    	     	    font.getName()+"/"+code+"/"+unicode+"/"+format(displacement, 3));
//    		System.out.println(font.getName().split("\\+")[1]);
    		String name = font.getName();
			name = name.contains("+") ? name.split("\\+")[1] : name;
			if (nameSet.contains(name)) {
				System.out.print("+");
			} else {
				nameSet.add(name);
				System.out.println("\n"+name);
			}
    	}
    	super.showFontGlyph(textRenderingMatrix, font, code, unicode, displacement);
    	currentFontGlyph = fontGlyph;
//        AffineTransform at = textRenderingMatrix.createAffineTransform();
//        at.concatenate(font.getFontMatrix().createAffineTransform());
//
//        drawGlyph2D(glyph2D, font, code, displacement, at);
    }

	/**
     * Render the font using the Glyph2D interface.
     * 
     * @param glyph2D the Glyph2D implementation provided a GeneralPath for each glyph
     * @param font the font
     * @param code character code
     * @param displacement the glyph's displacement (advance)
     * @param at the transformation
     * @throws IOException if something went wrong
     */
    private void drawGlyph2D(Glyph2D glyph2D, PDFont font, int code, Vector displacement,
                             AffineTransform at) throws IOException {
//        PDGraphicsState state = getGraphicsState();
//        RenderingMode renderingMode = state.getTextState().getRenderingMode();
//
//        GeneralPath path = glyph2D.getPathForCharacterCode(code);
//        if (path != null)
//        {
//            // Stretch non-embedded glyph if it does not match the height/width contained in the PDF.
//            // Vertical fonts have zero X displacement, so the following code scales to 0 if we don't skip it.
//            // TODO: How should vertical fonts be handled?
//            if (!font.isEmbedded() && !font.isVertical() && !font.isStandard14() && font.hasExplicitWidth(code))
//            {
//                float fontWidth = font.getWidthFromFont(code);
//                if (fontWidth > 0 && // ignore spaces
//                        Math.abs(fontWidth - displacement.getX() * 1000) > 0.0001)
//                {
//                    float pdfWidth = displacement.getX() * 1000;
//                    at.scale(pdfWidth / fontWidth, 1);
//                }
//            }
//
//            // render glyph
//            Shape glyph = at.createTransformedShape(path);
//
//            if (renderingMode.isFill())
//            {
//                graphics.setComposite(state.getNonStrokingJavaComposite());
//                graphics.setPaint(getNonStrokingPaint());
//                setClip();
//                if (isContentRendered())
//                {
//                    graphics.fill(glyph);
//                }
//            }
//
//            if (renderingMode.isStroke())
//            {
//                graphics.setComposite(state.getStrokingJavaComposite());
//                graphics.setPaint(getStrokingPaint());
//                graphics.setStroke(getStroke());
//                setClip();
//                if (isContentRendered())
//                {
//                    graphics.draw(glyph);
//                }
//            }
//
//            if (renderingMode.isClip())
//            {
//                textClippings.add(glyph);
//            }
//        }
    }

//    @Override
//    protected void showType3Glyph(Matrix textRenderingMatrix, PDType3Font font, int code,
//            String unicode, Vector displacement) throws IOException {
    	
//        PDGraphicsState state = getGraphicsState();
//        RenderingMode renderingMode = state.getTextState().getRenderingMode();
//        if (!RenderingMode.NEITHER.equals(renderingMode))
//        {
//            super.showType3Glyph(textRenderingMatrix, font, code, unicode, displacement);
//        }
//    }

    /**
     * Provide a Glyph2D for the given font.
     * 
     * @param font the font
     * @return the implementation of the Glyph2D interface for the given font
     * @throws IOException if something went wrong
     */
//    private Glyph2D createGlyph2D(PDFont font) throws IOException
//    {
//        Glyph2D glyph2D = fontGlyph2D.get(font);
//        // Is there already a Glyph2D for the given font?
//        if (glyph2D != null)
//        {
//            return glyph2D;
//        }
//
//        if (font instanceof PDTrueTypeFont)
//        {
//            PDTrueTypeFont ttfFont = (PDTrueTypeFont)font;
//            glyph2D = new TTFGlyph2D(ttfFont);  // TTF is never null
//        }
//        else if (font instanceof PDType1Font)
//        {
//            PDType1Font pdType1Font = (PDType1Font)font;
//            glyph2D = new Type1Glyph2D(pdType1Font); // T1 is never null
//        }
//        else if (font instanceof PDType1CFont)
//        {
//            PDType1CFont type1CFont = (PDType1CFont)font;
//            glyph2D = new Type1Glyph2D(type1CFont);
//        }
//        else if (font instanceof PDType0Font)
//        {
//            PDType0Font type0Font = (PDType0Font) font;
//            if (type0Font.getDescendantFont() instanceof PDCIDFontType2)
//            {
//                glyph2D = new TTFGlyph2D(type0Font); // TTF is never null
//            }
//            else if (type0Font.getDescendantFont() instanceof PDCIDFontType0)
//            {
//                // a Type0 CIDFont contains CFF font
//                PDCIDFontType0 cidType0Font = (PDCIDFontType0)type0Font.getDescendantFont();
//                glyph2D = new CIDType0Glyph2D(cidType0Font); // todo: could be null (need incorporate fallback)
//            }
//        }
//        else
//        {
//            throw new IllegalStateException("Bad font type: " + font.getClass().getSimpleName());
//        }
//
//        // cache the Glyph2D instance
//        if (glyph2D != null)
//        {
//            fontGlyph2D.put(font, glyph2D);
//        }
//
//        if (glyph2D == null)
//        {
//            // todo: make sure this never happens
//            throw new UnsupportedOperationException("No font for " + font.getName());
//        }
//
//        return glyph2D;
//    }

    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
    	if (debugParams.showAppendRectangle) {
    		System.out.println("appRect ["+format(p0, 2)+"/"+format(p1, 2)+"/"+format(p2, 2)+"/"+format(p3, 2)+"]");
    	}
    	super.appendRectangle(p0, p1, p2, p3);
//    	GeneralPath linePath0 = super.getLinePath();
//        // to ensure that the path is created in the right direction, we have to create
//        // it by combining single lines instead of creating a simple rectangle
//        linePath0.moveTo((float) p0.getX(), (float) p0.getY());
//        linePath0.lineTo((float) p1.getX(), (float) p1.getY());
//        linePath0.lineTo((float) p2.getX(), (float) p2.getY());
//        linePath0.lineTo((float) p3.getX(), (float) p3.getY());
//
//        // close the subpath instead of adding the last line so that a possible set line
//        // cap style isn't taken into account at the "beginning" of the rectangle
//        linePath0.closePath();
    }

    @Override
    public void strokePath() throws IOException {
    	Graphics2D graphics = getGraphics();
    	Color color = graphics.getColor();
    	if (!color.equals(currentColor)) {
    		System.err.println(Integer.toHexString(color.getRGB() /*& 0x00ffffff*/));
    		currentColor = color;
    	}
    	Stroke stroke = graphics.getStroke();
    	if (!stroke.equals(currentStroke)) {
			if (!strokeSet .contains(stroke)) {
	    		System.out.println("\n"+stroke);
    		} else {
    			System.out.print("|");
    		}
    		currentStroke = stroke;
    	}
    	
    	
//        graphics.setComposite(getGraphicsState().getStrokingJavaComposite());
//        graphics.setPaint(getStrokingPaint());
//        graphics.setStroke(getStroke());
//        setClip();
//        //TODO bbox of shading pattern should be used here? (see fillPath)
//        if (isContentRendered())
//        {
//            graphics.draw(linePath);
//        }
//        linePath.reset();
    	Composite composite = getGraphicsState().getStrokingJavaComposite();
//    	Paint paint = getStrokingPaint();  // NOT VIS
//    	Stroke stroke = getStroke();  // NOT VIS
    	
    	if (debugParams.showStrokePath) {
    		System.out.println("strokePath "+format(composite)+"/");
    	}
    	super.strokePath();
    }

	@Override
    public void fillPath(int windingRule) throws IOException {
    	if (debugParams.showFillPath) {
    		System.out.println("super.fillPath(windingRule)");
    	}
    	super.fillPath(windingRule);
//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
//        graphics.setPaint(getNonStrokingPaint());
//        setClip();
//        linePath.setWindingRule(windingRule);
//
//        // disable anti-aliasing for rectangular paths, this is a workaround to avoid small stripes
//        // which occur when solid fills are used to simulate piecewise gradients, see PDFBOX-2302
//        // note that we ignore paths with a width/height under 1 as these are fills used as strokes,
//        // see PDFBOX-1658 for an example
//        Rectangle2D bounds = linePath.getBounds2D();
//        boolean noAntiAlias = isRectangular(linePath) && bounds.getWidth() > 1 &&
//                                                         bounds.getHeight() > 1;
//        if (noAntiAlias)
//        {
//            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                                      RenderingHints.VALUE_ANTIALIAS_OFF);
//        }
//
//        Shape shape;
//        if (!(graphics.getPaint() instanceof Color))
//        {
//            // apply clip to path to avoid oversized device bounds in shading contexts (PDFBOX-2901)
//            Area area = new Area(linePath);
//            area.intersect(new Area(graphics.getClip()));
//            intersectShadingBBox(getGraphicsState().getNonStrokingColor(), area);
//            shape = area;
//        }
//        else
//        {
//            shape = linePath;
//        }
//        if (isContentRendered())
//        {
//            graphics.fill(shape);
//        }
//        
//        linePath.reset();
//
//        if (noAntiAlias)
//        {
//            // JDK 1.7 has a bug where rendering hints are reset by the above call to
//            // the setRenderingHint method, so we re-set all hints, see PDFBOX-2302
//            setRenderingHints();
//        }
    }

    /**
     * Fills and then strokes the path.
     *
     * @param windingRule The winding rule this path will use.
     * @throws IOException If there is an IO error while filling the path.
     */
    @Override
    public void fillAndStrokePath(int windingRule) throws IOException {
    	if (debugParams.showFillAndStrokePath) {
    		System.out.println("super.fillAndStrokePath(windingRule)");
    	}
    	super.fillAndStrokePath(windingRule);
//        // TODO can we avoid cloning the path?
//        GeneralPath path = (GeneralPath)linePath.clone();
//        fillPath(windingRule);
//        linePath = path;
//        strokePath();
    }

    @Override
    public void clip(int windingRule) {
    	if (debugParams.showClip) {
    		System.out.println("clip("+windingRule+")");
    	}
    	super.clip(windingRule);
//        // the clipping path will not be updated until the succeeding painting operator is called
//        clipWindingRule = windingRule;
    }

    @Override
    public void moveTo(float x, float y) {
    	if (debugParams.showMoveTo) {
    		System.out.println("M"+format(x, y, 2));
    	}
    	super.moveTo(x, y);
//        linePath.moveTo(x, y);
    }

	@Override
    public void lineTo(float x, float y) {
    	if (debugParams.showLineTo) {
    		System.out.println("L"+format(x, y, 2));
    	}
    	super.lineTo(x, y);
//        linePath.lineTo(x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
    	if (debugParams.showCurveTo) {
    		System.out.println("C"+format(x1, y1, x2, y2, x3, y3, 2));
    	}
    	super.curveTo(x1, y1, x2, y2, x3, y3);
//        linePath.curveTo(x1, y1, x2, y2, x3, y3);
    }

	@Override
    public Point2D getCurrentPoint() {
    	if (debugParams.showCurrentPoint) {
    		System.out.println("CurPt"+format(super.getCurrentPoint(), 2));
    	}
    	return super.getCurrentPoint();
//        return linePath.getCurrentPoint();
    }

	@Override
    public void closePath() {
    	if (debugParams.showClosePath) {
    		System.out.println("closePath");
    	}
    	super.closePath();
//        linePath.closePath();
    }

    @Override
    public void endPath() {
    	if (debugParams.showEndPath) {
    		System.out.println("endPath");
    	}
    	super.endPath();
//        if (clipWindingRule != -1)
//        {
//            linePath.setWindingRule(clipWindingRule);
//            getGraphicsState().intersectClippingPath(linePath);
//
//            // PDFBOX-3836: lastClip needs to be reset, because after intersection it is still the same 
//            // object, thus setClip() would believe that it is cached.
//            lastClip = null;
//
//            clipWindingRule = -1;
//        }
//        linePath.reset();
    }
    
    @Override
    public void drawImage(PDImage pdImage) throws IOException {
    	if (debugParams.showDrawImage) {
    		System.out.println("drawImage "+pdImage);
    	}
    	super.drawImage(pdImage);
//        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
//        AffineTransform at = ctm.createAffineTransform();
//
//        if (!pdImage.getInterpolate())
//        {
//            boolean isScaledUp = pdImage.getWidth() < Math.round(at.getScaleX()) ||
//                                 pdImage.getHeight() < Math.round(at.getScaleY());
//
//            // if the image is scaled down, we use smooth interpolation, eg PDFBOX-2364
//            // only when scaled up do we use nearest neighbour, eg PDFBOX-2302 / mori-cvpr01.pdf
//            // stencils are excluded from this rule (see survey.pdf)
//            if (isScaledUp || pdImage.isStencil())
//            {
//                graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
//            }
//        }
//
//        if (pdImage.isStencil())
//        {
//            if (getGraphicsState().getNonStrokingColor().getColorSpace() instanceof PDPattern)
//            {
//                // The earlier code for stencils (see "else") doesn't work with patterns because the
//                // CTM is not taken into consideration.
//                // this code is based on the fact that it is easily possible to draw the mask and 
//                // the paint at the correct place with the existing code, but not in one step.
//                // Thus what we do is to draw both in separate images, then combine the two and draw
//                // the result. 
//                // Note that the device scale is not used. In theory, some patterns can get better
//                // at higher resolutions but the stencil would become more and more "blocky".
//                // If anybody wants to do this, have a look at the code in showTransparencyGroup().
//
//                // draw the paint
//                Paint paint = getNonStrokingPaint();
//                Rectangle2D unitRect = new Rectangle2D.Float(0, 0, 1, 1);
//                Rectangle2D bounds = at.createTransformedShape(unitRect).getBounds2D();
//                BufferedImage renderedPaint = 
//                        new BufferedImage((int) Math.ceil(bounds.getWidth()), 
//                                          (int) Math.ceil(bounds.getHeight()), 
//                                           BufferedImage.TYPE_INT_ARGB);
//                Graphics2D g = (Graphics2D) renderedPaint.getGraphics();
//                g.translate(-bounds.getMinX(), -bounds.getMinY());
//                g.setPaint(paint);
//                g.fill(bounds);
//                g.dispose();
//
//                // draw the mask
//                BufferedImage mask = pdImage.getImage();
//                BufferedImage renderedMask = new BufferedImage((int) Math.ceil(bounds.getWidth()), 
//                                                               (int) Math.ceil(bounds.getHeight()), 
//                                                               BufferedImage.TYPE_INT_RGB);
//                g = (Graphics2D) renderedMask.getGraphics();
//                g.translate(-bounds.getMinX(), -bounds.getMinY());
//                AffineTransform imageTransform = new AffineTransform(at);
//                imageTransform.scale(1.0 / mask.getWidth(), -1.0 / mask.getHeight());
//                imageTransform.translate(0, -mask.getHeight());
//                g.drawImage(mask, imageTransform, null);
//                g.dispose();
//
//                // apply the mask
//                final int[] transparent = new int[4];
//                int[] alphaPixel = null;
//                WritableRaster raster = renderedPaint.getRaster();
//                WritableRaster alpha = renderedMask.getRaster();
//                int h = renderedMask.getRaster().getHeight();
//                int w = renderedMask.getRaster().getWidth();
//                for (int y = 0; y < h; y++)
//                {
//                    for (int x = 0; x < w; x++)
//                    {
//                        alphaPixel = alpha.getPixel(x, y, alphaPixel);
//                        if (alphaPixel[0] == 255)
//                        {
//                            raster.setPixel(x, y, transparent);
//                        }
//                    }
//                }
//
//                // draw the image
//                setClip();
//                graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
//                if (isContentRendered())
//                {
//                    graphics.drawImage(renderedPaint,
//                            AffineTransform.getTranslateInstance(bounds.getMinX(), bounds.getMinY()),
//                            null);
//                }
//            }
//            else
//            {
//                // fill the image with stenciled paint
//                BufferedImage image = pdImage.getStencilImage(getNonStrokingPaint());
//
//                // draw the image
//                drawBufferedImage(image, at);
//            }
//        }
//        else
//        {
//            if (subsamplingAllowed)
//            {
//                int subsampling = getSubsampling(pdImage, at);
//                // draw the subsampled image
//                drawBufferedImage(pdImage.getImage(null, subsampling), at);
//            }
//            else
//            {
//                // subsampling not allowed, draw the image
//                drawBufferedImage(pdImage.getImage(), at);
//            }
//        }
//
//        if (!pdImage.getInterpolate())
//        {
//            // JDK 1.7 has a bug where rendering hints are reset by the above call to
//            // the setRenderingHint method, so we re-set all hints, see PDFBOX-2302
//            setRenderingHints();
//        }
    }

    @Override
    public void shadingFill(COSName shadingName) throws IOException {
    	if (debugParams.showShadingFill) {
    		System.out.println("shadingFill "+shadingName);
    	}
    	super.shadingFill(shadingName);
//        PDShading shading = getResources().getShading(shadingName);
//        if (shading == null)
//        {
//            LOG.error("shading " + shadingName + " does not exist in resources dictionary");
//            return;
//        }
//        Matrix ctm = getGraphicsState().getCurrentTransformationMatrix();
//        Paint paint = shading.toPaint(ctm);
//        paint = applySoftMaskToPaint(paint, getGraphicsState().getSoftMask());
//
//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
//        graphics.setPaint(paint);
//        graphics.setClip(null);
//        lastClip = null;
//
//        // get the transformed BBox and intersect with current clipping path
//        // need to do it here and not in shading getRaster() because it may have been rotated
//        PDRectangle bbox = shading.getBBox();
//        Area area;
//        if (bbox != null)
//        {
//            area = new Area(bbox.transform(ctm));
//            area.intersect(getGraphicsState().getCurrentClippingPath());
//        }
//        else
//        {
//            area = getGraphicsState().getCurrentClippingPath();
//        }
//        if (isContentRendered())
//        {
//            graphics.fill(area);
//        }
    }

    @Override
    public void showAnnotation(PDAnnotation annotation) throws IOException {
    	if (debugParams.showAnnotation) {
    		System.out.println("showAnnotation "+
    	    format(annotation));
    	}
    	super.showAnnotation(annotation);
//        lastClip = null;
//        int deviceType = -1;
//        if (graphics.getDeviceConfiguration() != null && 
//            graphics.getDeviceConfiguration().getDevice() != null)
//        {
//            deviceType = graphics.getDeviceConfiguration().getDevice().getType();
//        }
//        if (deviceType == GraphicsDevice.TYPE_PRINTER && !annotation.isPrinted())
//        {
//            return;
//        }
//        if (deviceType == GraphicsDevice.TYPE_RASTER_SCREEN && annotation.isNoView())
//        {
//            return;
//        }
//        if (annotation.isHidden())
//        {
//            return;
//        }
//        if (annotation.isInvisible() && annotation instanceof PDAnnotationUnknown)
//        {
//            // "If set, do not display the annotation if it does not belong to one
//            // of the standard annotation types and no annotation handler is available."
//            return;
//        }
//        //TODO support NoZoom, example can be found in p5 of PDFBOX-2348
//
//        if (isHiddenOCG(annotation.getOptionalContent()))
//        {
//            return;
//        }
//       
//        PDAppearanceDictionary appearance = annotation.getAppearance();
//        if (appearance == null || appearance.getNormalAppearance() == null)
//        {
//            annotation.constructAppearances(renderer.document);
//        }
//
//        if (annotation.isNoRotate() && getCurrentPage().getRotation() != 0)
//        {
//            PDRectangle rect = annotation.getRectangle();
//            AffineTransform savedTransform = graphics.getTransform();
//            // "The upper-left corner of the annotation remains at the same point in
//            //  default user space; the annotation pivots around that point."
//            graphics.rotate(Math.toRadians(getCurrentPage().getRotation()),
//                    rect.getLowerLeftX(), rect.getUpperRightY());
//            super.showAnnotation(annotation);
//            graphics.setTransform(savedTransform);
//        }
//        else
//        {
//            super.showAnnotation(annotation);
//        }
    }


	/**
     * {@inheritDoc}
     */
    @Override
    public void showForm(PDFormXObject form) throws IOException {
    	if (debugParams.showForm) {
    		System.out.println("showForm "+format(form));
    	}
    	super.showForm(form);
//        if (isContentRendered())
//        {
//            super.showForm(form);
//        }
    }

    @Override
    public void showTransparencyGroup(PDTransparencyGroup form) throws IOException {
    	if (debugParams.showShowTransGrp) {
    		System.out.println("transpGrp "+form);
    	}
        super.showTransparencyGroup(form);
//        if (!isContentRendered())
//        {
//            return;
//        }
//        TransparencyGroup group =
//                new TransparencyGroup(form, false, getGraphicsState().getCurrentTransformationMatrix(), null);
//        BufferedImage image = group.getImage();
//        if (image == null)
//        {
//            // image is empty, don't bother
//            return;
//        }
//
//        graphics.setComposite(getGraphicsState().getNonStrokingJavaComposite());
//        setClip();
//
//        // both the DPI xform and the CTM were already applied to the group, so all we do
//        // here is draw it directly onto the Graphics2D device at the appropriate position
//        PDRectangle bbox = group.getBBox();
//        AffineTransform savedTransform = graphics.getTransform();
//
//        Matrix m = new Matrix(xform);
//        float xScale = Math.abs(m.getScalingFactorX());
//        float yScale = Math.abs(m.getScalingFactorY());
//        
//        AffineTransform transform = new AffineTransform(xform);
//        transform.scale(1.0 / xScale, 1.0 / yScale);
//        graphics.setTransform(transform);
//
//        // adjust bbox (x,y) position at the initial scale + cropbox
//        float x = bbox.getLowerLeftX() - pageSize.getLowerLeftX();
//        float y = pageSize.getUpperRightY() - bbox.getUpperRightY();
//
//        if (flipTG)
//        {
//            graphics.translate(0, image.getHeight());
//            graphics.scale(1, -1);
//        }
//        else
//        {
//            graphics.translate(x * xScale, y * yScale);
//        }
//
//        PDSoftMask softMask = getGraphicsState().getSoftMask();
//        if (softMask != null)
//        {
//            Paint awtPaint = new TexturePaint(image,
//                    new Rectangle2D.Float(0, 0, image.getWidth(), image.getHeight()));
//            awtPaint = applySoftMaskToPaint(awtPaint, softMask);
//            graphics.setPaint(awtPaint);
//            if (isContentRendered())
//            {
//                graphics.fill(
//                        new Rectangle2D.Float(0, 0, bbox.getWidth() * xScale, bbox.getHeight() * yScale));
//            }
//        }
//        else
//        {
//            if (isContentRendered())
//            {
//                try
//                {
//                    graphics.drawImage(image, null, null);
//                }
//                catch (InternalError ie)
//                {
//                    LOG.error("Exception drawing image, see JDK-6689349, " +
//                              "try rendering into a BufferedImage instead", ie);
//                }
//            }
//        }
//
//        graphics.setTransform(savedTransform);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void beginMarkedContentSequence(COSName tag, COSDictionary properties) {
    	if (debugParams.showBeginMarked) {
    		System.out.println("beginMarkedContentSequence "+tag+"/"+properties);
    	}
//
//        if (nestedHiddenOCGCount > 0)
//        {
//            nestedHiddenOCGCount++;
//            return;
//        }
//        if (tag == null || getPage().getResources() == null)
//        {
//            return;
//        }
//        if (isHiddenOCG(getPage().getResources().getProperties(tag)))
//        {
//            nestedHiddenOCGCount = 1;
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endMarkedContentSequence() {
    	if (debugParams.showEndMarked) {
    		System.out.println("endMarked");
    	}
    	super.endMarkedContentSequence();
//        if (nestedHiddenOCGCount > 0)
//        {
//            nestedHiddenOCGCount--;
//        }
    }

    // ===========================
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


}

