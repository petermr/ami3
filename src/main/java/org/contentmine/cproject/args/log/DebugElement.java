package org.contentmine.cproject.args.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class DebugElement extends AbstractLogElement {

	private static final Logger LOG = Logger.getLogger(DebugElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TAG = "debug";
	
	protected DebugElement() {
		super(TAG);
	}
	public DebugElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
