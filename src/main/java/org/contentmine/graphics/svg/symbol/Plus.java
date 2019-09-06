package org.contentmine.graphics.svg.symbol;

import org.apache.log4j.Logger;

public class Plus extends AbstractSymbol {

	private final static Logger LOG = Logger.getLogger(Plus.class);
	
	public final static String PLUS_SIGN = "+"; 
	
	public Plus() {
		super();
		this.setUnicodeString(PLUS_SIGN);
		this.setSymbolFill(SymbolFill.NONE);
	}
	
	@Override
	protected void setSymbolFill(SymbolFill fill) {
		// no action
	}

}
