package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRange.Direction;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGRect;

/** extracts rects within graphic area.
 * 
 * @author pm286
 *
 */
public class RectCache extends AbstractCache {
	static final Logger LOG = Logger.getLogger(RectCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private double panelEps = DEFAULT_PANEL_EPS = 3.0;

	private List<SVGRect> rectList;
	private List<SVGRect> horizontalPanelList;
	private double DEFAULT_PANEL_EPS;
	
	private RectCache() {
		
	}
	
	public RectCache(ComponentCache containingComponentCache) {
		super(containingComponentCache);
		siblingShapeCache = containingComponentCache.getOrCreateShapeCache();
		rectList = siblingShapeCache.getRectList();
	}

	public List<SVGRect> getOrCreateRectList() {
		if (rectList == null) {
			rectList = siblingShapeCache == null ? null : siblingShapeCache.getRectList();
			if (rectList == null) {
				rectList = new ArrayList<SVGRect>();
			}
		}
		return rectList;
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		return getOrCreateRectList();
	}

	public List<SVGRect> getOrCreateHorizontalPanelList() {
		if (horizontalPanelList == null) {
			horizontalPanelList = new ArrayList<SVGRect>();
			getOrCreateRectList();
			RealRange xrange = getOrCreateComponentCacheBoundingBox().getRealRange(Direction.HORIZONTAL);
			for (SVGRect rect : rectList) {
				if (RealRange.isEqual(xrange, rect.getRealRange(Direction.HORIZONTAL), panelEps)) {
					horizontalPanelList.add(rect);
				}
			}
		}
		return horizontalPanelList;
	}

	@Override
	public String toString() {
		getOrCreateRectList();
		getOrCreateHorizontalPanelList();
		String s = ""
			+ "rects: "+rectList.size()+"; "
			+ "horPanels: "+horizontalPanelList.size()+"; ";
		return s;

	}

	@Override
	public void clearAll() {
		superClearAll();
		rectList = null;
		horizontalPanelList = null;
	}

}
