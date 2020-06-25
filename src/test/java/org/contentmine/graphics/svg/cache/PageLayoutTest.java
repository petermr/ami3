package org.contentmine.graphics.svg.cache;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Real2Range;
import org.junit.Test;

import junit.framework.Assert;

public class PageLayoutTest {
	private static final Logger LOG = LogManager.getLogger(PageLayoutTest.class);
@Test
	public void testCreatePageLayout() {
		String resource = PageLayout.AMSOCGENE_RESOURCE+"all.svg";
		LOG.debug("resource: "+resource);
		PageLayout pageLayout = PageLayout.readPageLayoutFromResource(resource);
		Assert.assertNotNull(pageLayout);
		Real2Range mediabox = pageLayout.getMediaBox();
//		Assert.assertEquals("media",  "((0.0,520.0),(0.0,700.0))", mediabox.toString());
		LOG.warn("Incomplete test");
		
	}
}
