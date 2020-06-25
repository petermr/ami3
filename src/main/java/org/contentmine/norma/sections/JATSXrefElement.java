package org.contentmine.norma.sections;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlElement;

import nu.xom.Element;

public class JATSXrefElement extends JATSElement implements IsInline {

	private static final String REF_TYPE = "ref-type";
	private static final String RID = "rid";

	/**
	<xref ref-type="aff" rid="aff1">
		<sup>1</sup>
	</xref>
	<xref rid="B27-materials-10-00045" ref-type="bibr" class="xref">27</xref>].
	<xref ref-type="bibr" rid="b15" class="xref">15</xref>
	<xref ref-type="bibr" rid="b18" class="xref">18</xref>
	<xref ref-type="fn" rid="t3-fn2" class="xref">b</xref>
	<xref ref-type="table-fn" rid="t3f2" class="xref">**</xref>
	 */
	private static final Logger LOG = LogManager.getLogger(JATSXrefElement.class);
public static final String TAG = "xref";
	
	private String refType; 
	private String rid; 

	public JATSXrefElement(Element element) {
		super(element);
		this.refType = element.getAttributeValue(REF_TYPE);
		this.rid     = element.getAttributeValue(RID); 
	}
	
	/** HTMLEquivalent
	 */
	@Override
	public HtmlElement createHTML() {
		return deepCopyAndTransform(new HtmlA());
	}

	

}
