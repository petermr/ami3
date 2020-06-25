package org.contentmine.cproject.args.log;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class WarnElement extends AbstractLogElement {

	private static final Logger LOG = LogManager.getLogger(WarnElement.class);
public final static String TAG = "warn";
	
	protected WarnElement() {
		super(TAG);
	}
	public WarnElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
