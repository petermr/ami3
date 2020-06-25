package org.contentmine.norma.svg;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.Norma;
import org.contentmine.norma.NormaFixtures;
import org.junit.Test;

import junit.framework.Assert;

/** test compacting SVG
 * using --transform compactsvg
 * @author pm286
 *
 */
public class CompactTest {
private static final Logger LOG = LogManager.getLogger(CompactTest.class);
@Test
	// FIXME TEST
	public void testCompact() {
		String cprojectName = "singleTreeSingleFigure";
		File sourceDir = new File(NormaFixtures.TEST_PLOT_DIR, cprojectName);
		Assert.assertTrue(""+sourceDir +"exists", sourceDir.exists());
		File targetDir = new File("target/compact", cprojectName);
		CMineTestFixtures.cleanAndCopyDir(sourceDir, targetDir);
		String cmd = "--project "+targetDir+" --fileFilter .*/figures/figure(\\d+)/figure.svg --outputDir "+targetDir+" --transform compactsvg";
		File inputFile = new File(targetDir, "ctree1/figures/figure1/figure.svg");
		Assert.assertEquals("input file ", 48389, FileUtils.sizeOf(inputFile));
		new Norma().run(cmd);
		File outputFile = new File(targetDir, "ctree1/figures/figure1/figure.svg");
		long size = FileUtils.sizeOf(outputFile);
//		Assert.assertEquals("new file "+size, 28150, size);
	}
	
}
