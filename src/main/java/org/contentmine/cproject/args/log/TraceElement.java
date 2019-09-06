package org.contentmine.cproject.args.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class TraceElement extends AbstractLogElement {

	private static final Logger LOG = Logger.getLogger(TraceElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TAG = "trace";
	
	protected TraceElement() {
		super(TAG);
	}
	public TraceElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
