package org.contentmine.cproject.args.log;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class LogElement extends AbstractLogElement {

	private static final Logger LOG = LogManager.getLogger(LogElement.class);
public final static String TAG = "log";
	
	protected LogElement() {
		super(TAG);
	}
	public LogElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
