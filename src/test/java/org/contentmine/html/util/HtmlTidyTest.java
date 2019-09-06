package org.contentmine.html.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.util.HTMLTidy;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.tidy.Tidy;


public class HtmlTidyTest {

	private final static Logger LOG = Logger.getLogger(HtmlTidyTest.class);
	
	@Test
	public void test1() throws IOException {
		String html = "<html><title>t</title><meta name=\"x\" content=\"y\"/></html>";
		Tidy tidy = makeTidy();
		String s = HtmlTidyTest.createOuput(html, tidy);
		Assert.assertEquals("out", 
				"<html><head><title>t</title><meta name=\"x\" content=\"y\" /></head></html>", s);
	}

	public void testNoEndTag() throws IOException {
		String html = "<html><title>t</title><meta name=\"x\" content=\"y\"></html>";
		Tidy tidy = makeTidy();
		String s = HtmlTidyTest.createOuput(html, tidy);
		Assert.assertEquals("out", 
				"<html><head><title>t</title><meta name=\"x\" content=\"y\" /></head></html>", s);
	}

	@Test
	public void testQuoting() throws IOException {
		String html = "<html><title class=\"a&b\">t</title><script> foo < bar & plugh</script><p> a < b & c</html>";
		Tidy tidy = makeTidy();
		// quoting does not happen in script
		String s = HtmlTidyTest.createOuput(html, tidy);
		Assert.assertEquals("out", 
				"<html><head><title class=\"a&amp;b\">t</title><script type=\"text/javascript\"> foo < bar & plugh</script></head><body><p>a &lt; b &amp; c</p></body></html>", s);
	}
// <html><head><title>t</title><script type="text/javascript"> foo < bar</script></head></html>


	@Test
	public void testBadMeta() throws IOException {
		String html = "<html><title>t</title><meta name=\"x\" content=\"y is \"bad\" here\"></html>";
		Tidy tidy = makeTidy();
		tidy.setXmlOut(true);
		String s = HtmlTidyTest.createOuput(html, tidy);
		Assert.assertEquals("out", 
				"<html><head><title>t</title><meta name=\"x\" content=\"y is \" /></head></html>", s);
	}
	

	@Test
	/** unfortunately we cannot catch the deliberate error which therefore comes out on the error stream.
	 * 
	 */
	public void testBadTag() throws IOException {
		String html = "<html><title>t</title><meta name=\"x\" content=\"y is \"bad\" here\"><it>bad</it></html>";
		HTMLTidy htmlTidy = new HTMLTidy();
		htmlTidy.tidy(IOUtils.toInputStream(html));
		// fails
		Assert.assertEquals("out", "", htmlTidy.getOutputString());
	}
	

	@Test
	public void testMendBadTag() throws IOException {
		String html = "<html><title>t</title><meta name=\"x\" content=\"y is \"bad\" here\"><it>bad</it></html>";
		HTMLTidy htmlTidy = new HTMLTidy();
		htmlTidy.replacetag("it", "i");
		htmlTidy.tidy(IOUtils.toInputStream(html));
		Assert.assertEquals("out", 
				"<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta name=\"generator\" content=\"HTML Tidy, see www.w3.org\" /><title>t</title><meta name=\"x\" content=\"y is \" /></head><body><i>bad</i></body></html>", htmlTidy.getOutputString());
	}
	
	@Test
	public void testHTMLTidy() throws IOException {
		InputStream is = IOUtils.toInputStream("<html><script> a = b</script><meta name=\"a\" content=\"b\"></html>");
		HTMLTidy htmlTidy = new HTMLTidy();
		String out = htmlTidy.tidy(is);
		Assert.assertEquals("new", 
				"<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta name=\"generator\" content=\"HTML Tidy, see www.w3.org\" /><script type=\"text/javascript\"> a = b</script><meta name=\"a\" content=\"b\" /><title></title></head></html>", out.trim());
		
	}

	@Test
	public void testHTMLTidyNoScript() throws IOException {
		InputStream is = IOUtils.toInputStream("<html><script> a = b</script><meta name=\"a\" content=\"b\"></html>");
		HTMLTidy htmlTidy = new HTMLTidy();
		htmlTidy.deleteTag("script");
		String out = htmlTidy.tidy(is);
		Assert.assertEquals("new", "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta name=\"generator\" content=\"HTML Tidy, see www.w3.org\" /><meta name=\"a\" content=\"b\" /><title></title></head></html>", out.trim());
		
	}
	
	@Test
	public void testReplaceDoctype() {
		StringBuilder sb = new StringBuilder("<?xml version=\"1.0\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html><p>a</p></html>");
		HTMLTidy.stripDoctype(sb);
		Assert.assertEquals("<?xml version=\"1.0\"?>\n<html><p>a</p></html>", sb.toString());
	}
	
	// ===================================
	
	private Tidy makeTidy() {
		Tidy tidy = new Tidy();
		tidy.setMakeClean(true);
		tidy.setNumEntities(true);
		tidy.setShowWarnings(false);
		tidy.setRawOut(true);
		tidy.setDocType("omit");
		tidy.setDropEmptyParas(true);
		tidy.setDropFontTags(true);
		tidy.setQuoteAmpersand(true);
		tidy.setQuoteMarks(true);
		tidy.setQuiet(true);
		tidy.setXHTML(true);
		return tidy;
	}

	private static String createOuput(String html, Tidy tidy) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		tidy.parse(IOUtils.toInputStream(html), baos);
		String s = baos.toString();
		s = HtmlTidyTest.declutter(s);
		return s;
	}

	private static String declutter(String s) {
		s = s.replaceAll("[\\r\\n]", "");
		s = s.replaceAll("<!DOCTYPE[^>]*>", "");
		s = s.replaceAll("<meta name=\"generator\"[^>]*/>", "");
		s = s.replaceAll(" xmlns=\"http://www.w3.org/1999/xhtml\"", "");
		return s;
	}
}
