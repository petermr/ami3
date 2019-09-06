package org.contentmine.norma.pdf;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.junit.Test;

import junit.framework.Assert;

public class ConservationTest {
	private static final Logger LOG = Logger.getLogger(ConservationTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

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
