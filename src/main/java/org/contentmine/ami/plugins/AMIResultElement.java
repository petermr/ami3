package org.contentmine.ami.plugins;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ResultElement;

public class AMIResultElement extends ResultElement {

	private static final Logger LOG = Logger.getLogger(AMIResultElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public AMIResultElement() {
		super();
	}
	
	public AMIResultElement(String title) {
		super(title);
	}
	
	
	
	
}
