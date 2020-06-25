package org.contentmine.ami;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.util.CMStringUtil;

public class AMICommandLineComponent {
	private static final Logger LOG = LogManager.getLogger(AMICommandLineComponent.class);
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
