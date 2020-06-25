package org.contentmine.ami.plugins.dummy;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.ResultElement;

public class DummyResultElement extends ResultElement {

	private static final Logger LOG = LogManager.getLogger(DummyResultElement.class);
public DummyResultElement() {
		super();
	}
	
	public DummyResultElement(String title) {
		super(title);
	}
	
	
	
	
}
