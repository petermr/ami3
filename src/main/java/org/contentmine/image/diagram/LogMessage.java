package org.contentmine.image.diagram;

import nu.xom.Attribute;
import nu.xom.Element;

public class LogMessage extends Element {

	private static final String IDREF = "idref";

	public LogMessage() {
		super("message");
	}
	
	public LogMessage(Exception e) {
		this();
		this.appendChild(e.toString());
	}
	
	public LogMessage(Exception e, String idref) {
		this(e);
		this.setIdref(idref);
	}
	
	public void setIdref(String idref) {
		this.addAttribute(new Attribute(IDREF, idref));
	}
	
	public String getIdref() {
		return this.getAttributeValue(IDREF);
	}
}
