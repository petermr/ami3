package org.contentmine.norma.pubstyle.generic;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.norma.NormaFixtures;
import org.contentmine.norma.pubstyle.getpapers.GetPapers;
import org.junit.Assert;
import org.junit.Test;

public class GetPapersTest {

	private static final Logger LOG = LogManager.getLogger(GetPapersTest.class);
@Test
	public void testGetPapers() throws IOException{
		// for info
		String query = "getpapers --api eupmc -q 'anopheles FIRST_PDATE:[2014-06-01 TO 2014-06-07]' -x --outdir anopheles";
		// results in src/test/resources/org/contentmine/norma/pubstyle/
		File anophelesDir = new File(NormaFixtures.TEST_PUBSTYLE_DIR, "getpapers/anopheles");
		File resultsJsonFile = new File(anophelesDir, "eupmc_results.json");
		Assert.assertTrue(resultsJsonFile.exists());
		GetPapers getPapers = new GetPapers();
		getPapers.setJsonPath(GetPapers.PMCID);
	    getPapers.mapJsonArrayToFiles(anophelesDir, resultsJsonFile);

	}

}
