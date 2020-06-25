package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlCaption;
import org.contentmine.graphics.html.HtmlElement;

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
	private static final Logger LOG = LogManager.getLogger(JATSFigElement.class);
public static String TAG = "caption";

	public JATSCaptionElement(Element element) {
		super(element);
	}

	@Override
	public HtmlElement createHTML() {
		HtmlCaption caption = new HtmlCaption();
		caption.appendChild(this.getValue());
		return caption;
	}


}
