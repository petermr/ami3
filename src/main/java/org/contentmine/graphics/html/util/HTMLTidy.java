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
import org.apache.log4j.Logger;
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
	private final static Logger LOG = Logger.getLogger(HTMLTidy.class);
	private Tidy tidy;
	private List<HTMLTagReplacement> tagReplacementList;
	private ByteArrayOutputStream baos;
	private org.w3c.tidy.Node node;
	private boolean stripDoctype;
	private boolean removeXMLLang;
	private boolean flattenNewline;
	private boolean removeForeignPrefixes;

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

	private void preTidy(StringBuilder sb) {
		if (tagReplacementList != null) {
			for (HTMLTagReplacement tagReplacement : tagReplacementList) {
				tagReplacement.replaceAll(sb);
			}
		}
		if (stripDoctype) {
			stripDoctype(sb);
		}
		if (removeXMLLang || flattenNewline || removeForeignPrefixes) {
			String s = sb.toString();
			if (removeXMLLang) {
				s = HTMLTidy.removeLangXMLLang(s);
			}
			if (flattenNewline) {
				s = HTMLTidy.flattenNewline(s);
			}
			if (removeForeignPrefixes) {
				s = HTMLTidy.removeForeignNamespacePrefixes(s);
			}
			sb.replace(0, sb.length(), s);
		}
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
		StringBuilder sb = new StringBuilder(IOUtils.toString(is));
		preTidy(sb);
		is = IOUtils.toInputStream(sb.toString());
		baos = new ByteArrayOutputStream();
		node = tidy.parse(is, baos);
		sb = new StringBuilder(baos.toString());
		// currently postTidy repeats preTidy()
		postTidy(sb);
		String out = sb.toString();
		LOG.trace("SB "+out);
		baos = new ByteArrayOutputStream();
		IOUtils.write(out.getBytes(), baos);
		return out;
	}
	
	public HtmlElement createHtmlElement(InputStream is) throws Exception {
		String out = tidy(is);
		HtmlFactory htmlFactory = new HtmlFactory();
		return htmlFactory.parse(out);
	}
	
	private void postTidy(StringBuilder sb) {
		preTidy(sb);
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
	}
}
