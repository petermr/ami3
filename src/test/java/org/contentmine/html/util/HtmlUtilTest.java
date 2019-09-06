package org.contentmine.html.util;

import org.apache.log4j.Logger;
import org.contentmine.graphics.html.util.HtmlUtil;
import org.junit.Assert;
import org.junit.Test;

public class HtmlUtilTest {

	private final static Logger LOG = Logger.getLogger(HtmlUtilTest.class);
	
	@Test
	public void testStripAttributeFromText() {
		
		String s = "<a onclick=\"popup('http://www.biomedcentral.com/1471-2229/14/10/figure/F1','',800,470); return false;\" "
				+ "href=\"http://www.biomedcentral.com/1471-2229/14/10/figure/F1\">1</a>A,B).";
		Assert.assertEquals("before", 175, s.length());
		String ss = HtmlUtil.stripAttributeFromText(s, "onclick");
		Assert.assertEquals("after", 76, ss.length());
		Assert.assertEquals("stripped", "<a  href=\"http://www.biomedcentral.com/1471-2229/14/10/figure/F1\">1</a>A,B).", ss);
	}
	
	@Test
	public void testStripTwoAttributesFromText() {
		
		String s = "<a foo=\"bar\" zub=\"foo\"/><c>zub</c><b xyzzy=\"plugh\" foo=\"goo\" q=\"p\"/>";
		Assert.assertEquals("before", 68, s.length());
		String ss = HtmlUtil.stripAttributeFromText(s, "foo");
		Assert.assertEquals("after", 50, ss.length());
		Assert.assertEquals("stripped", "<a  zub=\"foo\"/><c>zub</c><b xyzzy=\"plugh\"  q=\"p\"/>", ss);
	}
}
