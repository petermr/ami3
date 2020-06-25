package org.contentmine.norma.transform;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.junit.Assert;
import org.junit.Test;

public class TransformTest {

	

	private static final Logger LOG = LogManager.getLogger(TransformTest.class);
@Test
	public void testMissingFiles() throws Exception {
		
		File sourceDir = new File(NormaFixtures.TEST_NORMA_DIR, "transform/biserrula");
		File targetDir = new File("target/transform/biserrula");
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		String cmd = "--project "+targetDir+" -i fulltext.xml  -o scholarly.html --transform nlm2html";
		new Norma().run(cmd);
		Assert.assertNull("XML should not exist", new CTree(new File(targetDir, "PMC3907879")).getExistingFulltextXML());
		Assert.assertNull("SHTML should not exist", new CTree(new File(targetDir, "PMC3907879")).getExistingScholarlyHTML());
		Assert.assertNotNull("XML should exist", new CTree(new File(targetDir, "PMC4062634")).getExistingFulltextXML());
		Assert.assertNotNull("SHTML should exist", new CTree(new File(targetDir, "PMC4062634")).getExistingScholarlyHTML());
		// this is the problem - created when no fulltext.xml
		Assert.assertNull("XML should not exist", new CTree(new File(targetDir, "PMC4062642")).getExistingFulltextXML());
		Assert.assertNull("SHTML should not exist",new CTree(new File(targetDir, "PMC4062642")).getExistingScholarlyHTML());
		
		Assert.assertNotNull("XML should exist", new CTree(new File(targetDir, "PMC4901225")).getExistingFulltextXML());
		Assert.assertNotNull("SHTML should exist", new CTree(new File(targetDir, "PMC4901225")).getExistingScholarlyHTML());

	}
	
	@Test 
	public void testRotate() {
		File sourceDir = new File(NormaFixtures.TEST_TABLE_DIR, "rotated");
		Assert.assertTrue(sourceDir.exists());
		File targetDir = new File("target/rotate/rotated");
		LOG.debug("TARG "+targetDir);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		
		String cmd = "--project "+targetDir
				+ " --fileFilter ^.*/table\\d+/table\\.svg$"
		+ " --outputDir "+targetDir+" --transform svg2svg rotate clock90";
		new Norma().run(cmd);
	}
}
