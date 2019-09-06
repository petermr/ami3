package org.contentmine.norma.pubstyle.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.norma.NormaFixtures;
import org.contentmine.norma.pubstyle.util.XMLCleaner;
import org.junit.Test;

public class EmptyCleanerTest {

	@Test
	public void testCleanEmpty() throws Exception {
		HtmlElement html = new HtmlFactory().parse(new File(NormaFixtures.TEST_PUBSTYLE_DIR, "util/cup_scholarly.html"));
		XMLCleaner cleaner = new XMLCleaner(html);
		cleaner.remove("//*[local-name()='div' and normalize-space(.)='']");
		FileUtils.write(new File("target/cleaner/cup.html"), cleaner.getElement().toXML());
	}
}
