package org.contentmine.norma.image.ocr;

import java.awt.image.BufferedImage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGImage;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;

/** holds the output of GOCR combined with the underlying image.
 * requires parentG to have children
 *     SVGRect (from GOCR), 
 *     SVGText (with GOCR char)
 *     SVGImage (clipped to fit box)
 * 
 * May be extended to HOCR output as well (?AbstractOCRCharBox)
 * @author pm286
 *
 */
public class CharBox {

	
	private static final Logger LOG = Logger.getLogger(CharBox.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Int2 bboxSize;
	private SVGG parentG;
	private SVGText svgText;
	private SVGRect svgRect;
	private Int2Range boundingBox;
	private SVGImage svgImage;

	private CharBox() {
	}

	/** create from <g> that has svgText, svgRect, and svgImage children
	 * 
	 * @param parentG
	 * @return
	 */
	public static CharBox createFrom(SVGG parentG) {
		CharBox charBox = null;
		if (parentG != null) {
			SVGText svgText = (SVGText) XMLUtil.getSingleElement(parentG, "*[local-name()='"+SVGText.TAG+"']");
			SVGRect svgRect = (SVGRect) XMLUtil.getSingleElement(parentG, "*[local-name()='"+SVGRect.TAG+"']");
			SVGImage svgImage = (SVGImage) XMLUtil.getSingleElement(parentG, "*[local-name()='"+SVGImage.TAG+"']");
			if (svgText != null && svgRect != null) {
				charBox = new CharBox();
				charBox.parentG = parentG;
				charBox.svgText = svgText;
				charBox.svgRect = svgRect;
				charBox.svgImage = svgImage;
				charBox.boundingBox = svgRect.createIntBoundingBox();
			} else {
				LOG.debug(parentG.toXML());
				throw new RuntimeException("need rect and text children");
			}
		}
		return charBox;
	}

	 
	public Int2 getBoundingBoxSize() {
		bboxSize = new Int2(boundingBox.getXRange().getRange(), boundingBox.getYRange().getRange());
		return bboxSize;
	}

	public SVGG getParentG() {
		return parentG;
	}

	public SVGText getSvgText() {
		return svgText;
	}

	public SVGRect getSvgRect() {
		return svgRect;
	}

	public Int2Range getBoundingBox() {
		return boundingBox;
	}

	public SVGImage getSvgImage() {
		return svgImage;
	}
	
	public BufferedImage getBufferedImage() {
		return svgImage == null ? null : svgImage.getBufferedImage();
	}
	
	public String toString() {
		String s = getText() + " " + boundingBox;
		return s;
	}

	private String getText() {
		return svgText == null ? null : svgText.getText();
	}

	IntRange getXRange() {
		Int2Range boundingBox = getBoundingBox();
		return boundingBox == null  ? null : boundingBox.getXRange();
	}
	
	IntRange getYRange() {
		Int2Range boundingBox = getBoundingBox();
		return boundingBox == null  ? null : boundingBox.getYRange();
	}
	
	public Integer getXMin() {
		IntRange xrange = getXRange();
		return xrange == null ? null : xrange.getMin();
	}
	
	public Integer getXMax() {
		IntRange xrange = getXRange();
		return xrange == null ? null : xrange.getMax();
	}
	
	public Integer getYMin() {
		IntRange yrange = getYRange();
		return yrange == null ? null : yrange.getMin();
	}
	
	public Integer getYMax() {
		IntRange yrange = getYRange();
		return yrange == null ? null : yrange.getMax();
	}
	
	public Integer getWidth() {
		IntRange xrange = getXRange();
		return xrange == null ? null : xrange.getRange();
	}
	
	public Integer getHeight() {
		IntRange yrange = getYRange();
		return yrange == null ? null : yrange.getRange();
	}

	public String getBestCharacter() {
		String s = getText();
		if (s == null || "null".equals(String.valueOf(s))) s = /*String.valueOf((char)127)*/ "?";
		int comma = s.indexOf(",");
		if (comma != -1) s = s.substring(0, comma);
		return s;
	}
	
}
