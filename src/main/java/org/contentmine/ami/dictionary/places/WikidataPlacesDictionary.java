package org.contentmine.ami.dictionary.places;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.dictionary.DefaultAMIDictionary;
import org.contentmine.eucl.xml.XMLUtil;


/** from taxdump
 * 
 * @author pm286
 *
 */
public class WikidataPlacesDictionary extends DefaultAMIDictionary {
	
	private static final Logger LOG = Logger.getLogger(WikidataPlacesDictionary.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private final static File WIKIPLACES_RAW_FILE = new File(PLACES_DIR, "raw.txt.xml");
	private final static File WIKIPLACES_XML_FILE = new File(PLACES_DIR, "wikiplaces.xml");
	private String raw;
	
	
	public WikidataPlacesDictionary() {
		init();
	}
	
	private void init() {
		readWIKIPLACESXML();
	}

	private void readWIKIPLACESXML() {
		if (!WIKIPLACES_XML_FILE.exists()) {
			readWikiplacesRaw();
			// read text file
		} else {
			readDictionary(WIKIPLACES_XML_FILE);
		}
	}

	private void readWikiplacesRaw() {
		try {
//			raw = FileUtils.readFileToString(WIKIPLACES_RAW_FILE, Charset.forName("UTF-8"));
//			String[] lines = raw.split("<");
//			String xmlString = StringUtils.join(Arrays.asList(lines), "\n<");
//			File xmlFile = new File("target/wikiplaces/raw.txt.xml");
//			FileUtils.write(xmlFile, xmlString);
//			XMLUtil.parseXML(xmlString);
			XMLUtil.parseQuietlyToDocument(WIKIPLACES_RAW_FILE);
		} catch (Exception e) {
			throw new RuntimeException("cannot read raw wikiplaces", e);
		}
	}


}
