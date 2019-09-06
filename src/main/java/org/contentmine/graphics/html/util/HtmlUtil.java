package org.contentmine.graphics.html.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLConstants;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlMeta;

import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.XPathContext;

public class HtmlUtil {

	private final static Logger LOG = Logger.getLogger(HtmlUtil.class);
	
    /** XPathContext for Html.
     */
    public static XPathContext XHTML_XPATH = new XPathContext("h", XMLConstants.XHTML_NS);
    public static Pattern ATTRIBUTE = Pattern.compile("\\s+([a-z]+\\s*=\\s*\\\"[^\\\"]+\\\")");

	public static List<HtmlElement> getQueryHtmlElements(HtmlElement htmlElement, String xpath) {
		List<Element> elements = XMLUtil.getQueryElements(htmlElement, xpath, XHTML_XPATH);
		List<HtmlElement> htmlElements = new ArrayList<HtmlElement>();
		for (Element element : elements) {
			if (!(element instanceof HtmlElement)) {
				throw new RuntimeException("Element was not HtmlElement: "+element.toXML());
			}
			htmlElements.add((HtmlElement)element);
		}
		return htmlElements;
	}

	/** extracts nodes and their values.
	 * 
	 * @param htmlElement ancestor
	 * @param xpath can include h:* elements
	 * @return list of string values of nodes
	 */
	public static List<String> getQueryHtmlStrings(Element htmlElement, String xpath) {
		Nodes nodes = htmlElement.query(xpath, XHTML_XPATH);
		List<String> stringList = new ArrayList<String>();
		for (int i = 0; i < nodes.size(); i++) {
			stringList.add(nodes.get(i).getValue());
		}
		return stringList;
	}

//	/** read file and subclass elements to HtmlElement.
//	 * 
//	 * @param file
//	 * @return
//	 * @throws Exception
//	 */
//	public static HtmlElement readAndCreateElementUsingJsoup(File file) throws Exception {
//		InputStream is = new FileInputStream(file);
//		return readAndCreateElementUsingJsoup(is);
//	}
//	
//	/** read XML string and subclass elements to HtmlElement.
//	 * 
//	 * @param xmlString must be valid XHTML
//	 * @return
//	 * @throws Exception
//	 */
//	public static HtmlElement readAndCreateElementUsingJsoup(String xmlString) throws Exception {
//		InputStream is = IOUtils.toInputStream(xmlString);
//		return readAndCreateElementUsingJsoup(is);
//	}
	

//	/** parses HTML into dom if possible.
//	 * 
//	 * @param is
//	 * @return null if fails
//	 * @throws Exception
//	 */
//	@Deprecated // use HtmlFactory
//	public static HtmlElement readAndCreateElementUsingJsoup(InputStream is) throws Exception {
//		String s = IOUtils.toString(is, "UTF-8");
//		org.jsoup.nodes.Document doc = Jsoup.parse(s);
//		String xmlDoc = doc.html();
//		HtmlElement htmlElement = null;
//		try {
//			HtmlFactory htmlFactory = new HtmlFactory();
////			htmlFactory.addReplacement(HtmlFactory.DEFAULT_REPLACEMENT_MAP);
//			Element xmlElement = XMLUtil.stripDTDAndParse(xmlDoc);
//			htmlElement = htmlFactory.parse(xmlElement);
//		} catch (Exception e) {
//			LOG.error("cannot parse HTML"+e+"; "+xmlDoc, e);
//		}
//		return htmlElement;
//	}

	/** JSoup does not add XHTML namespace to all elements, so add it.
	 * 
	 * @param xmlElement
	 */
	private static void addHTMLNamespace(Element xmlElement) {
		Nodes nodes = xmlElement.query("//*[namespace-uri()='']");
		for (int i = 0; i < nodes.size(); i++) {
			Element element = (Element)nodes.get(i);
			element.addNamespaceDeclaration("",  XMLConstants.XHTML_NS);
		}
	}

	public static HtmlElement readAndCreateElementQuietly(File file) {
		HtmlElement element = null;
		try {
			element = readAndCreateElement(file);
		} catch (Exception e) {
			throw new RuntimeException("Cannot read HOCR file: "+file, e);
		}
		return element;
	}

