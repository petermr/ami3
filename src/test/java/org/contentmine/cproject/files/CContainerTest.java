package org.contentmine.cproject.files;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.metadata.AbstractMetadata.Type;
import org.junit.Assert;
import org.junit.Test;

public class CContainerTest {

	private static final Logger LOG = Logger.getLogger(CContainerTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	@Test
	public void testGetAllowedChildFile() {
		CContainer cProject = new CProject(new File(CMineFixtures.TEST_PROJECTS_DIR, "project3"));
		File file = cProject.getAllowedChildFile(Type.EPMC.getCProjectMDFilename());
		Assert.assertNotNull("eupmc not null", file);
		Assert.assertNotNull("log not null", cProject.getAllowedChildFile(CContainer.LOG_XML));
		Assert.assertNull("unknown null", cProject.getUnknownChildFile("unknown.txt"));
		Assert.assertNotNull("unknown not null", cProject.getAllowedChildFile("unknown.txt"));
		Assert.assertNull("unknown null", cProject.getAllowedChildFile("junk"));
	}

}
