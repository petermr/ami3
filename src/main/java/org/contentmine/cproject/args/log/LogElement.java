package org.contentmine.cproject.args.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class LogElement extends AbstractLogElement {

	private static final Logger LOG = Logger.getLogger(LogElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TAG = "log";
	
	protected LogElement() {
		super(TAG);
	}
	public LogElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
