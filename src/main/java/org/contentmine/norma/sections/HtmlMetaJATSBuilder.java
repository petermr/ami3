package org.contentmine.norma.sections;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.metadata.AbstractMetadata.MetadataScheme;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlMeta;

import nu.xom.Element;

public class HtmlMetaJATSBuilder extends JATSBuilder {
	private static final Logger LOG = Logger.getLogger(HtmlMetaJATSBuilder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private Map<MetadataScheme, List<HtmlMeta>> metadataByScheme;
	private List<HtmlMeta> dcList;
	private List<HtmlMeta> hwList;
	private JATS_TempElement temp;

	public Map<MetadataScheme, List<HtmlMeta>>  readMetaListsByMetadataScheme(List<HtmlMeta> metaList) {
		metadataByScheme = metaList.stream()
			.filter(meta -> Objects.nonNull(meta.getName()))
			.filter(meta -> (MetadataScheme.getScheme(meta.getName()) != null))
//				.peek(meta -> System.out.println(meta.getName()))
			.collect(Collectors.groupingBy(
			    meta -> MetadataScheme.getScheme(meta.getName())
			    )
			);
		hwList = metadataByScheme.get(MetadataScheme.HW);
		dcList = metadataByScheme.get(MetadataScheme.DC);
		return metadataByScheme;
	}
	
	public Map<MetadataScheme, List<HtmlMeta>> getMetadataByScheme() {
		return metadataByScheme;
	}

	public List<HtmlMeta> getOrCreateDCList() {
		return dcList;
	}

	public List<HtmlMeta> getOrCreateHWList() {
		return hwList;
	}

	public JATSArticleElement processHWList() {
		List<JATSElement> jatsList = getOrCreateHWList().stream()
				
				.map(hw -> hw.toJATS())
				.collect(Collectors.toList())
				;
//		jatsList.forEach(m -> System.out.println(m == null ? "null" : m.toXML()));
		JATSArticleElement article = tidyJATS(jatsList);
		return article;
		
	}

	private JATSArticleElement tidyJATS(List<JATSElement> jatsList) {
		
		temp = new JATS_TempElement();
		
		for (JATSElement element : jatsList) {
			if (element == null) {
				
            } else if (element instanceof JATSAbstractElement) {
            	getOrCreateArticleMetaElement().appendChild(element);
            	
            } else if (element instanceof JATSArticleElement) {
            	getOrCreateArticleElement();
            	
            } else if (element instanceof JATSArticleIdElement) {
            	getOrCreateArticleElement().appendChild(element);
            	
            } else if (element instanceof JATSArticleTitleElement) {
            	getOrCreateArticleTitleGroupElement().appendChild(element);
            	
            } else if (element instanceof JATSContribElement) {
            	getOrCreateContribGroupElement().appendChild(element);
            	
            } else if (element instanceof JATSContribIdElement) {
            	getOrCreateLastContribElement().appendChild(element);
            	
            } else if (element instanceof JATSDateElement) {
            	getOrCreateArticleMetaElement().appendChild(element);

            } else if (element instanceof JATSExtLinkElement) {
            	getOrCreateArticleMetaElement().appendChild(element);
            	
            } else if (element instanceof JATSEmailElement) {
            	getOrCreateLastContribElement().appendChild(element);
            	
            } else if (element instanceof JATSFpageElement) {
            	getOrCreateArticleMetaElement().appendChild(element);
            	
            } else if (element instanceof JATSInstitutionElement) {
            	getOrCreateLastContribElement().appendChild(element);
            	
            } else if (element instanceof JATSJournalTitleElement) {
            	getOrCreateJournalTitleGroupElement().appendChild(element);
            	
            } else if (element instanceof JATSLpageElement) {
            	getOrCreateArticleMetaElement().appendChild(element);
            	
            } else if (element instanceof JATSPublisherElement) {
            	getOrCreateJournalMetaElement().appendChild(element);
            	
            } else if (element instanceof JATSPageCountElement) {
            	getOrCreateCountsElement().appendChild(element);
            	
            } else if (element instanceof JATSRefElement) {
            	getOrCreateRefListElement().appendChild(element);
            	
            } else if (element instanceof JATSSecElement) {
            	getOrCreateBodyElement().appendChild(element);
            	
            } else {
            	System.err.println("Unsupported "+element);
            }
		}
		JATSArticleElement article = temp.getOrCreateSingleArticleChild();
		return article;
	}

	private JATSContribElement getOrCreateLastContribElement() {
		List<Element> contribElements = 
				XMLUtil.getQueryElements(getOrCreateArticleMetaElement(), 
						"./*[local-name='"+JATSContribElement.TAG+"']");
		return (contribElements.size() == 0) ?
			(JATSContribElement) new JATSContribElement()
					.appendElement(new JATSContribElement()
					.appendElement(new JATSStringNameElement("anonymous"))) :
		(JATSContribElement) contribElements.get(contribElements.size() - 1);
	}

	public JATSRefListElement getOrCreateRefListElement() {
		return getOrCreateBackElement().getOrCreateSingleRefListChild();
	}

	public JATSArticleElement getOrCreateArticleElement() {
		return temp.getOrCreateSingleArticleChild();
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
