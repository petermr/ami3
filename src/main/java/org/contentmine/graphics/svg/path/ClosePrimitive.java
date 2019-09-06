package org.contentmine.graphics.svg.path;

import java.awt.geom.GeneralPath;

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGPathPrimitive;

/**
 * supports 'Z' command
 * @author pm286
 *
 */
public class ClosePrimitive extends SVGPathPrimitive {

	public final static String TAG = "Z";

	public ClosePrimitive() {
	}
	
	public ClosePrimitive(Real2 real2) {
		this.coordArray = new Real2Array();
		if (real2 != null) {
			coordArray.add(real2);
		} else {
			coordArray = null;
		}
	}

	public String getTag() {
		return TAG;
	}
	
	@Override
	public void operateOn(GeneralPath path) {
		path.closePath();
	}
	
	public String toString() {
		return TAG;
	}
	
	@Override
	/**
	 * @return null
	 */
	public Angle getAngle() {
		return null;
	}

	@Override
	/**
	 * @return null
	 *
	 */
	public Real2 getTranslation() {
		return null;
	}


}
