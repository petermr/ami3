package org.contentmine.norma.biblio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.contentmine.norma.NAConstants;
import org.junit.Assert;
import org.junit.Test;

public class RISParserTest {

	@Test
	public void testRISParser() throws FileNotFoundException, IOException {
		RISParser parser = new RISParser();
		parser.read(new FileInputStream(new File(NAConstants.TEST_NORMA_DIR, "biblio/ris/Citations.txt")));
		List<RISEntry> bibChunks = parser.getEntries();
		Assert.assertEquals(20,  bibChunks.size());
	}
	
}
