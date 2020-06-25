package org.contentmine.cproject.files;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.CMineFixtures;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.junit.Assert;
import org.junit.Test;

public class ProjectSnippetsTreeTest {

	private static final Logger LOG = LogManager.getLogger(ProjectSnippetsTreeTest.class);
@Test
	public void testSnippetsByCTreeName() throws IOException {
		ProjectSnippetsTree projectSnippetsTree = CMineTestFixtures.createProjectSnippetsTree(new File(CMineFixtures.TEST_RESULTS_DIR, "zika"), "sequence.dnaprimer.snippets.xml");
		// [PMC4654492, PMC4671560]
		SnippetsTree snippetsTree = projectSnippetsTree.getOrCreateSnippetsTreeByCTreeName().get("PMC4654492");
		Assert.assertNotNull(snippetsTree);
		Assert.assertEquals("cTreeName", "PMC4654492", snippetsTree.getCTreeName());
		snippetsTree = projectSnippetsTree.getOrCreateSnippetsTreeByCTreeName().get("PMC4654493");
		Assert.assertNull(snippetsTree);
	}


	//==================================
	

}
