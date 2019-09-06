package org.contentmine.norma.plot;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class CONSORTPlotTest {
	private static final Logger LOG = Logger.getLogger(CONSORTPlotTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	
	@Test
	@Ignore // as PDF converter is broken
	public void testPDF2SVG() {
		String project = "consort";
		String ctree = "Bel2014";
		
		File sourceDir = new File(NormaFixtures.TEST_PLOT_DIR, project+"/");
		File targetDir = new File(NormaFixtures.TARGET_PLOT_DIR, project+"/");
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		File ctreeDir = new File(targetDir, ctree);

		new Norma().convertRawPDFToProjectToCompactSVG(targetDir);
		// does PDF exist?
		File pdf = new File(ctreeDir, "fulltext.pdf");
		Assert.assertTrue("exists: "+pdf, pdf.exists());
		// does SVG exist?
		File svgDir = new File(ctreeDir, "svg/");
		Assert.assertTrue("exists: "+svgDir, svgDir.exists());
		File[] ff = svgDir.listFiles();
		Assert.assertEquals("files in: "+svgDir, 9,  ff.length);
	}

}
