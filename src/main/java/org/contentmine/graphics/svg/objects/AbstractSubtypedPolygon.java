package org.contentmine.graphics.svg.objects;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGPolygon;
import org.contentmine.graphics.svg.SVGPolyline;

public abstract class AbstractSubtypedPolygon extends SVGPolygon {
	private static final Logger LOG = Logger.getLogger(AbstractSubtypedPolygon.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	protected SVGPolyline polyline;

	protected AbstractSubtypedPolygon() {
		super();
	}
	
	protected AbstractSubtypedPolygon(SVGPolygon polygon) {
		setReal2Array(polygon.getReal2Array());
		copyAttributesFromOriginatingShape(polygon);
	}

	protected AbstractSubtypedPolygon(SVGPolyline polyline) {
		createSubtypedElementFeaturesX(polyline);
		copyAttributesFromOriginatingShape(polyline);

	}
	
	protected AbstractSubtypedPolygon(Real2Array real2Array) {
		super(real2Array);
	}

	/** size of subtyped element.
	 */
	protected abstract int getSubtypeSize();

//	protected abstract void createSubtypedElementFeatures(SVGPolyline polyline);

	/** 
	 * called by subtyped polygons
	 * @param polyline
	 */
	protected void createSubtypedElementFeaturesX(SVGPolygon polygon) {
		if (!(polygon.getLineList().size() == getSubtypeSize())) {
			throw new RuntimeException("Must have "+getSubtypeSize()+"; found "+polyline.getLineList().size());
		}
		this.polyline = polyline;
		getReal2Array();
		copyAttributesFromOriginatingShape(polygon);

	}

	/** 
	 * called by subtyped polygons
	 * @param polyline
	 */
	protected void createSubtypedElementFeaturesX(SVGPolyline polyline) {
		if (!(polyline.getLineList().size() == getSubtypeSize() && polyline.isClosed())) {
			throw new RuntimeException("Must have "+getSubtypeSize()+"; found "+polyline.getLineList().size());
		}
		this.polyline = polyline;
		getReal2Array();
		copyAttributesFromOriginatingShape(polyline);
	}


}
