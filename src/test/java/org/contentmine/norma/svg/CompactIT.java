package org.contentmine.norma.svg;

import java.io.File;

import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.junit.Ignore;
import org.junit.Test;

public class CompactIT {

	/** converts a directory with PDF files into Ctrees with compact svg.
	 * 
	 * SHOWCASE
	 */
	@Test
	// LONG
	@Ignore("FIXME")
	public void testPDFToCompactSVG() {
		Norma norma = new Norma();
		File projectDir = new File(NormaFixtures.TEST_DEMOS_DIR, "cert");
		File targetDir = new File("target/demos/cert/");
		CMineTestFixtures.cleanAndCopyDir(projectDir, targetDir);
		norma.convertRawPDFToProjectToCompactSVG(targetDir);
	}

}
