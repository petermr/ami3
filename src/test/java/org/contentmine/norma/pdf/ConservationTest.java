package org.contentmine.norma.pdf;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CProject;
import org.junit.Test;

import junit.framework.Assert;

public class ConservationTest {
	private static final Logger LOG = LogManager.getLogger(ConservationTest.class);
@Test
	// may fail test
	public void testConservation() throws Exception {
		File sourceDir = new File("/Users/pm286/workspace/projects/sutherland/testCopy");
		if (!sourceDir.exists()) return;
		CProject cProject = new CProject(sourceDir);
		cProject.convertPDF2SVG();
		Assert.assertNotNull(cProject);
		cProject.tidyImages();
	}


}
