package org.contentmine.graphics.html.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.joda.time.DateTime;
import org.w3c.tidy.Tidy;

import nu.xom.Document;
import nu.xom.Element;

/** wraps HTMLTidy and provides more options.
 * 
 * @author pm286
 *
 */
public class HTMLTidy {
	
	private static final String DOCTYPE_REGEX = "<!DOCTYPE[^>]*>";
	private static final String NEWLINE_REGEX = "[\\r\\n]+";
	private static final String WHITESPACE = "[\\r\\n\\t]+";
	private static final String ENDTAG_PREFIX = "(</[A-Za-z_][A-Za-z_0-9]*:)";
	private static final String STARTTAG_PREFIX = "(<[A-Za-z_][A-Za-z_0-9]*:)";
	private final static Logger LOG = LogManager.getLogger(HTMLTidy.class);
	private Tidy tidy;
	private List<HTMLTagReplacement> tagReplacementList;
	private ByteArrayOutputStream baos;
	private org.w3c.tidy.Node node;
	private boolean stripDoctype;
	private boolean removeXMLLang;
	private boolean flattenNewline;
	private boolean removeForeignPrefixes;
	private boolean removeTidyFails = true;
	private boolean removeInvisibleCharacters = true;

	public HTMLTidy() {
		tidy = createTidyWithOptions();
		this.setCommonDefaults();
	}
	
	/**
	 * reads HTML in inputStream and tidies it.
	 * First with HTML tidy (using as many cleaning options as possible
	 * then excises the DOCTYP and namespace from result
	 * Tidy may throw warnings and errors to syserr than cannot be 
	 * removed from console
	 * @param inputStream if null throws IOException
	 * @return document with some HTML root element
	 * @throws IOException
	 */
	public static Document htmlTidy(InputStream inputStream) throws IOException {
	    	
		if (inputStream == null) {
			throw new RuntimeException("Null input for HTMLTidy");
		}
		byte[] bytesin = IOUtils.toByteArray(inputStream);
    	Tidy tidy = createTidyWithOptions();
    	ByteArrayInputStream bais = new ByteArrayInputStream(bytesin);
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	// FIXME
    	org.w3c.tidy.Node doc = tidy.parse(bais, baos);
    	byte[] bytes = baos.toByteArray();
    	Document document = null;
    	try {
    		FileUtils.writeByteArrayToFile(new File("target/htmlIn"+new DateTime().getMillisOfDay()+".html"), bytes);
    		ByteArrayInputStream bais1 = new ByteArrayInputStream(bytes);
    		document = XMLUtil.parseQuietlyToDocument(bais1);
    	} catch (RuntimeException e) {
    		FileUtils.writeByteArrayToFile(new File("target/badhtmlIn"+new DateTime().getMillisOfDay()+".html"), bytesin);
    		FileUtils.writeByteArrayToFile(new File("target/badhtml"+new DateTime().getMillisOfDay()+".html"), bytes);
    		throw e;
    	}
//    	baos.close();
//    	Document document = null;
//    	String baosS0 = ""+new String(baos.toByteArray());
//    	if (baosS0.length() > 0) {
//    		document = XMLUtil.stripDTDAndOtherProblematicXMLHeadings(baosS0);
//    	}
    	return document;
    }

	public static String tidyWhitespaceAndForeignNamePrefixesAndLang(String content) {
		String content0 = HTMLTidy.removeLangXMLLang(content);
		if (!content0.equals(content)) {
			LOG.trace("tidied xml:lang");
		}
		String content1 = HTMLTidy.normalizeWhitespace(content0);
		if (!content1.equals(content0)) {
			LOG.trace("tidied whitespace");
		}
		String contentx = HTMLTidy.removeDTD(content1);
		if (!content1.equals(contentx)) {
			LOG.trace("stripped DTD");
		}
		String content2 = HTMLTidy.removeForeignNamespacePrefixes(contentx);
		if (!content1.equals(content2)) {
			LOG.trace("removed namespacePrefixes");
		}
		return content2;
	}

