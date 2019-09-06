package org.contentmine.ami.plugins.word;

import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.ami.plugins.AMIPlugin;
import org.contentmine.ami.plugins.word.WordArgProcessor;
import org.contentmine.norma.NAConstants;
import org.junit.Test;

public class Prototype {

	@Test
	public void testBoWIEEE() {
		String cmd = "-q ../cproject/target/ieee/musti/Henniger/ -i fulltext.txt --w.words wordFrequencies "
				+ "--w.stopwords "+NAConstants.AMI_WORDUTIL+"/stopwords.txt "
				+ "   " +NAConstants.PLUGINS_WORD+"/clinicaltrials200.txt";		
		AMIArgProcessor amiArgProcessor = new WordArgProcessor();
		amiArgProcessor.parseArgs(cmd);
		amiArgProcessor.runAndOutput();
		
	}
	
}
