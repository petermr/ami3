package org.contentmine.graphics.svg.linestuff;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGLine.LineDirection;

/** equals length lines aligned as a Ladder
 * i.e
 * __
 * __
 * 
 * __
 * 
 * or the same vertically
 * | | | ||  |
 * 
 * Note LineDirection applies to the LINE direction NOT the ladder direction.
 * A vertical ladder has LineDirection.HORIZONTAL
 * 
 * @author pm286
 *
 */
public class LadderLineList extends AxialLineList {
	private static final Logger LOG = LogManager.getLogger(LadderLineList.class);
public LadderLineList(LineDirection direction) {
		super(direction);
	}

}
