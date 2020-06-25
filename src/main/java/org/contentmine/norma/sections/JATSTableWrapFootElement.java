package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlTfoot;

import nu.xom.Element;

/**
 * 
 * @author pm286
 * 
 * <table-wrap id="pntd-0001477-t001" position="float">
...
<table-wrap-foot>
<fn id="nt101">
<label/>
<p>&#x0002a;Earliest report, indicates either the first virus isolation or the first report of seroprevalence.</p>
</fn>
<fn id="nt102">
<p>
<bold>&#x02020;:</bold> Seroprevalence was either determined by one or more of the following methods: Haemagglution inhibition, neutralization, complement-fixation, IgG and/or IgM ELISA. Of note, it is possible due to antigenic cross-reactivity among flaviviruses that seropostive individuals may have been previously exposed to one or more flaviviruses and not to Zika virus.</p>
</fn>
<fn id="nt103">
<p>
<bold>&#x02021;:</bold> Viral RNA sequenced from four patients (Lanciotti et al. 2008).</p>
</fn>
</table-wrap-foot>
</table-wrap>
 *
 */
public class JATSTableWrapFootElement extends JATSElement implements IsBlock {

	private static final Logger LOG = LogManager.getLogger(JATSFigElement.class);
static String TAG = "table-wrap-foot";
	
	public JATSTableWrapFootElement(Element element) {
		super(element);
	}

	/** creates a tfoot
	 * @return HtmlTFoot
	 */
	@Override
	public HtmlElement createHTML() {
		HtmlTfoot tfoot = new HtmlTfoot();
		for (Element child : this.getChildElementList()) {
			HtmlElement childHtml = ((JATSElement)child).createHTML();
			tfoot.appendChild(childHtml);
		}
		return tfoot;
	}

}
