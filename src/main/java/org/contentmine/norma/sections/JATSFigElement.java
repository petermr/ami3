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
 *<fig id="F1" position="float">
<label>Fig. 1</label>
<caption>
<p>The antimicrobial activity of thyme oil, at different amounts, expressed as a mean inhibition 
zone for each of the nine repeated measurements
</p>
</caption>
<graphic xlink:href="JMedLife-07-56-g001"/>
</fig>

 */
public class JATSFigElement extends JATSElement implements IsBlock, IsFloat, HasTitle {
	private static final Logger LOG = Logger.getLogger(JATSFigElement.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	static String TAG = "fig";

	public JATSFigElement(Element element) {
		super(element);
	}

	public JATSCaptionElement getCaption() {
		return (JATSCaptionElement) getSingleChild(JATSCaptionElement.TAG);
	}

	public JATSGraphicElement getGraphic() {
		return (JATSGraphicElement) getSingleChild(JATSGraphicElement.TAG);
	}

	public JATSLabelElement getLabel() {
		return (JATSLabelElement) getSingleChild(JATSLabelElement.TAG);
	}

	public String debugString(int level) {
		String caption = XMLUtil.getSingleValue(this, 
				"./*[local-name()='caption']/*[local-name()='title']");
		return caption == null ? "" : "[FIG: "+caption+"]";
	}

	@Override
	public String directoryName() {
		return TAG;
	}

	@Override
	public String generateTitle() {
		return this.getSingleChildValue(JATSLabelElement.TAG);
	}

	@Override
	public HtmlElement createHTML() {
		HtmlP p = new HtmlP();
		JATSCaptionElement caption = getCaption();
		if (caption != null) {
			p.appendChild(caption.createHTML());
			p.appendChild(" "); 
		}
		JATSGraphicElement graphic = getGraphic();
		if (graphic != null) {
			p.appendChild(graphic.createHTML());
			p.appendChild(" "); 
		}
		JATSLabelElement label = getLabel();
		if (label != null) {
			p.appendChild(label.createHTML());
			p.appendChild(" "); 
		}
		
		return p;
	}
}
