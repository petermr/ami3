package org.contentmine.norma.sections;

import nu.xom.Attribute;
import nu.xom.Element;

/** the actual abstract in tne article
 * 
 * @author pm286
 *
 */
public class JATSAbstractElement extends JATSElement implements IsBlock, HasUrl {

	public static String TAG = "abstract";

	public JATSAbstractElement() {
		super(TAG);
	}
	
	public JATSAbstractElement(Element element) {
		super(element);
	}
	
	public JATSAbstractElement setUrl(String url) {
		this.addAttribute(new Attribute(JATSElement.URL, url));
		return this;
	}
	
	public String getUrl() {
		return this.getAttributeValue(JATSElement.URL);
	}
	
	
	

}
