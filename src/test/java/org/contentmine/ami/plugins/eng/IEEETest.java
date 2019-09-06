package org.contentmine.ami.plugins.eng;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.word.WordArgProcessor;
import org.contentmine.ami.wordutil.WordSetWrapper;
import org.contentmine.norma.NAConstants;
import org.junit.Ignore;
import org.junit.Test;

/**
 * queries the IEEE API.
 * 
 * @author pm286
 *
 */
public class IEEETest {

	@Test
	// TESTED
	public void testReadIEEE() throws IOException {
		String ENG = NAConstants.TEST_AMI_DIR+"/engineering/ieee_1196402_html";
		FileUtils.copyDirectory(new File(ENG), new File("target/eng/"));
		String args = 
				"-q target/eng/ -i scholarly.html --w.words "+WordArgProcessor.WORD_FREQUENCIES +
				" --w.stopwords "+WordSetWrapper.COMMON_ENGLISH_STOPWORDS_TXT;
			AMIArgProcessor argProcessor = new WordArgProcessor(args);
			argProcessor.runAndOutput();
			AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
					"<results title=\"frequencies\">"
					+  "<result title=\"frequency\" word=\"link\" count=\"162\" />"
					+  "<result title=\"frequency\" word=\"FSOW\" count=\"117\" />"
					+  "<result title=\"frequency\" word=\"wireless\" count=\"80\" />"
					+  "<result title=\"frequency\" word=\"optical\" count=\"66\" />"
					+  "<result title=\"frequency\" word=\"hybrid\" count=\"55\" />"
					+  "<resu" //...
					);
	}
	
	@Test
	// TESTED
	@Ignore
	public void testReadIEEEPDF() throws IOException {
//		String ENG = "NAConstants.TEST_AMI_DIR+"/engineering/01268283";
//		FileUtils.copyDirectory(new File(ENG), new File("target/eng/01268283/"));
//		Norma norma = new Norma();
//		// note output must be fulltext.pdf.txt
//		String args = 
//				"-q target/eng/01268283/ -i fulltext.pdf -o fulltext.pdf.txt --transform pdf2txt";
//		norma.run(args);
//		args = 
//				"-q target/eng/01268283/ -i fulltext.txt --w.words "+WordArgProcessor.WORD_FREQUENCIES +
//				  " --w.stopwords "+WordTest.STOPWORDS_TXT  ;
//			WordArgProcessor argProcessor = new WordArgProcessor(args);
//			argProcessor.runAndOutput();
//			AMIFixtures.checkResultsElementList(argProcessor, 1, 0, 
//					"<results title=\"frequencies\">"
//					+   "<result title=\"frequency\" word=\"MIM\" count=\"62\" />"
//					+   "<result title=\"frequency\" word=\"spectral\" count=\"37\" />"
//					+   "<result title=\"frequency\" word=\"TPV\" count=\"33\" />"
//					+   "<result title=\"frequency\" word=\"temperature\" count=\"31\" />"
//					+   "<result title=\"frequency\" word=\"control\" count=\"27\" />"
//					+   "<res" // ...
//					);

	}
	
}
