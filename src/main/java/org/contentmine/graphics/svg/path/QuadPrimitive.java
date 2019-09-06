package org.contentmine.graphics.svg.path;

import java.awt.geom.GeneralPath;

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.Vector2;
import org.contentmine.graphics.svg.SVGPathPrimitive;

public class QuadPrimitive extends SVGPathPrimitive {

	public final static String TAG = "Q";

	protected QuadPrimitive() {
		
	}
	
	public QuadPrimitive(Real2Array coordArray) {
		if (coordArray == null || coordArray.size() != 2) {
			throw new RuntimeException("Bad coordArray (must be 2 points): "+coordArray);
		}
		this.coordArray = coordArray;
	}

	public String getTag() {
		return TAG;
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
	public void operateOn(GeneralPath path) {
		if (coordArray != null) {	
			Real2 coord0 = coordArray.elementAt(0);
			Real2 coord1 = coordArray.elementAt(1);
			path.quadTo(coord0.x, coord0.y, coord1.x, coord1.y);
		}
	}

	@Override
	/**
	 * @return angle between start-control1 and control1-end
	 */
	public Angle getAngle() {
		Angle angle = null;
		if (zerothCoord != null) {
			Vector2 vector0 = new Vector2(coordArray.elementAt(0).subtract(zerothCoord));
			Vector2 vector1 = new Vector2(coordArray.elementAt(1).subtract(coordArray.elementAt(0)));
			angle = vector1.getAngleMadeWith(vector0);
		}
		return angle;
	}


}
