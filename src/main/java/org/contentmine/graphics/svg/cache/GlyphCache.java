package org.contentmine.graphics.svg.cache;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.fonts.SVGGlyph;

/** holds Paths which may be Glyphs.
 * Very experimental
 * 
 * @author pm286
 *
 */
public class GlyphCache extends AbstractCache{

	private static final Logger LOG = LogManager.getLogger(GlyphCache.class);
private List<SVGGlyph> glyphList;
	private String imageBoxColor;

	public List<SVGGlyph> getOrCreateGlyphList() {
		if (glyphList == null) {
			glyphList = SVGGlyph.extractSelfAndDescendantGlyphs(ownerComponentCache.inputSVGElement);
		}
		return glyphList;
	}

	public GlyphCache(ComponentCache svgStore) {
		super(svgStore);
		setDefaults();
	}
	
	public GlyphCache() {
		this(new ComponentCache());
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		return getOrCreateGlyphList();
	}
	
	private void setDefaults() {
		imageBoxColor = "pink";
	}

	public List<SVGGlyph> getGlyphList() {
		return glyphList;
	}

	/** the bounding box of the actual image components
	 * The extent of the context (e.g. svgCache) may be larger
	 * @return the bounding box of the contained image
	 */
	public Real2Range getBoundingBox() {
		return getOrCreateBoundingBox(glyphList);
	}

	@Override
	public String toString() {
		String s = "images: "+getOrCreateGlyphList().size();
		return s;
	}

	@Override
	public void clearAll() {
		superClearAll();
		glyphList = null;
	}
	
}
