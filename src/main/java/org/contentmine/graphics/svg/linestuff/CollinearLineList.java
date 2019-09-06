package org.contentmine.graphics.svg.linestuff;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGLine.LineDirection;

/** lines (strictly segments) all falling on a common (axial) line.
 * i.e. __ _____ ___
 * or the same vertically
 * 
 * cf LadderLineList which is | | | ||  |
 * 
 * @author pm286
 *
 */
public class CollinearLineList extends AxialLineList {
	private static final Logger LOG = Logger.getLogger(CollinearLineList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public CollinearLineList(LineDirection direction) {
		super(direction);
	}

}