	private static String removeLangXMLLang(String content) {
		String content1 = content.replaceAll("xml\\:lang\\s*=\\s*\\\"([^\\\"]*)\\\"", ""); // xml:lang
		return content1;
	}

	private static String removeForeignNamespacePrefixes(String content) {
		String content1 = content.replaceAll(STARTTAG_PREFIX, "<_"); // <xyz: ... />
		String content2 = content1.replaceAll(ENDTAG_PREFIX, "</_"); // </xyz: ... />
		return content2;
	}

	/** missing tags in HTMLTidy, convert to span 
	 * 
	 */
	private static String removeTidyFails(String content) {
		String[] tags = {"bdi", "nav", "footer"};
		for (String tag : tags) {
			content = convertToSpan(content, tag);
		}
		return content;
	}

	private static String convertToSpan(String content, String tag) {
		int idx = -1;
//		while (true) { // debug
//			idx = content.indexOf(tag, idx + 1);
//			if (idx == -1) break;
//			LOG.warn("?"+tag+"?"+idx+"?"+content.substring(idx-10, idx+10));
//		}
		// start tag - may have attributes and spaces
		String content1 = content.replaceAll("<\\s*" + tag + "[^>]*>", "<span class='" + tag + "'>"); // <xyz: ... />
		// end tag
		String content2 = content1.replaceAll("</\\s*" + tag + "\\s*>", "</span>"); // <xyz: ... />
		// start-end tag - may have attributes and spaces
		String content3 = content2.replaceAll("<\\s*" + tag + "[^>]*>", "<span class='" + tag + "'/>"); // <xyz: ... />
		return content3;
	}

	private static String normalizeWhitespace(String content) {
		content = content.replaceAll(WHITESPACE, " ");
		return content;
	}

	private static String flattenNewline(String content) {
		content = content.replaceAll(NEWLINE_REGEX, "");
		return content;
	}

	private static String removeDTD(String content) {
		content = content.replaceAll(DOCTYPE_REGEX, "");
		return content;
	}

	private String preTidy(String s) {
		StringBuilder sb = new StringBuilder(s);
		if (tagReplacementList != null) {
			for (HTMLTagReplacement tagReplacement : tagReplacementList) {
				tagReplacement.replaceAll(sb);
			}
		}
		s = sb.toString();
		if (stripDoctype) {
			s = stripDoctype(s);
		}
//		if (removeXMLLang ||
//				flattenNewline ||
//				removeForeignPrefixes ||
//				removeTidyFails ||
//				removeInvisibleCharacters
//				) {
//			String s = sb.toString();
		if (removeXMLLang) {
			s = HTMLTidy.removeLangXMLLang(s);
		}
		if (flattenNewline) {
			s = HTMLTidy.flattenNewline(s);
		}
		if (removeForeignPrefixes) {
			s = HTMLTidy.removeForeignNamespacePrefixes(s);
		}
		if (removeTidyFails) {
			s = HTMLTidy.removeTidyFails(s);
		}
		if (removeInvisibleCharacters ) {
			s = HTMLTidy.removeInvisibleCharacters(s);						
		}
//			sb.replace(0, sb.length(), s);
//		
		return s;
	}

