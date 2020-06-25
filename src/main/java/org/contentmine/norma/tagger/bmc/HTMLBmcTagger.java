package org.contentmine.norma.tagger.bmc;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.tagger.PubstyleTagger;

public class HTMLBmcTagger extends PubstyleTagger {

	private final static Logger LOG = LogManager.getLogger(HTMLBmcTagger.class);

	public final static String BMC = "bmc";
	
	private static final String BMC_TAGGER_RESOURCE = NAConstants.PUBSTYLE_RESOURCE+"/"+BMC;
	public static final String BMC_TAGGER_HTML = BMC_TAGGER_RESOURCE+"/"+"htmlTagger.xml";

	public HTMLBmcTagger() {
		super(BMC_TAGGER_HTML);
	}

	public static String getTaggerName() {
		return BMC;
	}

}
