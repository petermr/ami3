package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlP;

import nu.xom.Element;

/**
 <graphic xlink:href="TSWJ2017-4927214.001" class="graphic" xmlns:xlink="http://www.w3.org/1999/xlink"/>
 * @author pm286
 *
 */
public class JATSGraphicElement extends JATSElement implements IsAnchor {
	private static final Logger LOG = Logger.getLogger(JATSGraphicElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

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
		HtmlP p = new HtmlP();
		p.appendChild(this.getAttributeValue("href"));
		return p;
	}


}
