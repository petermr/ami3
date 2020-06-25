package org.contentmine.cproject.args.log;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class DebugElement extends AbstractLogElement {

	private static final Logger LOG = LogManager.getLogger(DebugElement.class);
public final static String TAG = "debug";
	
	protected DebugElement() {
		super(TAG);
	}
	public DebugElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
