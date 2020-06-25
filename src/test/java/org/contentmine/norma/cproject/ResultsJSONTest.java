package org.contentmine.norma.cproject;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CContainer;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.norma.NormaFixtures;
import org.junit.Test;

public class ResultsJSONTest {

	private static final Logger LOG = LogManager.getLogger(ResultsJSONTest.class);
@Test
	public void testSplitAndNormalize() {
		CContainer cProject = new CProject(new File(NormaFixtures.TEST_MISC_DIR, "cproject"));
		File file = cProject.getAllowedChildFile(AbstractMetadata.Type.EPMC.getCProjectMDFilename());
	}
}
