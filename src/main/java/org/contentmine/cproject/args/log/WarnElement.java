package org.contentmine.cproject.args.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class WarnElement extends AbstractLogElement {

	private static final Logger LOG = Logger.getLogger(WarnElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TAG = "warn";
	
	protected WarnElement() {
		super(TAG);
	}
	public WarnElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
