package org.contentmine.norma.pubstyle;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlFactory;
import org.contentmine.norma.InputFormat;
import org.contentmine.norma.NAConstants;
import org.contentmine.norma.RawInput;
import org.contentmine.norma.input.InputReader;
import org.contentmine.norma.tagger.PubstyleTagger;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;

public abstract class PubstyleReader {

	private static final Logger LOG = LogManager.getLogger(PubstyleReader.class);
public final static String TEI_XSL_REOURCE = "/"+ NAConstants.ORG_CM_NORMA+"/"+"tei/";

	private URL url;
	private RawInput rawInput;
	private InputReader inputReader;
	private File file;
	private HtmlElement htmlElement;
	private InputFormat inputFormat;
	private HashMap<InputFormat, PubstyleTagger> taggerByFormatMap;
	private List<Pair<String, String>> tagReplacementList;
	
	public static List<String> EXTRANEOUS_TAGS = Arrays.asList(new String[] {
			"button",
			"fieldset",
			"iframe",
			"input",
			"link",
			"object",
			"script",
			"style",
	});
;
	
	protected PubstyleReader() {
		setDefaults();
		addTaggers();
	}

	public PubstyleReader(InputFormat type) {
		this();
		setFormat(type);
	}

	public void setFormat(InputFormat inputFormat) {
		this.inputFormat = inputFormat;
		inputReader = InputReader.createReader(inputFormat);
	}

	public InputFormat getInputFormat() {
		return inputFormat;
	}
	
	private void setDefaults() {
	}

	public InputReader getInputReader() {
		return this.inputReader;
	}
	
	public void readURL(String urlString) throws Exception {
		if (inputReader != null && urlString != null) {
			url = new URL(urlString);
			InputStream inputStream = url.openStream();
			this.rawInput = inputReader.read(inputStream);
		}
	}

	public void readFile(File file) throws Exception {
		if (inputReader != null && file != null) {
			this.file = file;
			InputStream inputStream = new FileInputStream(file);
			this.rawInput = inputReader.read(inputStream);
		}
	}

	public RawInput getRawInput() {
		return this.rawInput;
	}

	public HtmlElement getOrCreateXHtmlFromRawHtml() throws Exception {
		byte[] rawBytes = (rawInput == null) ? null : rawInput.getRawBytes();
		if (rawBytes != null) {
			HtmlFactory htmlFactory = new HtmlFactory();
			ByteArrayInputStream bais = new ByteArrayInputStream(rawBytes);
			htmlElement = htmlFactory.parse(bais);
		}
		return htmlElement;
	}
	
	protected abstract void addTaggers();

	protected void addTagger(InputFormat format, PubstyleTagger tagger) {
		ensureTaggerByFormatMap();
		taggerByFormatMap.put(format, tagger);
	}

	private void ensureTaggerByFormatMap() {
		if (taggerByFormatMap == null) {
			taggerByFormatMap = new HashMap<InputFormat, PubstyleTagger>();
		}
	}

	public PubstyleTagger getTagger(InputFormat inputFormat) {
		ensureTaggerByFormatMap();
		return taggerByFormatMap.get(inputFormat);
	}

	/** removes a number of publisher tags which clutter the HTML.
	 * 
	 * These tags should not affect the content (they are mainly interactive). Currently:
	 * script
	 * link
	 * object
	 * iframe
	 * fieldset
	 * button
	 * style
	 * 
	 * This functionality can be overridden in subclasses if required
	 * 
	 * 
	 * @param htmlElement
	 */
	public void removeExtraneousHtmlTags(List<String> tagNames) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tagNames.size(); i++) {
			String tagName = tagNames.get(i);
			if (i == 0) {
				sb.append("//*[local-name()='"+tagName+"'");
			} else if (i == tagNames.size() - 1) {
				sb.append("]");
				sb.append(" | //comment()");
			} else {
				sb.append(" or local-name()='"+tagName+"'");
			}
		}
		Nodes nodes = htmlElement.query(sb.toString());
		for (int i = nodes.size()-1; i >= 0; i--) {
			nodes.get(i).detach();
		}
	}

	/** many tags are common to the problem so we can bundle them here.
	 * 
	 */
	protected void removeExtraneousHtmlTagsAndXPaths() {
		removeExtraneousHtmlTags(EXTRANEOUS_TAGS);
		removeExtraneousXPaths();
	}

	protected void removeExtraneousXPaths() {
		List<String> extraneousXPaths = getExtraneousXPaths();
		for (String xpath : extraneousXPaths) {
			removeNodes(xpath);
		}
	}
	
	private void removeNodes(String xpath) {
		Nodes nodes = htmlElement.query(xpath);
		for (int i = nodes.size() - 1; i >= 0; i--) {
			nodes.get(i).detach();
		}
	}

	protected abstract List<String> getExtraneousXPaths();

	/** normalizes tags and known problems.
	 * 
	 */
	public HtmlElement normalize() {
		if (htmlElement != null) {
			removeExtraneousHtmlTagsAndXPaths();
			normalizeTagNames();
			normalizeDivStructure();
			normalizeCharacters();
		}
		return htmlElement;
	}


	/** currently changes em->i, strong->i, etc.
	 * 
	 */
	protected void normalizeTagNames() {
		ensureTagNameReplacementList();
		for (Pair<String, String> tagReplacement : tagReplacementList) {
			changeTagName(tagReplacement.getLeft(), tagReplacement.getRight());
		}
		
//		changeTagName("em", "i");
//		changeTagName("strong", "b");
	}

	private void ensureTagNameReplacementList() {
		if (tagReplacementList == null) {
			tagReplacementList = new ArrayList<Pair<String, String>>();
		}
		// TODO Auto-generated method stub
		
	}

	private void changeTagName(String tag0, String tag1) {
		Nodes nodes = htmlElement.query("//*[local-name()='"+tag0+"']");
		for (int i = 0; i < nodes.size(); i++) {
			replaceNode((Element)nodes.get(i), tag1);
		}
	}

	/** replace element by one with a new tag.
	 * 
	 * transfers children so that order of processing should not matter.
	 * old element is destroyed.
	 * 
	 * @param element
	 * @param tag
	 */
	private void replaceNode(Element element, String tag) {
		HtmlElement newHtmlElement = new HtmlFactory().createElementFromTag(tag);
		XMLUtil.copyAttributes(element, newHtmlElement);
		int size = element.getChildCount();
		for (int i = 0; i < size; i++) {
			Node child = element.getChild(0);
			child.detach();
			newHtmlElement.appendChild(child);
		}
	}

	/** create a div structure if missing.
	 * 
	 */
	protected void normalizeDivStructure() {
	}

	/** normalize problem characters.
	 * 
	 */
	protected void normalizeCharacters() {
	}

	public HtmlElement getHtmlElement() {
		return htmlElement;
	}

	public void setHtmlElement(HtmlElement htmlElement) {
		this.htmlElement = htmlElement;
	}

}
