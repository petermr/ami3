/**
 *    Copyright 2011 Peter Murray-Rust et. al.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.contentmine.graphics.html;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.graphics.html.util.HtmlUtil;
import org.contentmine.norma.sections.JATSAbstractElement;
import org.contentmine.norma.sections.JATSArticleIdElement;
import org.contentmine.norma.sections.JATSArticleMetaElement;
import org.contentmine.norma.sections.JATSArticleTitleElement;
import org.contentmine.norma.sections.JATSContribElement;
import org.contentmine.norma.sections.JATSContribIdElement;
import org.contentmine.norma.sections.JATSElement;
import org.contentmine.norma.sections.JATSEmailElement;
import org.contentmine.norma.sections.JATSInstitutionElement;
import org.contentmine.norma.sections.JATSStringNameElement;


/** base class for lightweight generic SVG element.
 * no checking - i.e. can take any name or attributes
 * @author pm286
 *
 */
public class HtmlMeta extends HtmlElement {
	private static final Logger LOG = Logger.getLogger(HtmlMeta.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String TAG = "meta";
	public static final String CONTENT = "content";
	public static final String NAME = "name";
	public static final String HEAD_META_XPATH = ".//*[local-name()='"+HtmlHead.TAG+"']/*[local-name()='"+HtmlMeta.TAG+"']";

	private HtmlStyle style;
	
	/** constructor.
	 * 
	 */
	public HtmlMeta() {
		super(TAG);
	}

	public String getName() {
		return getAttributeValue(NAME);
	}

	public String getContent() {
		return getAttributeValue(CONTENT);
	}

	/** makes a new list composed of the metas in the list
	 * 
	 * @param elements
	 * @return
	 */
	public static List<HtmlMeta> extractMetas(List<HtmlElement> elements) {
		List<HtmlMeta> metaList = new ArrayList<HtmlMeta>();
		for (HtmlElement element : elements) {
			if (element instanceof HtmlMeta) {
				metaList.add((HtmlMeta) element);
			}
		}
		return metaList;
	}

	/** convenience method to extract list of HtmlMeta in element
	 * 
	 * @param htmlElement
	 * @return
	 */
	public static List<HtmlMeta> extractSelfAndDescendantMetas(HtmlElement htmlElement) {
		return HtmlMeta.extractMetas(HtmlUtil.getQueryHtmlElements(htmlElement, HEAD_META_XPATH));
	}

	/** convenience method to extract list of HtmlMeta in element
	 * 
	 * @param htmlElement
	 * @param xpath
	 * @return
	 */

	public static List<HtmlMeta> extractMetas(HtmlElement htmlElement, String xpath) {
		return HtmlMeta.extractMetas(HtmlUtil.getQueryHtmlElements(htmlElement, xpath));
	}

	public String toString() {
		return "\n"+this.getName() + " = " + this.getContent();
	}

	/** translates HW metadata to JATS equivalent.
	 * sometimes needs a compound element 
	 * these may need to be stitched together later
	 * 
	 * @return
	 */
	public JATSElement toJATS() {
		JATSElement jatsElement = null;
		String name = this.getName();
		String content = this.getContent();
		
		if (name == null) {
			
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_ARTICLE_TYPE)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_ARTICLE_TYPE);
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_ABSTRACT_HTML_URL)) {
			jatsElement = new JATSAbstractElement().setUrl(content); 
			
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_ABSTRACT)) {
			jatsElement = new JATSAbstractElement().appendText(content); 
			
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_AUTHOR)) {
        	/**
	<contrib contrib-type="author">
		<name>
			<surname>Huy</surname>
			<given-names>Rekol</given-names>
		</name>
		<role>Editor</role>
	</contrib>
        	 */
			jatsElement = new JATSContribElement().setContribType(JATSContribElement.AUTHOR).
					appendElement(new JATSStringNameElement(content)); 
			
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_AUTHOR_EMAIL)) {
			jatsElement = new JATSEmailElement().appendText(content);
			
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_AUTHOR_INSTITUTION)) {
			jatsElement = new JATSInstitutionElement().appendText(content);
			
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_AUTHOR_ORCID)) {
        	/**
        	 * <contrib-id contrib-id-type="orcid">http://orcid.org/0000-0003-2291-6821</contrib-id>
        	 */

			jatsElement = new JATSContribIdElement()
					.setAttribute(JATSContribIdElement.CONTRIB_ID_TYPE, JATSContribIdElement.ORCID); 
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_AUTHORS)) {
        	// is this ever used?
        	System.out.println("no processing for "+AbstractMetadata.CITATION_AUTHORS);
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_DATE)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_DATE);
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_DOI)) {
        	/**
    		<article-meta>
				<article-id pub-id-type="doi">10.1371/journal.pntd.0001477</article-id>
				*/

			jatsElement = new JATSArticleMetaElement()
					.appendElement(new JATSArticleIdElement()
					.setAttribute(JATSArticleIdElement.PUB_ID_TYPE, JATSArticleIdElement.DOI)
					)
					; 
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_FIRSTPAGE)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_FIRSTPAGE);
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_FULLTEXT_HTML_URL)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_FULLTEXT_HTML_URL);
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_ID)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_ID + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_ISSN)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_ISSN + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_JOURNAL_ABBREV)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_JOURNAL_ABBREV + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_JOURNAL_TITLE)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_JOURNAL_TITLE + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_LASTPAGE)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_LASTPAGE + "("+content+")");
        	
//        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_MJID)) {
//        	System.out.println("no processing for "+AbstractMetadata.CITATION_PDF_URL + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_NUM_PAGES)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_NUM_PAGES + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_PDF_URL)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_PDF_URL + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_PUBLIC_URL)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_PUBLIC_URL + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_PUBLICATION_DATE)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_PUBLIC_URL + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_PUBLISHER)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_PUBLISHER + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_REFERENCE)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_REFERENCE + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_SECTION)) {
        	System.out.println("no processing for "+AbstractMetadata.CITATION_SECTION + "("+content+")");
        	
        } else if (name.equalsIgnoreCase(AbstractMetadata.CITATION_TITLE)) {
			jatsElement = new JATSArticleTitleElement().appendText(content); 

		} else {
			System.err.println("Unprocessed element: "+name+"="+content);
		}
		return jatsElement;
	}
}
