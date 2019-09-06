package org.contentmine.graphics.svg.path;

import java.awt.geom.GeneralPath;

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.svg.SVGPathPrimitive;

public class CubicPrimitive extends SVGPathPrimitive {

	public final static String TAG = "C";

	public CubicPrimitive(Real2Array coordArray) {
		if (coordArray == null || coordArray.size() != 3) {
			throw new RuntimeException("Bad coordArray: "+coordArray);
		}
		this.coordArray = coordArray;
	}

	public String getTag() {
		return TAG;
	}
	
	@Override
	public void operateOn(GeneralPath path) {
		if (coordArray != null) {	
			Real2 coord0 = coordArray.elementAt(0);
			Real2 coord1 = coordArray.elementAt(1);
			Real2 coord2 = coordArray.elementAt(2);
			path.curveTo(coord0.x, coord0.y, coord1.x, coord1.y, coord2.x, coord2.y);
		}
	}

	public String toString() {
		String s = TAG;
		for (int i = 0; i < coordArray.size(); i++) {
			Real2 coord = coordArray.get(i);
			s += formatCoords(coord);
		}
		return s;
	}
	
	@Override
	/**
	 * @return angle between start-control1 and control1-end
	 */
	public Angle getAngle() {
		Angle angle = null;
		if (zerothCoord != null) {
			Vector2 vector01 = new Vector2(coordArray.elementAt(0).subtract(zerothCoord));
			Vector2 vector12 = new Vector2(coordArray.elementAt(2).subtract(coordArray.elementAt(1)));
			angle = vector12.getAngleMadeWith(vector01);
		}
		return angle;
	}

}
