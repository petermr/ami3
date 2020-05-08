package org.contentmine.pdf2svg2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.PDLineDashPattern;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDTransparencyGroup;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.graphics.svg.SVGClipPath;
import org.contentmine.graphics.svg.SVGDefs;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;

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
	static final Logger LOG = Logger.getLogger(PageParserTwo.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String CLIP_PATH = "clipPath";
	private static final Graphics2D TEXT = null;
	private static final String SVG_RHMARGIN = "svg_rhmargin";
	//	private SVGG svgg;
	private double yEpsilon = 0.05; // guess
	private double scalesEpsilon = 0.1; // guess
	private int nPlaces = 3;
	private double angleEps = 0.005;
	
	private String clipString;
	private Set<String> clipStringSet;
	// defs
	private SVGElement defs1;
	private Point2D currentPoint;

	PageParserTwo(PageDrawerParameters parameters, int iPage, AMIDebugParameters debugParams) throws IOException        {
        super(parameters, debugParams);
        init();
    	this.pageSerial = PageSerial.createFromZeroBasedPage(iPage);
    }

    void init() {
    	super.init();
    	LOG.trace("created parserPageDrawer");

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

	// ONE
	

	//==== TEXT ==========    

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


//    // ONE + ZERO
//    @Override
//    public void drawImage(PDImage pdImage) throws IOException    {
//    	super.drawImage(pdImage);
//    	extractImage(pdImage);
//
//    }

    // This doesn't mark the canvas so not used
    @Override
    public void appendRectangle(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
    	if (debugParams.showAppendRectangle) {
    		System.out.println(">appRect ["+format(p0, ndec)+"/"+format(p1, ndec)+"/"+format(p2, ndec)+"/"+format(p3, ndec)+"]");
    	}
    	super.appendRectangle(p0, p1, p2, p3);
    	
    }

// ===== PATHS ========
    
    @Override
    public void clip(int windingRule) {
    	if (debugParams.showClip) {System.out.println("clip("+windingRule+")");}
    	super.clip(windingRule);
    }


    // ============ UTILITIES ============


	// THESE ARE ALL UNUSED BUT MIGHT BE USEFUL
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

	
}
