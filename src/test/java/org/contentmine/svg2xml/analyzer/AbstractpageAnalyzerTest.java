package org.contentmine.svg2xml.analyzer;

import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.svg2xml.util.SVG2XMLUtil;
import org.junit.Assert;
import org.junit.Test;

import nu.xom.Element;

public class AbstractpageAnalyzerTest {

	  
	  @Test
	  public void testTidyTagWhiteTag() {
		  Element element = XMLUtil.parseXML("" +
		  "<p>Borer" + 
		  " <i>et</i>" +
		  " <i>al</i>" +
		  " <i>BMC</i>" +
		  " <i>Evolutionary</i>" +
		  " <i>Biology</i> 2011," +
		  " <b>11</b>:310 http://www.biomedcentral.com/1471-2148/11/310" +
		  "</p>" +
		  "");
		  SVG2XMLUtil.tidyTagWhiteTag(element, "i");
		  Assert.assertEquals("expanded", 
				  "<p>Borer <i>et al BMC Evolutionary Biology</i> 2011, <b>11</b>:310 http://www.biomedcentral.com/1471-2148/11/310</p>", 
				  element.toXML());

	  }
	  
	  @Test
	  public void testTidyTagWhiteTag0() {
		  Element element = XMLUtil.parseXML("" +
		  "<p>"+
		  " <i>ONE</i>" +
		  " <i>TWO</i>" +
		  " <i>THREE</i>" +
		  "</p>" +
		  "");
		  SVG2XMLUtil.tidyTagWhiteTag(element, "i");
		  Assert.assertEquals("expanded", 
				  "<p> <i>ONE TWO THREE</i></p>",
				  element.toXML());

	  }
}
