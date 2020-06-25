package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

/**
 <graphic xlink:href="TSWJ2017-4927214.001" class="graphic" xmlns:xlink="http://www.w3.org/1999/xlink"/>
 * @author pm286
 *
 */
public class JATSGraphicElement extends JATSElement implements IsAnchor {
	private static final Logger LOG = LogManager.getLogger(JATSGraphicElement.class);
static String TAG = "graphic";

//	public final static List<String> ALLOWED_CHILD_NAMES = Arrays.asList(new String[] {
//	});
	
//	@Override
//	protected List<String> getAllowedChildNames() {
//		return ALLOWED_CHILD_NAMES;
//	}

	public JATSGraphicElement(Element element) {
		super(element);
	}

	@Override
	public HtmlElement createHTML() {
		HtmlDiv div = new HtmlDiv();
		div.setClassAttribute(this.TAG);
		div.appendChild(this.getAttributeValue("href"));
		return div;
	}


}
