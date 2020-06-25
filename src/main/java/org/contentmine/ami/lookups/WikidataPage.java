package org.contentmine.ami.lookups;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlMeta;
import org.contentmine.graphics.html.util.HtmlUtil;

/** HTML page on Wikidata.
 * Allows some fields to be extracted
 * 
 * @author pm286
 *
** metadata
<meta property="og:title" content="Asian tiger mosquito"/>
<meta name="description" content="species of insect"/>
<meta property="og:description" content="species of insect"/>
<meta name="twitter:card" content="summary"/>
<meta property="og:image" content="https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Aedes_Albopictus.jpg/1200px-Aedes_Albopictus.jpg"/>
 *
 ** aliases in table. Note en only. There are different aliases in different languages
 *
 *<td class="wikibase-entitytermsforlanguageview-aliases"><div class="wikibase-aliasesview ">
<ul class="wikibase-aliasesview-list" dir="ltr" lang="en">
  <li class="wikibase-aliasesview-list-item">Stegomyia albopicta</li>
  <li class="wikibase-aliasesview-list-item">Forest day mosquito</li>
  <li class="wikibase-aliasesview-list-item">Aedes albopictus</li>
</ul>
 */
public class WikidataPage {
	private static final Logger LOG = LogManager.getLogger(WikidataPage.class);
private HtmlHtml html;

	public static WikidataPage createPage(HtmlHtml html) {
		WikidataPage wikidataPage = null;
		if (html != null) {
			wikidataPage = new WikidataPage();
			wikidataPage.html = html;
		}
		return wikidataPage;
	}
	
	public String getDescription() {
		List<HtmlMeta> metaList = html.getHead().getMetaElements();
		List<HtmlElement> descList = HtmlUtil.getQueryHtmlElements(
				metaList, "./*[@property='og.description' or @name='description']");
		return descList.size() == 0 ? null : ((HtmlMeta)descList.get(0)).getContent();
	}

	public String getTitle() {
		List<HtmlMeta> metaList = html.getHead().getMetaElements();
		List<HtmlElement> contentList = HtmlUtil.getQueryHtmlElements(metaList, "./*[@property='og.title']");
		return contentList.size() == 0 ? null : ((HtmlMeta)contentList.get(0)).getContent();
	}

	public String getImageUrl() {
		List<HtmlMeta> metaList = html.getHead().getMetaElements();
		List<HtmlElement> contentList = HtmlUtil.getQueryHtmlElements(metaList, "./*[@property='og.image']");
		return contentList.size() == 0 ? null : ((HtmlMeta)contentList.get(0)).getContent();
	}
	
	/**
 *<td class="wikibase-entitytermsforlanguageview-aliases"><div class="wikibase-aliasesview ">
<ul class="wikibase-aliasesview-list" dir="ltr" lang="en">
  <li class="wikibase-aliasesview-list-item">Stegomyia albopicta</li>
  <li class="wikibase-aliasesview-list-item">Forest day mosquito</li>
  <li class="wikibase-aliasesview-list-item">Aedes albopictus</li>
</ul>
	 * @return
	 */
	public List<String> getAliasList() {
		List<String> aliasList = new ArrayList<>();
		if (html != null) {
			aliasList = HtmlUtil.getQueryHtmlStrings(html, ".//*[@class='wikibase-aliasesview-list-item']");
		}
		return aliasList;
	}

}
