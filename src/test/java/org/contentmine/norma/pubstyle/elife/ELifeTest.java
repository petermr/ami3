package org.contentmine.norma.pubstyle.elife;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.norma.NormaFixtures;
import org.junit.Test;

public class ELifeTest {
	private static final Logger LOG = Logger.getLogger(ELifeTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testParsingError() throws FileNotFoundException, IOException {
		File elifeXML = new File(NormaFixtures.TEST_ELIFE_DIR, "e04407/fulltext.xml");
		XMLUtil.parseQuietlyToDocument(new FileInputStream(elifeXML));
		// not valid XML
//		File elifeHtml = new File(NormaFixtures.TEST_ELIFE_DIR, "e04407/fulltext.html");
//		XMLUtil.parse(new FileInputStream(elifeHtml));
	}

}
