package org.contentmine.ami.lookups;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.util.HtmlUtil;

import nu.xom.Attribute;
import nu.xom.Element;

public class WikiResult {
	private static final Logger LOG = LogManager.getLogger(WikiResult.class);
public static final String WIKIDATA = "wikidata";
	public static final String PROPERTY2 = "Property:";

	private String href;
	private String title;
	private String description;
	private String qpString;
	private String label;
	private String property;
	private String item;

	/**
	<li>
	<div class="mw-search-result-heading">
	 <a href="/wiki/Q1887740" title="‎Larus‎ | ‎genus of birds‎" data-serp-pos="0">
		<span class="wb-itemlink">
		  <span class="wb-itemlink-label" lang="en" dir="ltr">
		    <span class="searchmatch">Larus</span>
		  </span>
		  <span class="wb-itemlink-id">(Q1887740)</span>
		</span>
	 </a>
	</div>
	<div class="searchresult">
	 <span class="wb-itemlink-description">genus of birds</span>
	</div>
	</li>
	
	 * @param li
	 * @return
	 */
	public static WikiResult extractWikiResult(HtmlElement li) {
		WikiResult wikiResult = new WikiResult();
		wikiResult.extractResult(li);
		return wikiResult;
	}

	public static List<WikiResult> extractWikiResultList(List<HtmlElement> liList) {
		List<WikiResult> resultList = new ArrayList<WikiResult>();
		for (HtmlElement li : liList) {
			WikiResult wikiResult = WikiResult.extractWikiResult((HtmlElement)li);
			resultList.add(wikiResult);
		}
		if (resultList.size() == 1) {
//			LOG.debug("singleton: "+resultList.get(0));
		}
		return resultList;
	}

	public String getQString() {
		return qpString;
	}

	/**
	 * <wikidata item="Q123" property="P456" label="foo" description="bar"/>
	 * @return
	 */
	public Element toXML() {
		Element element = new Element(WIKIDATA);
		if (item != null) {
			element.addAttribute(new Attribute(WikipediaLookup.ITEM, item));
		}
		if (property != null) {
			element.addAttribute(new Attribute(WikipediaLookup.PROPERTY, property));
		}
		if (label != null) {
			element.addAttribute(new Attribute(WikipediaLookup.LABEL, label));
		}
		if (description != null) {
			element.addAttribute(new Attribute(WikipediaLookup.DESCRIPTION, description));
		}
		return element;
	}

	public void extractResult(HtmlElement li) {
		HtmlDiv div0 = (HtmlDiv) XMLUtil.getQueryElements(li, 
				"./"+HtmlUtil.elem(HtmlDiv.TAG)).get(0);
		HtmlA a = (HtmlA) XMLUtil.getSingleElement(div0, 
				"./" + HtmlUtil.elem(HtmlA.TAG));
		if (a != null) {
			href = a.getHref();
			qpString = href.replace("/wiki/", "");
			if (qpString != null && qpString.startsWith(PROPERTY2)) {
				property = qpString.substring(PROPERTY2.length());
			} else {
				item = qpString;
			}
//			LOG.warn("WIKICHARS "+chars(a)+a.toXML());
			title = a.getTitle();
			int idx = title.indexOf("|");
			label = idx == -1 ? "" : title.substring(0, idx).trim();
			description = idx == -1 ? "" : title.substring(idx + 1).trim();
		}
		HtmlDiv div1 = (HtmlDiv) XMLUtil.getQueryElements(li, 
				"./" + HtmlUtil.elem(HtmlDiv.TAG)).get(1);
		if (div1 != null) {
			HtmlSpan span = (HtmlSpan) XMLUtil.getSingleElement(div1, 
					"./" + HtmlUtil.elem(HtmlSpan.TAG));
			description = (span == null) ? null : span.getValue();
		}
	}

	private String chars(HtmlA a) {
		String s = a.toXML();
		StringBuilder sb = new StringBuilder(s+"?"+s.length()+"?");
		for (int i = 0; i < s.length(); i++) {
			sb.append(Integer.toHexString((int)s.charAt(i))+ " ");
		}
		return sb.toString();
	}

	public String getDescription() {
		return description;
	}

	public String getLabel() {
		return label;
	}
}
