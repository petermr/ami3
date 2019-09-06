package org.contentmine.html.util;



import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.graphics.html.util.JsoupWrapper;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nu.xom.Builder;

public class JsoupWrapperTest {

	private static final Logger LOG = Logger.getLogger(JsoupWrapperTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	@Test
	public void testJsoupClean() {
		String s = "<html><body></html>";
		String ss = Jsoup.clean(s, Whitelist.basic());
		Assert.assertEquals("",  ss);
	}
	
	
	@Test
	public void testJsoupParse() {
		String s = "<html><body></html>";
		org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(s);
		String ss = doc.toString();
		ss = ss.replaceAll("[\\s\\r\\n]+", " ");
		Assert.assertEquals("<html> <head></head> <body></body> </html>", ss);
	}
	
	@Test
	/** doesn't seem to mind invalid tags.
	 * 
	 */
	public void testJsoupParseIt() {
		String s = "<html>This is <i>italics</i> and so is <italic>this</italic></html>";
		org.jsoup.nodes.Document doc = Jsoup.parse(s);
		String ss = doc.toString();
		ss = ss.replaceAll("[\\s\\r\\n]+", " ");
		Assert.assertEquals("<html> <head></head> <body> This is <i>italics</i> and so is <italic> this </italic> </body> </html>", ss);
	}
	
	@Test
	public void testJsoupParseMeta() {
		String s = "<html><meta name='x' content='y'><body></html>";
		org.jsoup.nodes.Document doc = Jsoup.parse(s);
		String ss = doc.toString();
		ss = ss.replaceAll("[\\s\\r\\n]+", " ");
		// note tags not closed
		Assert.assertEquals("<html> <head> <meta name=\"x\" content=\"y\"> </head> <body></body> </html>", ss);
	}
	
	@Test
	@Ignore
	public void testJsoupParseMetaBad() {
		String s = "<html><meta name=\"x\" content=\"y is \"bad\" here\"><body></html>";
		Document doc = Jsoup.parse(s);
		String ss = doc.toString();
//		ss = ss.replaceAll("[\\s\\r\\n]+", " ");
		Assert.assertEquals("<html> <head> <meta name=\"x\" content=\"y is \" bad\"=\"\" here\"=\"\" /> </head> <body></body> </html>", ss);
	}
	
	@Test
	public void testStripBad() {
		String s = "<html><meta name=\"x\" content=\"y is \"bad\" here\"><body></html>";
		String ss = JsoupWrapper.parseAndCorrect(s);
		ss = ss.replaceAll("[\\s\\r\\n]+", " ");
		Assert.assertEquals("<html> <head> <meta name=\"x\" content=\"y is \" bad\" here\"> </head> <body></body> </html>", ss);
	}
	
	
	@Test
	@Ignore // entities
	public void testBad1() throws Exception {
		String ss = JsoupWrapper.parseAndCorrect(IOUtils.toString(new FileInputStream(new File(Fixtures.HTML_DIR, "badHtml1a.html"))));
		ss = ss.replaceAll("&nbsp;", " ");
		ss = ss.replaceAll("&aacute;", "#aacute");
		ss = ss.replaceAll("&eacute;", "#eacute");
		ss = ss.replaceAll("&egrave;", "#egrave");
		ss = ss.replaceAll("&micro;", "#micro");
		ss = ss.replaceAll("&deg;", "#deg");
		ss = ss.replaceAll("&times;", "#times");
		ss = ss.replaceAll("&oacute;", "#oacute");
		ss = ss.replaceAll("&iacute;", "#iacute");
		ss = ss.replaceAll("&middot;", "#middot");
		ss = ss.replaceAll("&copy;", "#copy");
		FileUtils.writeStringToFile(new File("target/bad"+new DateTime().getMillisOfDay()+".html"), ss);
		nu.xom.Document doc = new Builder().build(IOUtils.toInputStream(ss));
	}
	
	@Test
	@Ignore
	public void testXML() throws Exception {
		String s = IOUtils.toString(new FileInputStream(new File(Fixtures.HTML_DIR, "badHtml1a.html")));
		String ss = Jsoup.parse(s).html();
		FileUtils.writeStringToFile(new File("target/bad"+new DateTime().getMillisOfDay()+".html"), ss);
		ss = ss.replaceAll("&nbsp;", " ");
		ss = ss.replaceAll("&aacute;", "#aacute");
		ss = ss.replaceAll("&eacute;", "#eacute");
		ss = ss.replaceAll("&egrave;", "#egrave");
		ss = ss.replaceAll("&micro;", "#micro");
		ss = ss.replaceAll("&deg;", "#deg");
		ss = ss.replaceAll("&times;", "#times");
		ss = ss.replaceAll("&oacute;", "#oacute");
		ss = ss.replaceAll("&iacute;", "#iacute");
		ss = ss.replaceAll("&middot;", "#middot");
		ss = ss.replaceAll("&copy;", "#copy");
		ss = ss.replaceAll("[\\n\\r\\t]+", " ");
		ss = ss.replaceAll("<\\!\\-\\-[^>]*\\-\\->", "COMMENT");
		ss = ss.replaceAll("<script[^>]*>.*</script>", "<script/>");
		ss = ss.replaceAll(">[\\s]*<", ">\\\n<");
		FileUtils.writeStringToFile(new File("target/bad"+new DateTime().getMillisOfDay()+".html"), ss);
		nu.xom.Document doc = new Builder().build(IOUtils.toInputStream(ss));
	}
	
	
	@Test
	// runs on net
	@Ignore
	public void testBMC() throws Exception {
		URL url = new URL("http://www.biomedcentral.com/1471-2229/14/106");
		String ss = IOUtils.toString(url.openStream());
		HtmlFactory htmlFactory = new HtmlFactory();
		htmlFactory.addTagToDelete("script");
		htmlFactory.addTagToDelete("button");
		htmlFactory.addMissingNamespacePrefix("g");
		HtmlElement htmlElement = htmlFactory.parse(ss);
		Set<String> unknownTags = htmlFactory.getUnknownTags();
		Assert.assertTrue(unknownTags.size() >= 10);
		LOG.info(unknownTags);
		Assert.assertEquals(10,  unknownTags.size());
	}


}
