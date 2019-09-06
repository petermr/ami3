package org.contentmine.graphics.svg.rule.horizontal;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.html.HtmlB;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlI;
import org.contentmine.graphics.html.HtmlSub;
import org.contentmine.graphics.html.HtmlSup;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.text.build.PhraseChunk;
import org.contentmine.graphics.svg.text.build.SusType;

import nu.xom.Attribute;

/** chunks in a TextLine such as Phrases and Blanks
 * 
 * @author pm286
 *
 */
public abstract class LineChunk extends SVGG implements HorizontalElement {
	
	private static final String TRUE = "true";
	private static final Logger LOG = Logger.getLogger(LineChunk.class);

	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String SUPERSCRIPT = "superscript";
	private static final String SUBSCRIPT = "subscript";
	protected static final String SPACE = " ";
	protected static final double SPACE_OFFSET = 1.0;
	protected static final String NULL_SPACE = "";
	protected static final double SPACE_OFFSET1 = -0.5;

	public LineChunk() {
		super();
	}
	
	public LineChunk(SVGG e) {
		super(e);
	}

	public Real2Range getOrCreateBoundingBox() {
		if (boundingBox == null) {
			boundingBox = getBoundingBox();
		}
		return boundingBox;
	}

	public Real2 getXY() {
		Real2Range bbox = this.getBoundingBox();
		return bbox == null ? null : bbox.getLLURCorners()[0];
	}

	public Double getX() {
		Real2 xy = this.getXY();
		return xy == null ? null : xy.getX();
	}

	public Double getY() {
		Real2 xy = this.getXY();
		return xy == null ? null : xy.getY();
	}

	public String getFontFamily() {
		String s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() > 0) {
			s = childChunks.get(0).getFontFamily();
			for (int i = 1; i < childChunks.size(); i++) {
				String ss = childChunks.get(i).getFontFamily();
				if (s == null) {
					ss = s;
				} else if (!s.equals(ss)) {
					LOG.trace("Font Family changed "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	public Double getFontSize() {
		Double s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() > 0) {
			s = childChunks.get(0).getFontSize();
			for (int i = 1; i < childChunks.size(); i++) {
				Double ss = childChunks.get(i).getFontSize();
				if (s == null) {
					ss = s;
				} else if (!s.equals(ss)) {
					LOG.trace("Font Size changed "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	public String getFontWeight() {
		String s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() > 0) {
			s = childChunks.get(0).getFontWeight();
			for (int i = 1; i < childChunks.size(); i++) {
				String ss = childChunks.get(i).getFontWeight();
				if (s == null) {
					ss = s;
				} else if (!s.equals(ss)) {
					LOG.trace("Font Weight changed "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	public String getFontStyle() {
		String s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() > 0) {
			s = childChunks.get(0).getFontStyle();
			for (int i = 1; i < childChunks.size(); i++) {
				String ss = childChunks.get(i).getFontStyle();
				if (s == null) {
					ss = s;
				} else if (!s.equals(ss)) {
					LOG.trace("Font Style changed "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	public String getFill() {
		String s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() > 0) {
			s = childChunks.get(0).getFill();
			for (int i = 1; i < childChunks.size(); i++) {
				String ss = childChunks.get(i).getFill();
				if (s == null) {
					ss = s;
				} else if (!s.equals(ss)) {
					LOG.trace("Fill "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	public String getStroke() {
		String s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() > 0) {
			s = childChunks.get(0).getStroke();
			for (int i = 1; i < childChunks.size(); i++) {
				String ss = childChunks.get(i).getStroke();
				if (s == null) {
					ss = s;
				} else if (!s.equals(ss)) {
					LOG.trace("Stroke "+ss+" => "+s);
				}
			}
		}
		return s;
	}
	
	public boolean isBold() {
		String s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() == 0) return false;
		for (int i = 0; i < childChunks.size(); i++) {
			if (!childChunks.get(i).isBold()) return false;
		}
		return true;
	}
	
	public boolean isItalic() {
		String s = null;
		List<? extends LineChunk> childChunks = getChildChunks();
		if (childChunks.size() == 0) return false;
		for (int i = 0; i < childChunks.size(); i++) {
			if (!childChunks.get(i).isItalic()) return false;
		}
		return true;
	}
	
	protected abstract List<? extends LineChunk> getChildChunks();

	public void setSuperscript(boolean superscript) {
		if (superscript) {
			this.addAttribute(new Attribute(SUPERSCRIPT, TRUE));
		} else {
			this.removeAttribute(SUPERSCRIPT);
			this.removeAttribute(SUBSCRIPT);
		}
	}

//	public void removeAttribute(String attName) {
//		Attribute attribute = this.getAttribute(attName);
//		if (attribute != null) {
//			this.removeAttribute(attribute);
//		}
//	}

	public void setSubscript(boolean subscript) {
		if (subscript) {
			this.addAttribute(new Attribute(SUBSCRIPT, TRUE));
		} else {
			this.removeAttribute(SUPERSCRIPT);
			this.removeAttribute(SUBSCRIPT);
		}
	}
	
	public boolean hasSuperscript() {
		return TRUE.equals(this.getAttributeValue(SUPERSCRIPT));
	}

	public boolean hasSubscript() {
		return TRUE.equals(this.getAttributeValue(SUBSCRIPT));
	}

	public void setSuscript(SusType susType, boolean onoff) {
		if (SusType.SUB.equals(susType)) {
			this.setSubscript(onoff);
		} else if (SusType.SUPER.equals(susType)) {
			this.setSuperscript(onoff);
		}
	}

	public boolean shouldAddSpaceBefore(LineChunk chunk) {
		boolean addSpace = false;
		if (chunk == null || chunk.getOrCreateBoundingBox() == null || getOrCreateBoundingBox() == null) return false;
		double deltax = Real.normalize(chunk.getOrCreateBoundingBox().getXMin() - getOrCreateBoundingBox().getXMax(), 1);
		if (deltax < PhraseChunk.SPACE_OFFSET1) {
			addSpace = false;
		} else if (deltax > PhraseChunk.SPACE_OFFSET) {
			addSpace = true;
		} else {
			addSpace = false;
		}
		return addSpace;
	}

	protected HtmlElement addSuscriptsAndStyle(HtmlElement span) {
		if (hasSubscript()) {
			HtmlSub sub = new HtmlSub();
			span.appendChild(sub);
			span = sub;
		}
		if (hasSuperscript()) {
			HtmlSup sup = new HtmlSup();
			span.appendChild(sup);
			span = sup;
		}
		if (isBold()) {
			HtmlB b = new HtmlB();
			span.appendChild(b);
			span = b;
		}
		if (isItalic()) {
			HtmlI it = new HtmlI();
			span.appendChild(it);
			span = it;
		}
		return span;
	}
}
