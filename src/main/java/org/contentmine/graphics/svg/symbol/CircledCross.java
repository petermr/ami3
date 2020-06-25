package org.contentmine.graphics.svg.symbol;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CircledCross extends AbstractSymbol {

	private final static Logger LOG = LogManager.getLogger(CircledCross.class);
	
	public final static String N_ARY_CIRCLED_TIMES_OPERATOR = "\u2A02"; 
	
	public CircledCross() {
		super();
		this.setUnicodeString(N_ARY_CIRCLED_TIMES_OPERATOR);
		this.setSymbolFill(SymbolFill.NONE);
	}
	
	@Override
	protected void setSymbolFill(SymbolFill fill) {
		// no action
	}

}
