package org.contentmine.graphics.svg.path;

import java.awt.geom.GeneralPath;

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGPathPrimitive;

public class MovePrimitive extends SVGPathPrimitive {

	public final static String TAG = "M";

	public MovePrimitive(Real2 real2) {
		this.coordArray = new Real2Array();
		coordArray.add(real2);
	}

	public String getTag() {
		return TAG;
	}
	
	@Override
	public void operateOn(GeneralPath path) {
		if (coordArray != null) {	
			Real2 coord = coordArray.elementAt(0);
			path.moveTo(coord.x, coord.y);
		}
	}

	public String toString() {
		return TAG + formatCoords(coordArray.get(0));
	}
	
	@Override
	/**
	 * @return null
	 */
	public Angle getAngle() {
		return null;
	}

}
