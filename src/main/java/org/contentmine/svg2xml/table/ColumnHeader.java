package org.contentmine.svg2xml.table;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;

/** manages column heading and overbars
 * ColumnHeader may have parent ColumnHeader and child ColumnHeaders
 * These are not represented by XML containment at this stage
 * 
 * @author pm286
 *
 */
public class ColumnHeader extends SVGG {
	private static final Logger LOG = Logger.getLogger(ColumnHeader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	

}
