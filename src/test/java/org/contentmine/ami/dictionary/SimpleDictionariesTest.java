package org.contentmine.ami.dictionary;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.AMIProcessor;
import org.contentmine.ami.tools.AMIDictionaryToolOLD;
import org.junit.Assert;
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
