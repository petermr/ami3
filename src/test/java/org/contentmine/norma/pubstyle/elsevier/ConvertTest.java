package org.contentmine.norma.pubstyle.elsevier;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.junit.Ignore;
import org.junit.Test;

@Ignore // too long for tests
public class ConvertTest {

	private static final Logger LOG = LogManager.getLogger(ConvertTest.class);
@Test
	public void testConvert() throws IOException {
		File project = new File(NormaFixtures.TEST_PUBSTYLE_DIR, "elsevier/tgac0");
		File target = new File("target/elsevier/tgac0");
		FileUtils.copyDirectory(project, target);
		LOG.debug(target);
		String args = "--project "+target+" -i fulltext.pdf -o fulltext.pdf.html --transform pdf2html";
		Norma norma = new Norma();
		norma.run(args);
		
	}
	
}
