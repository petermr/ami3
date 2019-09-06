package org.contentmine.graphics.svg.symbol;

import org.apache.log4j.Logger;

public class Triangle extends AbstractSymbol {

	private final static Logger LOG = Logger.getLogger(Triangle.class);

	public final static String WHITE_UP_POINTING_TRIANGLE = "\u25b3";
	public final static String BLACK_UP_POINTING_TRIANGLE = "\u25b2";

	public Triangle() {
		super();
	}

	@Override
	/** sets fill type.
	 * 
	 * also changes Unicode where possible.
	 * 
	 * @param fill
	 */
	protected void setSymbolFill(SymbolFill fill) {
		super.setSymbolFill(fill);
		if (SymbolFill.ALL.equals(fill)) {
			this.setUnicodeString(BLACK_UP_POINTING_TRIANGLE);
		} else if (SymbolFill.NONE.equals(fill)) {
			this.setUnicodeString(WHITE_UP_POINTING_TRIANGLE);
		}
	}
	
	

}
