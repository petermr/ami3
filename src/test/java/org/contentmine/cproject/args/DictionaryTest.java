package org.contentmine.cproject.args;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.contentmine.CHESConstants;
import org.contentmine.cproject.CProjectArgProcessor;
import org.contentmine.cproject.lookup.DefaultStringDictionary;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Ignore // moved to AMI
public class DictionaryTest {

	private static final Logger LOG = Logger.getLogger(DictionaryTest.class);
	static {
		LOG.setLevel(org.apache.log4j.Level.DEBUG);
	}

	@Test
	public void testDictionaries() throws IOException {
		DefaultArgProcessor argProcessor = new CProjectArgProcessor();
		File targetFile = new File("target/test/log/");
		targetFile.mkdirs();
		// dummy file
		FileUtils.write(new File(targetFile, "fulltext.txt"), "fulltext");
		argProcessor.parseArgs("-q "+targetFile+" -i fulltext.txt  "
				+ "--c.dictionary src/test/resources/" + CHESConstants.ORG_CM_CPROJECT + "/files/testDictionary.xml "
				+ "               /" + CHESConstants.ORG_CM_CPROJECT + "/files/testDictionary2.xml");
		List<DefaultStringDictionary> dictionaryList = argProcessor.getDictionaryList();
		Assert.assertEquals("dictionaries", 2, dictionaryList.size());
		DefaultStringDictionary dictionary0 = dictionaryList.get(0);
		Map<String, List<List<String>>> listsByWord0 = dictionary0.getTrailingWordsByLeadWord();
		Assert.assertEquals("list0", 2, listsByWord0.size());
		Assert.assertEquals("single", "[[]]", listsByWord0.get("bar").toString());
		DefaultStringDictionary dictionary1 = dictionaryList.get(1);
		Map<String, List<List<String>>> listsByWord1 = dictionary1.getTrailingWordsByLeadWord();
		Assert.assertEquals("list0", 3, listsByWord1.size());
		Assert.assertEquals("single", "[[], [a], [b, c]]", listsByWord1.get("bar2").toString());
		Assert.assertEquals("single", "[[], [plugh2]]", listsByWord1.get("foo2").toString());
		Assert.assertEquals("single", "[[]]", listsByWord1.get("jumbo").toString());
		Assert.assertNull("single", listsByWord1.get("junk"));
	}

}
