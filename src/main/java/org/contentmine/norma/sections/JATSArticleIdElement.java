package org.contentmine.norma.sections;

import nu.xom.Attribute;
import nu.xom.Element;

public class JATSArticleIdElement extends JATSElement implements IsInline {

	public final static String TAG = "article-id";

	public static final String DOI = "doi";
	public static final String PMID = "pmid";
	public static final String PMCID = "pmcid";
	public static final String PUB_ID_TYPE = "pub-id-type";

	public JATSArticleIdElement() {
		super(TAG);
	}

	public JATSArticleIdElement(Element element) {
		super(element);
	}


}
