package org.contentmine.norma.sections;

import nu.xom.Element;

public class JATSJournalTitleElement extends JATSElement implements IsInline {

	public static String TAG = "journal-title";

	public JATSJournalTitleElement() {
		super(TAG);
	}

	public JATSJournalTitleElement(Element element) {
		super(element);
	}


}
