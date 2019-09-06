package org.contentmine.graphics.svg.path;

import java.awt.geom.GeneralPath;

import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGPathPrimitive;

public class UnknownPrimitive extends SVGPathPrimitive {

	private String TAG = "?";

	public UnknownPrimitive(char cc) {
		this.TAG = String.valueOf(cc);
	}

	public String getTag() {
		return TAG;
	}
	
	public String toString() {
		return TAG;
	}

	@Override
	public void operateOn(GeneralPath path) {
		throw new RuntimeException("Cannot create path");
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
