package org.contentmine.cproject.args.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class ErrorElement extends AbstractLogElement {

	private static final Logger LOG = Logger.getLogger(ErrorElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TAG = "error";
	
	protected ErrorElement() {
		super(TAG);
	}
	public ErrorElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
