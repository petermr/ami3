package org.contentmine.svg2xml.table;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGG;

/** manages column heading and overbars
 * ColumnHeader may have parent ColumnHeader and child ColumnHeaders
 * These are not represented by XML containment at this stage
 * 
 * @author pm286
 *
 */
public class ColumnHeader extends SVGG {
	private static final Logger LOG = LogManager.getLogger(ColumnHeader.class);
}
