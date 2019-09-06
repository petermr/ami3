package org.contentmine.norma;

import java.io.File;
import java.io.IOException;

import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore // LARGE
public class PDFIT {

	@Test
	public void testPDF2SVGLarge() throws IOException {
		File sourceDir = new File("../../projects/unesco");
		Assert.assertTrue(""+sourceDir.getCanonicalPath(), sourceDir.exists());
		new CProject().run("--project "+sourceDir+" --makeProject (\\1)/fulltext.pdf --fileFilter .*/(.*)\\.pdf");
		new Norma().run("--project " + sourceDir + " --input fulltext.pdf "+ " --outputDir " + sourceDir + " --transform pdf2svg ");
	}

	@Test
	//	@Ignore // LARGE
		// reinstated
	public void testPDF2SVG() {
		File cprojectDir = new File(NormaFixtures.TEST_MISC_DIR, "cproject");
		File targetDir = new File("target/pdfs/cproject");
		File targetDir1 = new File("target/pdfs/cproject/temp");
		CMineTestFixtures.cleanAndCopyDir(cprojectDir, targetDir);
		String cmd = "--project " + targetDir + " --input fulltext.pdf "+ " --outputDir " + targetDir1 + " --transform pdf2svg ";
		new Norma().run(cmd);
	}

}
