package org.contentmine.norma.tagger.plosone;

import org.apache.log4j.Logger;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.tagger.PubstyleTagger;

public class XMLPlosoneTagger extends PubstyleTagger {

	private final static Logger LOG = Logger.getLogger(XMLPlosoneTagger.class);

	public final static String PLOSONE = "plosone";
	private static final String PLOSONE_RESOURCE = NAConstants.PUBSTYLE_RESOURCE+"/"+PLOSONE;
	public static final String PLOSONE_TAGDEFINITIONS_RESOURCE = PLOSONE_RESOURCE+"/"+NAConstants.HTML_TAGGER_XML;

	public XMLPlosoneTagger() {
		super(PLOSONE_TAGDEFINITIONS_RESOURCE);
	}

	public static String getTaggerName() {
		return PLOSONE;
	}

}
