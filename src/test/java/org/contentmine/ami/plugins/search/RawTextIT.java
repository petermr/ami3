package org.contentmine.ami.plugins.search;

import java.io.File;
import java.io.IOException;

import org.contentmine.ami.plugins.AbstractSearchArgProcessor;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.Norma;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("long")
public class RawTextIT {

	@Test
	public void testSearchCochranePDF() throws IOException {
		File test = new File(NAConstants.TEST_AMI_DIR, "word/sentences/cochrane3");
		File target = new File("target/normapdf");
		File project = new File(target, "junk");
		CMineTestFixtures.cleanAndCopyDir(test, target);
		String cmd = "-i "+target.toString()+" -e pdf -o "+project+" --ctree";
		Norma norma = new Norma();
		norma.run(cmd);
		
		cmd = "--ctree "+project.toString()+" -i fulltext.pdf --transform pdf2txt -o fulltext.pdf.txt";
		norma = new Norma();
		norma.run(cmd);
		
		cmd = "--project "+project+" -i fulltext.pdf.txt --sr.search "
				+ "searchwords/cochrane.xml -o junk.txt";
		AbstractSearchArgProcessor argProcessor = new SearchArgProcessor(cmd);
		argProcessor.runAndOutput();
	}

}
