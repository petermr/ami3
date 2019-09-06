package org.contentmine.graphics.svg.layout;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;

/** table that spans the whole page
 * 
 * @author pm286
 *
 */
public class SVGPubstyleWideTable extends AbstractPubstyle {
	private static final Logger LOG = Logger.getLogger(SVGPubstyleWideTable.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
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
