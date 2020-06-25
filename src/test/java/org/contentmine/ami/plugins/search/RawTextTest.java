package org.contentmine.ami.plugins.search;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.norma.NAConstants;
import org.junit.Test;

public class RawTextTest {

	;
	private static final Logger LOG = LogManager.getLogger(RawTextTest.class);

@Test
	public void testSplitSentences() {
		File test = new File(NAConstants.TEST_AMI_DIR, "word/sentences");
		String cmd = "--ctree "+test.toString()+" -i simple.txt --sr.search "
				+ "searchwords/prepositions.xml -o junk.txt";
		AbstractSearchArgProcessor argProcessor = new SearchArgProcessor(cmd);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testSearchCochrane() {
		File test = new File(NAConstants.TEST_AMI_DIR, "word/sentences/cochrane1");
		String cmd = "--ctree "+test.toString()+" -i fulltext.pdf.txt --sr.search "
				+ "searchwords/cochrane.xml -o junk.txt";
		AbstractSearchArgProcessor argProcessor = new SearchArgProcessor(cmd);
		argProcessor.runAndOutput();
	}
	
	@Test
	public void testSearchCochraneProject() {
		File test = new File(NAConstants.TEST_AMI_DIR, "word/sentences");
		String cmd = "--project "+test.toString()+" -i fulltext.pdf.txt --sr.search "
				+ "searchwords/cochrane.xml -o junk.txt";
		AbstractSearchArgProcessor argProcessor = new SearchArgProcessor(cmd);
		argProcessor.runAndOutput();
	}
	
}