	/**
	 * 
	 * Not sure this is true...
	 * 
	 * The â€Ž ( e2 20ac 17d ) is invisible in browsers and has no semantic role
A <a href="/wiki/Q53997930" title="â€ŽMentholâ€Ž" data-serp-pos="1"><span class="wb-itemlink"><div class="spanclass=&quot;wb-itemlink-label&quot;_UNKNOWN" lang="en" dir="ltr"><div class="spanclass=&quot;searchmatch&quot;_UNKNOWN">
Menthol</div></div></span> <div class="spanclass=&quot;wb-itemlink-id&quot;_UNKNOWN">
(Q53997930)</div></a>?336?3c 61 20 68 72 65 66 3d 22 2f 77 69 6b 69 2f 51 35 33 39 39 37 39 33 30 22 20 74 69 74 6c 65 3d 22 e2 20ac 17d 4d 65 6e 74 68 6f 6c e2 20ac 17d 22 20 64 61 74 61 2d 73 65 72 70 2d 70 6f 73 3d 22 31 22 3e 3c 73 70 61 6e 20 63 6c 61 73 73 3d 22 77 62 2d 69 74 65 6d 6c 69 6e 6b 22 3e 3c 64 69 76 20 63 6c 61 73 73 3d 22 73 70 61 6e 63 6c 61 73 73 3d 26 71 75 6f 74 3b 77 62 2d 69 74 65 6d 6c 69 6e 6b 2d 6c 61 62 65 6c 26 71 75 6f 74 3b 5f 55 4e 4b 4e 4f 57 4e 22 20 6c 61 6e 67 3d 22 65 6e 22 20 64 69 72 3d 22 6c 74 72 22 3e 3c 64 69 76 20 63 6c 61 73 73 3d 22 73 70 61 6e 63 6c 61 73 73 3d 26 71 75 6f 74 3b 73 65 61 72 63 68 6d 61 74 63 68 26 71 75 6f 74 3b 5f 55 4e 4b 4e 4f 57 4e 22 3e a 4d 65 6e 74 68 6f 6c 3c 2f 64 69 76 3e 3c 2f 64 69 76 3e 3c 2f 73 70 61 6e 3e 20 3c 64 69 76 20 63 6c 61 73 73 3d 22 73 70 61 6e 63 6c 61 73 73 3d 26 71 75 6f 74 3b 77 62 2d 69 74 65 6d 6c 69 6e 6b 2d 69 64 26 71 75 6f 74 3b 5f 55 4e 4b 4e 4f 57 4e 22 3e a 28 51 35 33 39 39 37 39 33 30 29 3c 2f 64 69 76 3e 3c 2f 61 3e <a href="/wiki/Q53997930" title="â€ŽMentholâ€Ž" data-serp-pos="1"><span class="wb-itemlink"><div class="spanclass=&quot;wb-itemlink-label&quot;_UNKNOWN" lang="en" dir="ltr"><div class="spanclass=&quot;searchmatch&quot;_UNKNOWN">
Menthol</div></div></span> <div class="spanclass=&quot;wb-itemlink-id&quot;_UNKNOWN">
	 * @param s
	 * @return
	 */

	/**
	 * http://www.alanwood.net/unicode/general_punctuation.html
	 * 
	8203	‘​’	200B	 	ZERO WIDTH SPACE
‌	8204	‌	200C	&zwnj;	ZERO WIDTH NON-JOINER
‍	8205	‍	200D	&zwj;	ZERO WIDTH JOINER
‎	8206	‎	200E	&lrm;	LEFT-TO-RIGHT MARK
‏	8207	‏	200F	&rlm;	RIGHT-TO-LEFT MARK
	 * @param s
	 * @return
	 */
	private static String removeInvisibleCharacters(String s) {
		String[] invisibles = {
				String.valueOf((char) 8203),
				String.valueOf((char) 8204),
				String.valueOf((char) 8205),
				String.valueOf((char) 8206),
				String.valueOf((char) 8207),
		};
		for (String invis : invisibles) {
			s = s.replaceAll(invis, "");
		}
		return s;
	}

	public boolean isRemoveForeignPrefixes() {
		return removeForeignPrefixes;
	}

	public void setRemoveForeignPrefixes(boolean removeForeignPrefixes) {
		this.removeForeignPrefixes = removeForeignPrefixes;
	}

	public void setStripDoctype(boolean stripDoctype) {
		this.stripDoctype = stripDoctype;
	}

	public void setRemoveXMLLang(boolean removeXMLLang) {
		this.removeXMLLang = removeXMLLang;
	}

