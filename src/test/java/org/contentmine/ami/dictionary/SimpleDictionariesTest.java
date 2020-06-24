package org.contentmine.ami.dictionary;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIProcessor;
import org.junit.Test;


/** 
 * 
 * @author pm286
 *
 */
public class SimpleDictionariesTest {
	private static final Logger LOG = Logger.getLogger(SimpleDictionariesTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

//	@Test
//	public void testResources() {
//		AMIDictionary dictionaries = new AMIDictionary();
//		List<File> childPaths = dictionaries.getDictionaries();
//		Assert.assertTrue("dictionaries "+childPaths.size(), childPaths.size() > 40);
//		Assert.assertTrue("dictionaries", childPaths.toString().contains("country.xml"));
//	}
	
	@Test
	public void testAMI() {
		AMIProcessor.main(new String[] {});
		AMIProcessor.main(new String[] {"help country"});
	}
}
