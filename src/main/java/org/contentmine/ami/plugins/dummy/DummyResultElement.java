package org.contentmine.ami.plugins.dummy;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.ResultElement;

public class DummyResultElement extends ResultElement {

	private static final Logger LOG = Logger.getLogger(DummyResultElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public DummyResultElement() {
		super();
	}
	
	public DummyResultElement(String title) {
		super(title);
	}
	
	
	
	
}