	/** read file and subclass elements to HtmlElement.
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static HtmlElement readAndCreateElement(File file) throws Exception {
		HtmlElement htmlElement = new HTMLTidy().createHtmlElement(new FileInputStream(file));
		return htmlElement;
	}
	
	/** read file and subclass elements to HtmlElement.
	 * @Deprecated("seems to fail on reading")
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static HtmlElement readAndCreateElement(URL url) throws Exception {
		LOG.debug("reading URL into HTML "+url);
		HtmlUnitWrapper htmlUnitWrapper = new HtmlUnitWrapper();
		HtmlElement htmlElement = htmlUnitWrapper.readAndCreateElement(url);
		return htmlElement;
	}
	
	/**
	 * From http://stackoverflow.com/questions/994331/java-how-to-decode-html-character-entities-in-java-like-httputility-htmldecode 
	 * with thanks
	 * 
	 * @param input
	 * @return
	 */
    public static final String unescapeHtml3(final String input, Map<String, CharSequence> lookupMap) {
        StringWriter writer = null;
        int len = input.length();
        int i = 1;
        int st = 0;
        while (true) {
            // look for '&'
            while (i < len && input.charAt(i-1) != '&') {
                i++;
            }
            if (i >= len) break;

            // found '&', look for ';'
            int j = i;
            while (j < len && j < i + MAX_ESCAPE + 1 && input.charAt(j) != ';') {
                j++;
            }
            if (j == len || j < i + MIN_ESCAPE || j == i + MAX_ESCAPE + 1) {
                i++;
                continue;
            }

            // found escape 
            if (input.charAt(i) == '#') {
                // numeric escape
                int k = i + 1;
                int radix = 10;

                final char firstChar = input.charAt(k);
                if (firstChar == 'x' || firstChar == 'X') {
                    k++;
                    radix = 16;
                }

                try {
                    int entityValue = Integer.parseInt(input.substring(k, j), radix);
                    if (writer == null) {
                        writer = new StringWriter(input.length());
                    }
                    writer.append(input.substring(st, i - 1));
                    writeEntityIncludingSurrogates(writer, entityValue);

                } catch (NumberFormatException ex) { 
                    i++;
                    continue;
                }
            }
            else {
                // named escape
                CharSequence value = lookupMap.get(input.substring(i, j));
                if (value == null) {
                    i++;
                    continue;
                } 

                LOG.trace("changed "+input.substring(i, j)+" to "+value);
                if (writer == null) {
                    writer = new StringWriter(input.length());
                }
                writer.append(input.substring(st, i - 1));
                writer.append(value);
            }

            // skip escape
            st = j + 1;
            i = st;
        }

        if (writer != null) {
            writer.append(input.substring(st, len));
            return writer.toString();
        }
        return input;
    }

	private static void writeEntityIncludingSurrogates(StringWriter writer, int entityValue) {
		if (entityValue > 0xFFFF) {
		    final char[] chrs = Character.toChars(entityValue);
		    writer.write(chrs[0]);
		    writer.write(chrs[1]);
		} else {
		    writer.write(entityValue);
		}
	}

    private static final int MIN_ESCAPE = 2;
    private static final int MAX_ESCAPE = 6;

    /** removes foo="bar" from a string.
     * 
     * assumes HTML contains foo="bar" string and removes them and
     * all contained comment. Do not use for well-formed XML - it is designed
     * for awful HTML.
     * 
     * @param ss
     * @param attributeName
     * @return stripped string
     */
	public static String stripAttributeFromText(String ss, String attributeName) {
		StringBuilder sb = new StringBuilder();
		int start = 0;
		Pattern pattern = Pattern.compile("("+attributeName+"\\s*=\\s*\\\"[^\\\"]+\\\")");
		Matcher matcher = pattern.matcher(ss);
		while (matcher.find(start)) {
			String string1 = ss.substring(start, matcher.start());
			sb.append(string1);
			start = matcher.end();
		}
		sb.append(ss.substring(start));
		return sb.toString();
	}

    /** removes <foo>...</foo> from a string.
     * 
     * assumes HTML contains <foo ...>...</foo> and removes them and
     * all contained content. Do not use for well-formed XML - it is designed
     * for awful HTML.
     * 
     * @param ss
     * @param tag to remove
     * 
     * @return
     */
	public static String stripElementFromTextString(String ss, String tag) {
		StringBuilder sb = new StringBuilder(ss);
		while (true) {
			int start = sb.indexOf("<"+tag);
			if (start == -1) {
				break;
			}
			int startEnd = sb.indexOf(">", start + 2);
			int end = sb.indexOf("</"+tag+">", startEnd);
			if (end == -1) {
				throw new RuntimeException("no trailing </"+tag+">");
			}
			sb.delete(start,  end + tag.length() + 3);
		}
		return sb.toString();
	}

    
    /** removes <tag> ... </tag> from a string.
     * 
     * assumes HTML contains <foo> ... </foo> bars and removes them and
     * all contained comment. Do not use for well-formed XML - it is designed
     * for awful HTML.
     * 
     * @param ss
     * @param startTag
     * @param endTag
     * @return
     */
	public static String stripDOCTYPE(String ss) {
		if (ss == null || ss.trim().length() == 0) {
			LOG.error("Empty Html Document");
			return "";
		}
		StringBuilder sb = new StringBuilder(ss);
		int start = 0;
		start = skipWhitespace(sb, start);
		start = sb.indexOf("<!DOCTYPE", start);
		int end = sb.indexOf(">", start);
		end = skipWhitespace(sb, end + 1);
		if (start >= 0 && end >= 0) {
			sb.delete(start, end);
		}
		return sb.toString();
	}
	
