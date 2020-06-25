package org.contentmine.ami.tools.template;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import nu.xom.Element;

public class TemplateElement extends AbstractTemplateElement {


	private static final Logger LOG = LogManager.getLogger(TemplateElement.class);
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
