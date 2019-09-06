package org.contentmine.cproject.args.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class InfoElement extends AbstractLogElement {

	private static final Logger LOG = Logger.getLogger(InfoElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String TAG = "info";
	
	protected InfoElement() {
		super(TAG);
	}
	public InfoElement(String msg) {
		this();
		this.addMessage(msg);
	}

}
