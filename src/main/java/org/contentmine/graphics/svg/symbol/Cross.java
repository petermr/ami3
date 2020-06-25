package org.contentmine.graphics.svg.symbol;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Cross extends AbstractSymbol {

	private final static Logger LOG = LogManager.getLogger(Cross.class);
	
	public final static String MULTIPLICATION_SIGN = "\u00D7"; 
	
	public Cross() {
		super();
		this.setUnicodeString(MULTIPLICATION_SIGN);
		this.setSymbolFill(SymbolFill.NONE);
	}
	
	@Override
	protected void setSymbolFill(SymbolFill fill) {
		// no action
	}

}
