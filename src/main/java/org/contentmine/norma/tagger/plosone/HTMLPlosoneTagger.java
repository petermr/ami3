package org.contentmine.norma.tagger.plosone;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.tagger.PubstyleTagger;

public class HTMLPlosoneTagger extends PubstyleTagger {

	private final static Logger LOG = LogManager.getLogger(HTMLPlosoneTagger.class);

	public final static String PLOSONE = "plosone";
	private static final String PLOSONE_TAGGER_DIR_RESOURCE = NAConstants.PUBSTYLE_RESOURCE+"/"+PLOSONE;
	public static final String PLOSONE_TAGGER_RESOURCE = PLOSONE_TAGGER_DIR_RESOURCE+"/"+"htmlTagger.xml";

	public HTMLPlosoneTagger() {
		super(PLOSONE_TAGGER_RESOURCE);
	}

	public static String getTaggerName() {
		return PLOSONE;
	}

}
