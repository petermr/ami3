package org.contentmine.cproject.args.log;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class InfoElement extends AbstractLogElement {

	private static final Logger LOG = LogManager.getLogger(InfoElement.class);
public final static String TAG = "info";
	
	protected InfoElement() {
		super(TAG);
	}
	public InfoElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
