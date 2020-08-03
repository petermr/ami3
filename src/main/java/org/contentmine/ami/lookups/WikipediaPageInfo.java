package org.contentmine.ami.lookups;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlA;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlImg;
import org.contentmine.graphics.html.HtmlTd;
import org.contentmine.graphics.html.HtmlTr;
import org.contentmine.graphics.html.util.HtmlUtil;

/**
 * subpage for most Wikipedia entries.
 * metadata for page; probably best starting point.

 * Can get:
 *  Wikipedia page ID
 *  Wikidata Q number
 *  redirects (some synoyms, other are related conecpts without their own page)
 *  central description
 * 
 * 
 * @author pm286
 *
 */

//<h2 id="mw-pageinfo-header-basic"><span class="mw-headline" id="Basic_information">Basic information</span></h2>
//<table class="wikitable mw-page-info">
//<tr id="mw-pageinfo-display-title"><td style="vertical-align: top;">Display title</td><td>Black fly</td></tr>
//<tr id="mw-pageinfo-default-sort"><td style="vertical-align: top;">Default sort key</td><td>Black fly</td></tr>
//<tr id="mw-pageinfo-length"><td style="vertical-align: top;">Page length (in bytes)</td><td>14,348</td></tr>
//<tr id="mw-pageinfo-article-id"><td style="vertical-align: top;">Page ID</td><td>2422921</td></tr>
//<tr><td style="vertical-align: top;">Page content language</td><td>en - English</td></tr>
//<tr id="mw-pageinfo-content-model"><td style="vertical-align: top;">Page content model</td><td>wikitext</td></tr>
//<tr id="mw-pageinfo-robot-policy"><td style="vertical-align: top;">Indexing by robots</td><td>Allowed</td></tr>
//<tr id="mw-pageinfo-watchers"><td style="vertical-align: top;">Number of page watchers</td><td>Fewer than 30 watchers</td></tr>
//<tr><td style="vertical-align: top;"><a href="/w/index.php?title=Special:WhatLinksHere/Black_fly&amp;hidelinks=1&amp;hidetrans=1" title="Special:WhatLinksHere/Black fly">Number of redirects to this page</a></td><td>12</td></tr>
//<tr id="mw-pageinfo-contentpage"><td style="vertical-align: top;">Counted as a content page</td><td>Yes</td></tr>
//<tr id="mw-wikibase-pageinfo-entity-id"><td style="vertical-align: top;">Wikidata item ID</td><td><a class="extiw wb-entity-link external" href="https://www.wikidata.org/wiki/Special:EntityPage/Q720467">Q720467</a></td></tr>
//<tr id="mw-wikibase-pageinfo-description-central"><td style="vertical-align: top;">Central description</td><td>family of insects</td></tr>
//<tr id="mw-pageimages-info-label"><td style="vertical-align: top;">Page image</td><td><a href="/wiki/File:Simulium_trifasciatum_adult_(British_Entomology_by_John_Curtis-_765).png" class="image"><img alt="Simulium trifasciatum adult (British Entomology by John Curtis- 765).png" src="//upload.wikimedia.org/wikipedia/commons/thumb/7/76/Simulium_trifasciatum_adult_%28British_Entomology_by_John_Curtis-_765%29.png/220px-Simulium_trifasciatum_adult_%28British_Entomology_by_John_Curtis-_765%29.png" width="220" height="178" data-file-width="333" data-file-height="270" /></a></td></tr>
//<tr id="mw-pvi-month-count"><td style="vertical-align: top;">Page views in the past 30 days</td><td><div class="mw-pvi-month">7,808</div></td></tr>
//</table> 
//
 
//linked to from Wikipedia page by the title (name of WP page)
//https://en.wikipedia.org/w/index.php?title=Black_fly&action=info
//
//<h3 id="p-tb-label">Tools</h3>
//<div class="body">
//	<ul>
//		<li id="t-whatlinkshere"><a href="/wiki/Special:WhatLinksHere/Black_fly" title="List of all English Wikipedia pages containing links to this page [j]" accesskey="j">What links here</a></li>
//		<li id="t-recentchangeslinked"><a href="/wiki/Special:RecentChangesLinked/Black_fly" rel="nofollow" title="Recent changes in pages linked from this page [k]" accesskey="k">Related changes</a></li>
//		<li id="t-upload"><a href="/wiki/Wikipedia:File_Upload_Wizard" title="Upload files [u]" accesskey="u">Upload file</a></li>
//		<li id="t-specialpages"><a href="/wiki/Special:SpecialPages" title="A list of all special pages [q]" accesskey="q">Special pages</a></li>
//		<li id="t-permalink"><a href="/w/index.php?title=Black_fly&amp;oldid=870111579" title="Permanent link to this revision of the page">Permanent link</a></li>
//	**	<li id="t-info"><a href="/w/index.php?title=Black_fly&amp;action=info" title="More information about this page">Page information</a></li>
//		<li id="t-wikibase"><a href="https://www.wikidata.org/wiki/Special:EntityPage/Q720467" title="Link to connected data repository item [g]" accesskey="g">Wikidata item</a></li>
//		<li id="t-cite"><a href="/w/index.php?title=Special:CiteThisPage&amp;page=Black_fly&amp;id=870111579" title="Information on how to cite this page">Cite this page</a></li>				
//	</ul>
//</div>
//



 
public class WikipediaPageInfo {
	private static final String HTTPS = "https://";
	private static final Logger LOG = LogManager.getLogger(WikipediaPageInfo.class);
private static final String T_INFO = "t-info";
	public final static String WIKIDATA_BASE = "https://www.wikidata.org";
	public final static String WIKIPEDIA_BASE = "https://en.wikipedia.org";
	
