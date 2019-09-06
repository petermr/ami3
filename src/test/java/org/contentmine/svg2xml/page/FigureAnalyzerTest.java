package org.contentmine.svg2xml.page;

import org.apache.log4j.Logger;
import org.junit.Test;

import nu.xom.Element;

public class FigureAnalyzerTest {

	private final static Logger LOG = Logger.getLogger(FigureAnalyzerTest.class);
	
//	@Test
//	public void testMatchShort() {
//		String s = "Fig. 1. foo";
//		AbstractAnalyzer figureAnalyzer = new FigureAnalyzerX();
//		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.CAPTION_PATTERN, s);
//		Assert.assertEquals("serial", new Integer(1), i);
//	}
//	
//	@Test
//	public void testMatchLong() {
//		String s = "Figure 1. foo";
//		AbstractAnalyzer figureAnalyzer = new FigureAnalyzerX();
//		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.CAPTION_PATTERN, s);
//		Assert.assertEquals("serial", new Integer(1), i);
//	}
//	
//	@Test
//	public void testMatchNoSerial() {
//		String s = "Figure foo";
//		AbstractAnalyzer figureAnalyzer = new FigureAnalyzerX();
//		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.CAPTION_PATTERN, s);
//		Assert.assertEquals("serial", new Integer(-1), i);
//	}
//	
//	@Test
//	public void testNoMatchNoSerial() {
//		String s = "Fogure 2. foo";
//		AbstractAnalyzer figureAnalyzer = new FigureAnalyzerX();
//		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.CAPTION_PATTERN, s);
//		Assert.assertNull("serial", i);
//	}
//	
//	
//	@Test
//	public void testNoMatchNoSerialHigh() {
//		String s = "Figure 2. foo+(char)1643";
//		AbstractAnalyzer figureAnalyzer = new FigureAnalyzerX();
//		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.CAPTION_PATTERN, s);
//		Assert.assertEquals("serial", new Integer(2), i);
//	}
//	
//	@Test
//	public void testDingbat() {
//		String s = "Figure 2. foo"+(char)10110+"bar";
//		LOG.trace(s);
//		AbstractAnalyzer figureAnalyzer = new FigureAnalyzerX();
//		Integer i = FigureAnalyzerX.getSerial(FigureAnalyzerX.CAPTION_PATTERN, s);
//		Assert.assertEquals("serial", new Integer(2), i);
//	}
//	
	
	@Test
	public void testDingbat1() {
		Element e = new Element("x");
		add("F", e);
		add("i", e);
		add("g", e);
		Element a = add(String.valueOf((char)10110), e);
		Element t = new Element("title");
		t.appendChild("foo");
		a.appendChild(t);
		add("u", e);
		add("r", e);
		add("e", e);
		LOG.trace(e.getValue());
	}

	private Element add(String s, Element e) {
		Element text = new Element("text");
		text.appendChild(s);
		e.appendChild(text);
		return text;
	}

}
