package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import nu.xom.Element;

public class JATSSubjGroupElement extends JATSElement implements IsBlock {

	private static final String ROLE = "role";
	private static final String CONTRIB_TYPE = "contrib-type";

	/**
<subj-group subj-group-type="heading">
<subject>Research Article</subject>
</subj-group>
	 */
	final static String TAG = "subj-group";
	final static String SUBJ_GROUP_TYPE = "subj-group-type";

	public JATSSubjGroupElement(Element element) {
		super(element);
	}

	@Override 
	public String getAttributeString() {
		return getAttributeString(SUBJ_GROUP_TYPE);
	}
}
