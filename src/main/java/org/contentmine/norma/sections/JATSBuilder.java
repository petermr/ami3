package org.contentmine.norma.sections;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import nu.xom.Element;

/** build JATS elements from components
 * can take non-JATS inputs (e.g. HTML)
 * 
 * @author pm286
 *
 */
public class JATSBuilder {
	private static final Logger LOG = Logger.getLogger(JATSBuilder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	static final String ANONYMOUS = "anonymous";

	public enum BuilderType {
		HTML,
		JATS
		;
	}

	private BuilderType type;
	private JATSElement temp;

	public void setType(BuilderType type) {
		this.type = type;
	}

	public JATSArticleElement tidyJATS(List<JATSElement> jatsList) {
		
		temp = new JATS_TempElement();
		
		for (JATSElement element : jatsList) {
			if (element == null) {
				
	        } else if (element instanceof JATSAbstractElement) {
	        	getOrCreateArticleMetaElement().appendChild(element);
	        	
	        } else if (element instanceof JATSArticleElement) {
	        	// unlikely
	        	getOrCreateArticleElement();
	        	
	        } else if (element instanceof JATSArticleIdElement) {
	        	// creates an article with this ID
	        	getOrCreateArticleElement().appendElement(element);
	        	
	        } else if (element instanceof JATSArticleTitleElement) {
	        	getOrCreateArticleTitleGroupElement().appendElement(element);
	        	
	        } else if (element instanceof JATSContribElement) {
	        	getOrCreateContribGroupElement().appendElement(element);
	        	
	        } else if (element instanceof JATSContribIdElement) {
	        	getOrCreateLastContribElement().appendElement(element);
	        	
	        } else if (element instanceof JATSDateElement) {
	        	getOrCreateArticleMetaElement().appendElement(element);
	
	        } else if (element instanceof JATSExtLinkElement) {
	        	getOrCreateArticleMetaElement().appendElement(element);
	        	
	        } else if (element instanceof JATSEmailElement) {
	        	getOrCreateLastContribElement().appendElement(element);
	        	
	        } else if (element instanceof JATSFpageElement) {
	        	getOrCreateArticleMetaElement().appendElement(element);
	        	
	        } else if (element instanceof JATSInstitutionElement) {
	        	getOrCreateLastContribElement().appendElement(element);
	        	
	        } else if (element instanceof JATSJournalTitleElement) {
	        	getOrCreateJournalTitleGroupElement().appendElement(element);
	        	
	        } else if (element instanceof JATSLpageElement) {
	        	getOrCreateArticleMetaElement().appendElement(element);
	        	
	        } else if (element instanceof JATSPublisherElement) {
	        	getOrCreateJournalMetaElement().appendElement(element);
	        	
	        } else if (element instanceof JATSPageCountElement) {
	        	getOrCreateCountsElement().appendElement(element);
	        	
	        } else if (element instanceof JATSRefElement) {
	        	getOrCreateRefListElement().appendElement(element);
	        	
	        } else if (element instanceof JATSSecElement) {
	        	getOrCreateBodyElement().appendElement(element);
	        	
	        } else {
	        	System.err.println("HtmlMetaJATSBuilder Unsupported "+element);
	        }
		}
		JATSArticleElement article = ((JATS_TempElement)temp).getOrCreateSingleArticleChild();
		article.detach();
		return article;
	}

	/** get last contributor.
	 * if it doesn't exist (probably an error in document) create one
	 * with string-name = anonymous
	 * 
	 * @return
	 */
	public JATSContribElement getOrCreateLastContribElement() {
		List<Element> contribElements = getOrCreateContribGroupElement().getContribChildElements();
		JATSContribElement jce = (JATSContribElement) 
			(
				(contribElements.size() == 0) ? 
						(JATSContribElement) getOrCreateContribGroupElement().appendAndReturnElement(
						new JATSContribElement().appendElement(new JATSStringNameElement(ANONYMOUS))) :
				(JATSContribElement) contribElements.get(contribElements.size() - 1)
			);
		return jce;
	
	}

	public JATSRefListElement getOrCreateRefListElement() {
		return getOrCreateBackElement().getOrCreateSingleRefListChild();
	}

	public JATSArticleElement getOrCreateArticleElement() {
		return ((JATS_TempElement)temp).getOrCreateSingleArticleChild();
	}

	public JATSFrontElement getOrCreateFrontElement() {
		return getOrCreateArticleElement().getOrCreateSingleFrontChild();
	}

	public JATSBodyElement getOrCreateBodyElement() {
		return getOrCreateArticleElement().getOrCreateSingleBodyChild();
	}

	public JATSBackElement getOrCreateBackElement() {
		return getOrCreateArticleElement().getOrCreateSingleBackChild();
	}

	public JATSArticleMetaElement getOrCreateArticleMetaElement() {
		return getOrCreateFrontElement().getOrCreateSingleArticleMetaChild();
	}

	public JATSJournalMetaElement getOrCreateJournalMetaElement() {
		return getOrCreateFrontElement().getOrCreateSingleJournalMetaChild();
	}

	public JATSTitleGroupElement getOrCreateArticleTitleGroupElement() {
		return getOrCreateArticleMetaElement().getOrCreateSingleTitleGroupChild();
	}

	public JATSTitleGroupElement getOrCreateJournalTitleGroupElement() {
		return getOrCreateJournalMetaElement().getOrCreateSingleTitleGroupChild();
	}

	public JATSContribGroupElement getOrCreateContribGroupElement() {
		return getOrCreateArticleMetaElement().getOrCreateSingleContribGroupChild();
	}

	public JATSCountsElement getOrCreateCountsElement() {
		return getOrCreateArticleMetaElement().getOrCreateSingleCountsChild();
	}

}
