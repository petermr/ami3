package org.contentmine.ami;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.CMStringUtil;

public class AMICommandLineComponent {
	private static final Logger LOG = Logger.getLogger(AMICommandLineComponent.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String id;
	private String classname;
	private String description;
	
	public AMICommandLineComponent() {
		
	}

	public AMICommandLineComponent(String id, String classname, String description) {
		this.id = id;
		this.classname = classname;
		this.description = description;
	}

	public String toString() {
		String s = CMStringUtil.addPaddedSpaces(id, 20) + " " + classname + "\n    " + description;
		s = CMStringUtil.addPaddedSpaces(id, 20) + " " + description;
		return s;
	}
}
