package org.contentmine.ami.dictionary.species;

import java.io.File;
import java.io.InputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.norma.NAConstants;


/** from taxdump
 * 
 * @author pm286
 *
 */
public class TaxDumpGenusDictionary extends DefaultAMIDictionary {

	private static final String TAXDUMP = "taxdump";
	private static final Logger LOG = Logger.getLogger(TaxDumpGenusDictionary.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static File TAXDUMP_DIR = new File(SPECIES_DIR, TAXDUMP);
	private final static File TAXDUMP_XML_FILE = new File(TAXDUMP_DIR, "taxdumpGenus.xml");
	
	
	public TaxDumpGenusDictionary() {
		init();
	}
	
	private void init() {
		readTAXDUMPXML();
	}

	private void readTAXDUMPXML() {
		ClassLoader cl = getClass().getClassLoader();
		InputStream TAXDUMP_XML_RES = cl.getResourceAsStream(NAConstants.AMI_RESOURCE + "/plugins/species/taxdump/taxdumpGenus.xml");
		readDictionary(TAXDUMP_XML_RES);
	}


}
