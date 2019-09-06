package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.fonts.StyleRecordSet;
import org.contentmine.graphics.svg.math.SVGMath;
import org.contentmine.graphics.svg.text.SVGTextLineList;

/** creates maths from primitives and other caches
 * Most of the material will be from the sibling textCache
 * 
 * @author pm286
 *
 */
public class MathCache extends AbstractCache {
	static final Logger LOG = Logger.getLogger(MathCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<SVGText> horizontalTextList;
	private List<SVGMath> mathList;
	private StyleRecordSet horizontalStyleRecordSet;
	private SVGTextLineList textLineList;
	
	private MathCache() {
		
	}
	
	public MathCache(ComponentCache containingComponentCache) {
		super(containingComponentCache);
		siblingTextCache = containingComponentCache.getOrCreateTextCache();
		if (siblingTextCache == null) {
			throw new RuntimeException("null siblingTextCache");
		}
		horizontalTextList = siblingTextCache.getOrCreateHorizontalTextListSortedY();
		// 
	}

	public List<SVGMath> getOrCreateMathList() {
		if (mathList == null) {
			mathList = new ArrayList<SVGMath>();
		}
		return mathList;
	}


	public List<? extends SVGElement> getOrCreateElementList() {
		return getOrCreateMathList();
	}

	@Override
	public String toString() {
		getOrCreateMathList();
		String s = ""
		+ "text: "+horizontalTextList.size()+"\n"
		+ "math: "+mathList.size();
		return s;

	}

	@Override
	public void clearAll() {
		superClearAll();
		mathList = null;
	}

	public SVGTextLineList createTextLineList() {
		textLineList = siblingTextCache.getTextLinesForLargestFont();
		return textLineList;
	}



}
