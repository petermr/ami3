package org.contentmine.norma.tagger.hindawi;

import org.apache.log4j.Logger;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.tagger.PubstyleTagger;

public class HTMLHindawiTagger extends PubstyleTagger {


	private final static Logger LOG = Logger.getLogger(HTMLHindawiTagger.class);

	public final static String HINDAWI = "hindawi";
	private static final String HINDAWI_TAGGER_DIR_RESOURCE = NAConstants.PUBSTYLE_RESOURCE+"/"+HINDAWI;
	public static final String HINDAWI_TAGGER_RESOURCE = HINDAWI_TAGGER_DIR_RESOURCE+"/"+NAConstants.HTML_TAGGER_XML;

	public HTMLHindawiTagger() {
		super(HINDAWI_TAGGER_RESOURCE);
	}

	public static String getTaggerName() {
		return HINDAWI;
	}



}
