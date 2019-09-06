package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;

public abstract class PageComponentCache extends ComponentCache {
	private static final Logger LOG = Logger.getLogger(PageComponentCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static Double OPACITY = 0.3;
	public static String YELLOW = "yellow";
	
	protected PageCache pageCache;
	protected Real2Range boundingBox;
	
	protected PageComponentCache() {
		super();
	}

	protected void setPageCache(PageCache pageCache) {
		this.pageCache = pageCache;
	}

	protected List<SVGElement> getOrCreateAllElementList() {
		if (allElementList == null) {
			allElementList = new ArrayList<SVGElement>();
		}
		return allElementList;
	}
	
	@Override
	public SVGElement getOrCreateConvertedSVGElement() {
		convertedSVGElement = new SVGG();
		if (allElementList != null) {
			for (AbstractCMElement element : allElementList) {
				convertedSVGElement.appendChild(element.copy());
			}
		}
		SVGRect rect = SVGRect.createFromReal2Range(boundingBox);
		if (rect != null) {
			convertedSVGElement.appendChild(rect);
			rect.setFill(getFill());
			rect.setOpacity(OPACITY);
		}
		return convertedSVGElement;
	}
	
	public String getFill() {
		return YELLOW;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append("bbox: "+(boundingBox == null ? null : boundingBox.toString()));
		return sb.toString();
	}

}
