package org.contentmine.html.util;

import org.apache.log4j.Logger;
import org.contentmine.graphics.html.util.HTMLTagReplacement;
import org.junit.Assert;
import org.junit.Test;

public class HtmlTagReplacementTest {

	private final static Logger LOG = Logger.getLogger(HtmlTagReplacementTest.class);
	
	@Test
	public void testReplace() {
		StringBuilder sb = new StringBuilder("<html><tag>foo</tag><bar/><tag a=\"b\">boo</tag> and <tag c=\"d\"/> end</html>");
		HTMLTagReplacement replace = new HTMLTagReplacement("tag", "plugh");
		replace.replaceAll(sb);
		Assert.assertEquals("<html><plugh>foo</plugh><bar/><plugh a=\"b\">boo</plugh> and <plugh c=\"d\"/> end</html>", sb.toString());
	}
	
	@Test
	public void testDelete() {
		StringBuilder sb = new StringBuilder("<html><tag>foo</tag><bar/><tag a=\"b\">boo</tag> and <tag c=\"d\"/> end</html>");
		HTMLTagReplacement replace = new HTMLTagReplacement("tag");
		replace.replaceAll(sb);
		Assert.assertEquals("<html><bar/> and  end</html>", sb.toString());
	}
}
