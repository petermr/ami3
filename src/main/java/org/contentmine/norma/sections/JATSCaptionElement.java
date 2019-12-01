package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlP;

import nu.xom.Element;

/**
 * 
 * @author pm286
 * 
<caption>
<p>The antimicrobial activity of thyme oil, at different amounts, expressed as a mean inhibition 
zone for each of the nine repeated measurements
</p>
</caption>

 */
public class JATSCaptionElement extends JATSElement implements IsBlock {
	private static final Logger LOG = Logger.getLogger(JATSFigElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static String TAG = "caption";

	public JATSCaptionElement(Element element) {
		super(element);
	}

	@Override
	public HtmlElement createHTML() {
		HtmlP p = new HtmlP();
		p.appendChild(this.getValue());
		return p;
	}


}
