package org.contentmine.norma.sections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.AbstractMetadata.MetadataScheme;
import org.contentmine.graphics.html.HtmlMeta;

public class HtmlMetaJATSBuilder extends JATSBuilder {
	private static final Logger LOG = Logger.getLogger(HtmlMetaJATSBuilder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private Map<MetadataScheme, List<HtmlMeta>> metadataByScheme;
	private List<HtmlMeta> dcList;
	private List<HtmlMeta> hwList;

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
		jatsList.forEach(m -> System.out.println(m == null ? "null" : m.toXML()));
		JATSArticleElement article = tidyJATS(jatsList);
		return article;
		
	}

	private JATSArticleElement tidyJATS(List<JATSElement> jatsList) {
		
		JATSArticleElement article = new JATSArticleElement();
		JATSArticleMetaElement articleMeta = new JATSArticleMetaElement();
		article.appendChild(articleMeta);
		JATSJournalMetaElement journalMeta = new JATSJournalMetaElement();
		article.appendChild(journalMeta);
		
		for (JATSElement element : jatsList) {
			if (element == null) {
				
            } else if (element instanceof JATSAbstractElement) {
            	articleMeta.appendChild(element);
            } else if (element instanceof JATSAbstractElement) {
            } else if (element instanceof JATSContribElement) {
            	articleMeta.appendChild(element);
            } else if (element instanceof JATSEmailElement) {
            	articleMeta.appendChild(element);
            } else if (element instanceof JATSInstitutionElement) {
            	articleMeta.appendChild(element);
            } else if (element instanceof JATSContribIdElement) {
            	articleMeta.appendChild(element);
            } else if (element instanceof JATSArticleTitleElement) {
            	articleMeta.appendChild(element);
            } else {
            	System.err.println("Unsupported "+element);
            }
		}
		return article;
	}


}
