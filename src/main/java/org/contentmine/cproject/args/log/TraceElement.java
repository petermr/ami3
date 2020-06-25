package org.contentmine.cproject.args.log;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class TraceElement extends AbstractLogElement {

	private static final Logger LOG = LogManager.getLogger(TraceElement.class);
public final static String TAG = "trace";
	
	protected TraceElement() {
		super(TAG);
	}
	public TraceElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
