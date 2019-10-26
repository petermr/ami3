package org.contentmine.norma.sections;

import nu.xom.Element;

public class JATSObjectIdElement extends JATSElement implements IsInline {

	/**
<object-id pub-id-type="doi">10.1371/journal.pntd.0001477.t001</object-id>
		
	 */
	static String TAG = "object-id";
	private static final String PUB_ID_TYPE = "pub-id-type";

	public JATSObjectIdElement(Element element) {
		super(element);
	}

	@Override
	protected String getAttributeString() {
		return getAttributeString(PUB_ID_TYPE);
	}
	
	public String debugString(int level) {
		return "{"+this.getValue()+"}";
	}

}
