package org.contentmine.ami.dictionary.synbio;

import java.io.File;
import java.io.InputStream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.norma.NAConstants;

/** simple list of possible synbio terms

 */
public class SynbioDictionary extends DefaultAMIDictionary {

	private static final Logger LOG = LogManager.getLogger(SynbioDictionary.class);
private final static File SYNBIO_XML_FILE = new File(SYNBIO_DIR, "synbio.xml");
	private final static File SYNBIO0_XML_FILE = new File(SYNBIO_DIR, "synbio0.xml");
	
	public SynbioDictionary() {
		init();
	}
	
	private void init() {
		ClassLoader cl = getClass().getClassLoader();
		InputStream SYNBIORES = cl.getResourceAsStream(NAConstants.PLUGINS_SYNBIO+"/synbio.xml");
		readDictionary(SYNBIORES);
	}


}
