package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGPolygon;

/** extracts polygons within graphic area.
 * 
 * @author pm286
 *
 */
public class PolygonCache extends AbstractCache {
	static final Logger LOG = LogManager.getLogger(PolygonCache.class);
private double panelEps = DEFAULT_PANEL_EPS = 3.0;

	private List<SVGPolygon> polygonList;
//	private List<SVGRhomb> rhombList;
//	private List<SVGTriangle> triangleList;
	private double DEFAULT_PANEL_EPS;
	
	public PolygonCache() {
		this(new ComponentCache());
	}
	
	public PolygonCache(ComponentCache containingComponentCache) {
		super(containingComponentCache);
		siblingShapeCache = containingComponentCache.getOrCreateShapeCache();
		polygonList = siblingShapeCache.getPolygonList();
	}

	public List<SVGPolygon> getOrCreatePolygonList() {
		if (polygonList == null) {
			polygonList = siblingShapeCache == null ? null : siblingShapeCache.getPolygonList();
			if (polygonList == null) {
				polygonList = new ArrayList<SVGPolygon>();
			}
		}
		return polygonList;
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		return getOrCreatePolygonList();
	}

	@Override
	public String toString() {
		getOrCreatePolygonList();
		String s = ""
			+ "polygons: "+polygonList.size();
		return s;

	}

	@Override
	public void clearAll() {
		superClearAll();
		polygonList = null;
	}

//	public List<SVGRhomb> getOrCreateRhombList() {
//		if (rhombList == null) {
//			rhombList = new ArrayList<SVGRhomb>();
//		}
//		LOG.debug("RL "+rhombList.size());
//		return rhombList;
//	}
//
//	public List<SVGTriangle> getOrCreateTriangleList() {
//		if (triangleList == null) {
//			triangleList = new ArrayList<SVGTriangle>();
//		}
//		return triangleList;
//	}

}
