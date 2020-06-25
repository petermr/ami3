package org.contentmine.cproject.args.log;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class ErrorElement extends AbstractLogElement {

	private static final Logger LOG = LogManager.getLogger(ErrorElement.class);
public final static String TAG = "error";
	
	protected ErrorElement() {
		super(TAG);
	}
	public ErrorElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
