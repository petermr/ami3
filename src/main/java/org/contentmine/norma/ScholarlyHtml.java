package org.contentmine.norma;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;

public class ScholarlyHtml {

	
	private static final Logger LOG = Logger.getLogger(ScholarlyHtml.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private HtmlElement html;
	
	public ScholarlyHtml() {
		
	}
}
