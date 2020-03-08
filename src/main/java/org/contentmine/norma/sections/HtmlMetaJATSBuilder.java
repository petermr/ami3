package org.contentmine.norma.sections;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.metadata.AbstractMetadata.MetadataScheme;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlMeta;

import nu.xom.Element;

/**
 * creates a JATSArticleElement from the components produced by HtmlMeta (from HW, DC, etc.)
 * 
 * tries to gather them into structured JATS
 * @author pm286
 *
 */
public class HtmlMetaJATSBuilder extends JATSBuilder {

	private static final String LANDING_PAGE_HTML = "landingPage.html";
	private static final String LANDING_PAGE_METADATA_XML = "landingPageMetadata.xml";

	private static final Logger LOG = Logger.getLogger(HtmlMetaJATSBuilder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private static final String ANONYMOUS = "anonymous";
	
	private Map<MetadataScheme, List<HtmlMeta>> metadataByScheme;
	private List<HtmlMeta> dcList;
	private List<HtmlMeta> hwList;
	private JATS_TempElement temp;

	CProject cProject;

	private boolean outputLandingMetadata;

	private CTree currentTree;

	public Map<MetadataScheme, List<HtmlMeta>>  readMetaListsByMetadataScheme(List<HtmlMeta> metaList) {
		metadataByScheme = metaList.stream()
			.filter(meta -> Objects.nonNull(meta.getName()))
			.filter(meta -> (MetadataScheme.getScheme(meta.getName()) != null))
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
//				.peek(jats -> debugPrint(jats, "email"))
//				.peek(j -> debugPrint(j, "institution"))
//				.peek(j -> debugPrint(j, "orcid"))
				.collect(Collectors.toList())
				;
		JATSArticleElement article = tidyJATS(jatsList);
		if (outputLandingMetadata) {
			try {
				XMLUtil.debug(article, new File(createLandingPageMetadataFilename(currentTree)), 1);
			} catch (IOException e) {
				throw new RuntimeException("cannot write landing page", e);
			}
		}
		return article;
		
	}

	private String createLandingPageMetadataFilename(CTree currentTree) {
		File file = new File(currentTree.getDirectory(), LANDING_PAGE_METADATA_XML);
		return file.getAbsolutePath();
	}

	private JATSArticleElement tidyJATS(List<JATSElement> jatsList) {
		
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
		JATSArticleElement article = temp.getOrCreateSingleArticleChild();
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

	public void setCProject(CProject cProject) {
		this.cProject = cProject;
	}

	public void extractMetadataFromLandingPage() {
		for (CTree cTree : cProject.getOrCreateCTreeList()) {
			this.currentTree = cTree;
			List<HtmlMeta> metaList = HtmlMeta.createMetaList(new File(cTree.getDirectory(), LANDING_PAGE_HTML));
			Map<MetadataScheme, List<HtmlMeta>> map = readMetaListsByMetadataScheme(metaList);
			processHWList();
		}
	}

	// =====================
	private void debugPrint(JATSElement jats, String content) {
		System.out.print(jats != null && jats.toXML().trim().length() > 0 && jats.toXML().contains(content)? "HW "+content.toUpperCase()+" "+jats.toXML() + "\n": "");
	}

	public void setOutputLandingMetadata(boolean b) {
		this.outputLandingMetadata = b;
	}



}