	final static char[][] badGood = {
		// nbsp
		new char[]{'\u00a0', ' '},
		// smart quotes
		new char[]{'\u201c', '"'},
		new char[]{'\u201d', '"'},
		new char[]{'\u201e', '"'},
		new char[]{'\u201f', '"'},
	};
	
	public final static String replaceProblemCharacters(String s) {
		StringBuilder sb = new StringBuilder(s);
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			for (int j = 0; j < badGood.length; j++) {
				if (c == badGood[j][0]) {
					LOG.trace("substituted "+badGood[j][0]+" with"+badGood[j][1]);
					sb.setCharAt(i, badGood[j][1]);
				}
			}
		}
		return sb.toString();
	}

	private static int skipWhitespace(StringBuilder sb, int start) {
		while (true) {
			if (Character.isWhitespace(sb.charAt(start))) {
				start++;
			} else {
				break;
			}
		}
		return start;
	}

	/** some illiterate has used smart quotes for BMC namespaces.
	 * 
	 * substitute "\u201c and \u201d" by balanced " "
	 * 
	 * then add missing namespace prefix "g"
	 * 
	 * @param pageAsXml
	 * @return
	 */
	public static String removeBMCHorror(String xmlString) {
		String s = xmlString.replaceAll("\"\u201c", "\"");
		s = s.replaceAll("\u201d\"", "\"");
		if (s.length() != xmlString.length()) {
			s = s.replaceAll("<html", "<html xmlns:g=\"http://g.foo/\"");
			// broken buttonß
			s = s.replaceAll("<button [^>]*[^/]>", "<button>");
		}
		return s;
	}

	/**
	 *  returns "*[local-name()='"+tag+"']" 
	 *  primarily to balance quotes
	 *  
	 * @param tag
	 * @return
	 */
	public static String elem(String tag) {
		return "*[local-name()='"+tag+"']";
	}

	/** only use on well-formed HTML
	 * 
	 * @param urlString
	 * @return
	 */
	public static HtmlElement readAndCreateElement(String urlString) {
		HtmlElement htmlElement = null;
		try {
			URL url = new URL(urlString);
			InputStream openStream = url.openStream();
			Element element = XMLUtil.parseQuietlyToRootElement(openStream);
			openStream.close();
			htmlElement = HtmlElement.create(element);
		} catch (Exception e) {
			throw new RuntimeException("cannot parse/open "+urlString, e);
		}
		return htmlElement;
	}

	public static List<HtmlElement> getQueryHtmlElements(List<? extends HtmlElement> htmlElementList, String xpath) {
		ArrayList<HtmlElement> resultList = new ArrayList<>();
		for (HtmlElement htmlElement : htmlElementList) {
			List<HtmlElement> elements = HtmlUtil.getQueryHtmlElements(htmlElement, xpath);
			resultList.addAll(elements);
		}
		return resultList;
	}

	public static HtmlElement parseQuietlyToHtmlElementWithoutDTD(File htmlFile) {
		Element rootElement = XMLUtil.parseQuietlyToDocumentWithoutDTD(htmlFile).getRootElement();
		HtmlElement hocrElement = HtmlElement.create(rootElement);
		return hocrElement;
	}

	

//	@Deprecated
//	public static HtmlElement replaceEntitiesAndJavascriptTags(String ss) throws IOException {
//		ss = HtmlUtil.stripDOCTYPE(ss);
//		ss = ss.replace("<html", "<html xmlns:g=\"http://foo\""); // Missing namespace in BMC
//		ss = HtmlUtil.unescapeHtml3(ss, lookupMapXML);
//		ss = HtmlUtil.replaceProblemCharacters(ss);
//		ss = HtmlUtil.stripJavascriptElement(ss, "script");
//		ss = HtmlUtil.stripJavascriptElement(ss, "button");
//		ss = Jsoup.parse(ss).html();
//		// ARGH Jsoup re-escapes characters - have to turn them back again, but NOT &amp; 
//		ss = HtmlUtil.unescapeHtml3(ss, lookupMapHTML);
//		Element element = XMLUtil.parseXML(ss);
//		HtmlElement htmlElement = HtmlElement.create(element);
//		return htmlElement;
//	}

}
