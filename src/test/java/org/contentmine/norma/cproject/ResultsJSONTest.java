package org.contentmine.norma.cproject;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CContainer;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.norma.NormaFixtures;
import org.junit.Test;

public class ResultsJSONTest {

	private static final Logger LOG = Logger.getLogger(ResultsJSONTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testSplitAndNormalize() {
		CContainer cProject = new CProject(new File(NormaFixtures.TEST_MISC_DIR, "cproject"));
		File file = cProject.getAllowedChildFile(AbstractMetadata.Type.EPMC.getCProjectMDFilename());
	}
}
