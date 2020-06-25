package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGPolyline;

/** extracts polylines within graphic area.
 * 
 * @author pm286
 *
 */
public class PolylineCache extends AbstractCache {
	static final Logger LOG = LogManager.getLogger(PolylineCache.class);
private double panelEps = DEFAULT_PANEL_EPS = 3.0;

	private List<SVGPolyline> polylineList;
	private double DEFAULT_PANEL_EPS;
	
	public PolylineCache() {
		this(new ComponentCache());
	}
	
	public PolylineCache(ComponentCache containingComponentCache) {
		super(containingComponentCache);
		siblingShapeCache = containingComponentCache.getOrCreateShapeCache();
		if (siblingShapeCache == null) {
			throw new RuntimeException("null siblingShapeCache");
		}
		polylineList = siblingShapeCache.getPolylineList();
	}

	public List<SVGPolyline> getOrCreatePolylineList() {
		if (polylineList == null) {
			polylineList = siblingShapeCache == null ? null : siblingShapeCache.getPolylineList();
			if (polylineList == null) {
				polylineList = new ArrayList<SVGPolyline>();
			}
		}
		return polylineList;
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		return getOrCreatePolylineList();
	}

	@Override
	public String toString() {
		getOrCreatePolylineList();
		String s = ""
			+ "polylines: "+polylineList.size();
		return s;

	}

	@Override
	public void clearAll() {
		superClearAll();
		polylineList = null;
	}

}
