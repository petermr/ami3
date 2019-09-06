package org.contentmine.graphics.svg.symbol;

import org.apache.log4j.Logger;

public class Square extends AbstractSymbol {

	private final static Logger LOG = Logger.getLogger(Square.class);
	
	public final static String WHITE_SQUARE = "\u25a1";
	public final static String BLACK_SQUARE = "\u25a0";
	
	public Square() {
		super();
		this.setUnicodeString(WHITE_SQUARE);
		this.setSymbolFill(SymbolFill.NONE);
	}
	
	@Override
	/** sets fill type.
	 * 
	 * also changes Unicode where possible.
	 * 
	 * @param fill
	 */public void setSymbolFill(SymbolFill fill) {
		super.setSymbolFill(fill);
		if (SymbolFill.ALL.equals(fill)) {
			this.setUnicodeString(BLACK_SQUARE);
		} else if (SymbolFill.NONE.equals(fill)) {
			this.setUnicodeString(WHITE_SQUARE);
		}
	}
	
}
