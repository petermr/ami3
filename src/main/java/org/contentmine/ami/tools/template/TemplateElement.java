package org.contentmine.ami.tools.template;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

public class TemplateElement extends AbstractTemplateElement {


	private static final Logger LOG = Logger.getLogger(TemplateElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public static final String TAG = "template";

	public TemplateElement() {
		super(TAG);
	}

	@Override
	public void process() {
//		System.out.println(">> Processing templateElement");
		super.process();
	}
	
}
