package org.contentmine.ami.tools.table;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.AMIFixtures;
import org.contentmine.eucl.xml.XMLUtil;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Element;

public class TTemplateListTest {
	
	private static final Logger LOG = LogManager.getLogger(TTemplateListTest.class);
public static File TEST_TABLE_DIR = new File(AMIFixtures.TEST_TOOLS_DIR, "table");
	public static File TEMPLATE_LIST_TEST = new File(TEST_TABLE_DIR, "templateList.xml");

	@Test
	public void testVariables() {
		TTemplateList templateList = TTemplateList.getOrCreateTemplateListElement(TEMPLATE_LIST_TEST);
//		LOG.debug("TT"+templateList.toXML());
		testSubstitution(templateList, "@FLOAT@", "\\-?\\d+\\.\\d+");
		testSubstitution(templateList, "@FLOAT_ERROR@", "\\-?\\d+\\.\\d+\\s*±\\s*\\-?\\d+\\.\\d+");
		testSubstitution(templateList, "@CHEMICAL@", "[A-Za-z0-9\\(][A-Za-z0-9\\.\\-\\(\\)\\s]+[A-Za-z\\)]");
		testSubstitution(templateList, "@JUNK@", "GG@GROT@HH\\-?\\d+\\.\\d+\\s*±\\s*\\-?\\d+\\.\\d+KKK");
		
	}

	private void testSubstitution(TTemplateList templateList, String string, String expected) {
		Element var = XMLUtil.getQueryElements(templateList, ".//*[@name='" + string + "']").get(0);
		Assert.assertEquals(string, expected, var.getValue());
	}
}
