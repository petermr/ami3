package org.contentmine.svg2xml.table;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlB;
import org.contentmine.graphics.html.HtmlCaption;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlTable;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.svg2xml.util.SVG2XMLUtil;

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Nodes;

public class TableCaption extends TableChunk {

	private final static Logger LOG = Logger.getLogger(TableCaption.class);
	
	public static final Pattern PATTERN = Pattern.compile("^[Tt][Aa][Bb][Ll]?[Ee]?\\s*\\.?\\s*(\\d+).*", Pattern.DOTALL);

	private HtmlCaption caption;
	
	public TableCaption(List<? extends SVGElement> elementList) {
		super(elementList);
	}

	public TableCaption(TableChunk chunk) {		
		this(chunk.getElementList());
	}

	public static void addCaptionTo(HtmlTable table, HtmlCaption caption) {
		Nodes captions = table.query("*[local-name()='caption']");
		HtmlP p = new HtmlP();
		if (captions.size() == 0) {
			HtmlB b = new HtmlB();
			XMLUtil.transferChildren(caption,  b);
			p.appendChild(b);
			caption.appendChild(p);
			table.insertChild(caption, 1);  // because <head> is first
		} else {
			((Element)captions.get(0)).appendChild(p);
			XMLUtil.transferChildren(caption,  p);
		}
	}


	public static Integer getNumber(HtmlCaption caption) {
		Integer number = null;
		if (caption != null) {
			String value = caption.getValue();
			Matcher matcher = PATTERN.matcher(value);
			if (matcher.matches()) {
				String tableId = matcher.group(1);
				number = new Integer(tableId);
			}
		}
		return number;
	}

	/** default simple value without spaces or subscripts
	 * 
	 * @return
	 */
	public HtmlElement createHtmlElement() {
		caption = new HtmlCaption();
		HtmlElement captionBody = createHtmlThroughTextStructurer();
		if (captionBody == null) {
			throw new RuntimeException("Null caption");
		}
		captionBody = SVG2XMLUtil.removeStyles(captionBody);
		caption.appendChild(captionBody);
		caption.addAttribute(new Attribute("style", "border:1px solid blue"));
		return caption;
	}
	
	public String toString() {
		return getValue();
	}

}
