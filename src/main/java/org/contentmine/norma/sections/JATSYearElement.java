package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

/**
 * 		<year>1958</year>

 * @author pm286
 *
 */
public class JATSYearElement extends JATSElement implements IsInline {

	private static final Logger LOG = Logger.getLogger(JATSYearElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String TITLE = "year";
	static final String TAG = "year";

	public JATSYearElement(Element element) {
		super(element);
	}

	public static boolean matches(Element element) {
		if (element.getLocalName().equals(TITLE)) {
			LOG.trace(TITLE+": "+element.toXML());
			return true;
		}
		return false;
	}
	
	public String debugString(int level) {
		return "y: "+this.getValue();
	}
	

}