	public void setFlattenNewline(boolean flattenNewline) {
		this.flattenNewline = flattenNewline;
	}

	public static String stripDoctype(String s) {
		return s.replaceFirst("<!DOCTYPE[^>]*>", "");
	}

	public static void stripDoctype(StringBuilder sb) {
		int idx0 = sb.indexOf("<!DOCTYPE");
		if (idx0 != -1) {
			int idx1 = sb.indexOf(">", idx0); 
			if (idx1 == -1) {
				throw new RuntimeException("Bad DOCTYPE at: "+idx0);
			}
			sb.delete(idx0, idx1 + 1);
		}
	}

	/** crude method to change tags.
	 * e.g. <foo>...</foo> => <span>...</span>; <foo/> => <span/>; will strip attributes.
	 * 
	 */
	
	public static String replaceBadTags(String s, String tag, String newTag) {
		s = s.replaceAll("<"+tag+"[^>]*>", "<"+newTag+">");
		s = s.replaceAll("<"+tag+"[^>/]*/>", "<"+newTag+"/>");
		s = s.replaceAll("</"+tag+"\\s*>", "</"+newTag+">");
		return s;
	}


	private static Tidy createTidyWithOptions() {
		Tidy tidy = new Tidy();
    	tidy.setDocType(null);
    	tidy.setXmlOut(true);
    	tidy.setDropEmptyParas(true);
    	tidy.setDropFontTags(true);
    	tidy.setMakeClean(true);
    	tidy.setNumEntities(true);
    	tidy.setXHTML(true);
    	tidy.setQuiet(true);
    	tidy.setQuoteMarks(true);
    	tidy.setShowWarnings(false);
		return tidy;
	}

	public static Element convertStringToXHTML(String s) {
		Element element = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
		try {
			Document document = htmlTidy(bais);
			if (document == null) {
				return null;
			}
			element = document.getRootElement();
		} catch (Exception e) {
			throw new RuntimeException("parse: "+e);
		}
		return element;
	}

	public String tidy(InputStream is) throws IOException {
		String inputString = IOUtils.toString(is, CMineUtil.UTF8_CHARSET);
//		StringBuilder sb = new StringBuilder(inputString);
		inputString = preTidy(inputString);
		baos = new ByteArrayOutputStream();
		node = tidy.parse(CMineUtil.createUTF8Stream(inputString), baos);
//		sb = new StringBuilder(baos.toString());
		String out = baos.toString();
		return out;
	}

	public HtmlElement createHtmlElement(InputStream is) throws Exception {
		LOG.trace("createHtmlElement");
		String out = tidy(is);
//		FileUtils.write(new File("target/problem1.txt"), out,  CMineUtil.UTF8_CHARSET);
		HtmlFactory htmlFactory = new HtmlFactory();
		return htmlFactory.parse(out);
	}
	
	public void addTagReplacement(HTMLTagReplacement tagReplacement) {
		if (tagReplacementList == null) {
			tagReplacementList = new ArrayList<HTMLTagReplacement>();
		}
		tagReplacementList.add(tagReplacement);
	}
	
	public void deleteTag(String old) {
		addTagReplacement(new HTMLTagReplacement(old));
	}
	
	public void replacetag(String old, String newTag) {
		addTagReplacement(new HTMLTagReplacement(old, newTag));
	}
	
	public void seStripDoctype(boolean b) {
		this.stripDoctype = b;
	}
	
	public org.w3c.tidy.Node getNode() {
		return node;
	}
	
	public ByteArrayOutputStream getByteArrayOutputStream() {
		return baos;
	}
	
	public String getOutputString() {
		return baos == null ? null : baos.toString();
	}
	
	public Tidy getTidy() {
		return tidy;
	}

	public void setCommonDefaults() {
		setStripDoctype(true);
		getTidy().setDocType(null);
		setFlattenNewline(true);
		removeTidyFails = true;
		removeInvisibleCharacters = true;
	}
}
