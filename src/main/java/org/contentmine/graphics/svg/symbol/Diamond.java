package org.contentmine.graphics.svg.symbol;

import org.apache.log4j.Logger;

public class Diamond extends AbstractSymbol {

	private final static Logger LOG = Logger.getLogger(Diamond.class);
	
	public final static String BLACK_DIAMOND = "\u25c6";
	public final static String WHITE_DIAMOND = "\u25c7";
	
	public Diamond() {
		super();
		this.setUnicodeString(WHITE_DIAMOND);
		this.setSymbolFill(SymbolFill.NONE);
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
			this.setUnicodeString(BLACK_DIAMOND);
		} else if (SymbolFill.NONE.equals(fill)) {
			this.setUnicodeString(WHITE_DIAMOND);
		}
	}
	
}
