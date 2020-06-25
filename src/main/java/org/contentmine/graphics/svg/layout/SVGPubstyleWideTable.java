package org.contentmine.graphics.svg.layout;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.svg.SVGElement;

/** table that spans the whole page
 * 
 * @author pm286
 *
 */
public class SVGPubstyleWideTable extends AbstractPubstyle {
	private static final Logger LOG = LogManager.getLogger(SVGPubstyleWideTable.class);
public final static String SVG_CLASSNAME = "wide.table";

	public SVGPubstyleWideTable() {
		super();
	}
	
	public SVGPubstyleWideTable(SVGElement element) {
		super(element);
	}

	@Override
	protected String getPubstyleClassName() {
		return SVG_CLASSNAME;
	}

}
