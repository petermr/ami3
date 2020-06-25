package org.contentmine.norma.tagger.elsevier;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.tagger.PubstyleTagger;

public class HTMLElsevierTagger extends PubstyleTagger {


	private final static Logger LOG = LogManager.getLogger(HTMLElsevierTagger.class);

	public final static String ELSEVIER = "elsevier";
	private static final String ELSEVIER_TAGGER_DIR_RESOURCE = NAConstants.PUBSTYLE_RESOURCE+"/"+ELSEVIER;
	public static final String ELSEVIER_TAGGER_RESOURCE = ELSEVIER_TAGGER_DIR_RESOURCE+"/"+NAConstants.HTML_TAGGER_XML;

	public HTMLElsevierTagger() {
		super(ELSEVIER_TAGGER_RESOURCE);
	}

	public static String getTaggerName() {
		return ELSEVIER;
	}

}