	public final static String WIKIDATA_ITEM_ID_FIELD =	"mw-wikibase-pageinfo-entity-id";
	public final static String CENTRAL_DESCRIPTION = "mw-wikibase-pageinfo-description-central";
	public final static String PAGE_IMAGE = "mw-pageimages-info-label";
	
	private HtmlElement pageElement;

	public WikipediaPageInfo() {
		
	}
	
	/** create pageInfo from Wikipedia page element
	 * wikipediaPage typically retrieved by parsing a URL
	 * 
	 * @param wikipediaPage
	 * @return
	 */
	public static WikipediaPageInfo createPageInfo(HtmlElement wikipediaPage) {
		String xpath = ".//*[@" + HtmlElement.ID + "='" + T_INFO + "']/*[local-name()='"+HtmlA.TAG+"']";
		List<HtmlElement> aList = HtmlUtil.getQueryHtmlElements(wikipediaPage, xpath);
		HtmlA aElement = aList.size() != 1 ? null : (HtmlA) aList.get(0);
		WikipediaPageInfo pageInfo = null;
		if (aElement != null) {
			String urlS = null;
			HtmlElement pageElement = null;
			try {
				urlS = (WIKIPEDIA_BASE + aElement.getHref());
				pageElement = HtmlUtil.readAndCreateElement(urlS); 
			} catch (Exception e) {
				throw new RuntimeException("bad URL: "+urlS, e);
			}
			pageInfo = new WikipediaPageInfo();
			pageInfo.setPageElement(pageElement);
		}
		return pageInfo;
	}

	private void setPageElement(HtmlElement pageElement) {
		this.pageElement = pageElement;
	}
	
	/** get link wikidata item
	 * 
	 * @return a/href element (value will be WikidataId)
	 * 
	 */
	public HtmlA getLinkToWikidataItem() {
		String value = getValue(WIKIDATA_ITEM_ID_FIELD);
		String href = getHref(WIKIDATA_ITEM_ID_FIELD);
		HtmlA aElement = null;
		if (value != null && href != null) {
			LOG.debug(">"+value+"; "+(int)value.codePointAt(0));
			aElement = HtmlA.createFromHrefAndContent(href, value);
		}
		return aElement;
		
	}

	public String getCentralDescription() {
		return getValue(CENTRAL_DESCRIPTION);
	}

	/** gets the most prominent image on the page.
	 * 
	 * @return
	 */
	public HtmlImg getPageImage() {
		HtmlElement htmlElement = getElement(PAGE_IMAGE);
		HtmlImg img = null;
		if (htmlElement != null) {
			List<HtmlElement> elements = HtmlUtil.getQueryHtmlElements(htmlElement, ".//*[local-name()='" + HtmlImg.TAG + "']");
			img =  elements.size() == 1 ? (HtmlImg) elements.get(0) : null;
			if (img != null) {
				String src = HTTPS+img.getSrc();
				img.setSrc(src);
			}
		}
		return img;
	}

	private String getValue(String id) {
		HtmlTr htmlTr = (HtmlTr) getElement(id);
		HtmlTd td1 = htmlTr == null ? null : htmlTr.getTd(1);
		return td1 == null ? null : td1.getValue();
	}
	
	private HtmlElement getElement(String id) {
		List<HtmlElement> elements = HtmlUtil.getQueryHtmlElements(pageElement, ".//*[@id='"+id+"']");
		return elements.size() == 1 ? elements.get(0) : null;
	}
	
	private String getHref(String id) {
		HtmlElement htmlElement = getElement(id);
		return htmlElement == null ? null : HtmlA.getDescendantHref(htmlElement);
	}
	
}
