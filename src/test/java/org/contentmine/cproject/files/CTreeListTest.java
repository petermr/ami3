package org.contentmine.cproject.files;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.cproject.CMineFixtures;
import org.junit.Assert;
import org.junit.Test;


public class CTreeListTest {

	private static final Logger LOG = Logger.getLogger(CTreeListTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testFileOrder() {
		CProject cProject = new CProject(CMineFixtures.TEST_SAMPLE);
		CTreeList cTreeList = cProject.getOrCreateCTreeList();
//		LOG.debug(cTreeList.getCTreeDirectoryList());
		String SAMPLE_DIR = ""+CHESConstants.SRC_TEST_RESOURCES + "/" + CHESConstants.ORG_CM_CPROJECT + "/download/sample";
		Assert.assertEquals("ctrees",  "["
				+ "src/test/resources/org/contentmine/cproject/download/sample/PMC4678086,"
				+ " src/test/resources/org/contentmine/cproject/download/sample/PMC4686705,"
				+ " src/test/resources/org/contentmine/cproject/download/sample/http_dx.doi.org_10.3161_15081109acc2016.18.1.017,"
				+ " src/test/resources/org/contentmine/cproject/download/sample/http_dx.doi.org_10.3324_haematol.2016.148015,"
				+ " src/test/resources/org/contentmine/cproject/download/sample/http_dx.doi.org_10.4000_geocarrefour.9765"
				+ "]",
				cTreeList.getCTreeDirectoryList().toString());
	}
}
