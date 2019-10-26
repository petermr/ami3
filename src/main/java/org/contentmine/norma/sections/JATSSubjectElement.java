package org.contentmine.norma.sections;

import java.util.Arrays;
import java.util.List;

import nu.xom.Element;

public class JATSSubjectElement extends JATSElement implements IsInline {

	/**
<subj-group subj-group-type="heading">
<subject>Research Article</subject>
</subj-group>
	 */
	final static String TAG = "subject";

	public JATSSubjectElement(Element element) {
		super(element);
	}

}
