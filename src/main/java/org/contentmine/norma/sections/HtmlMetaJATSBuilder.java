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
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.cproject.metadata.AbstractMetadata.HtmlMetadataScheme;
import org.contentmine.cproject.metadata.crossref.CrossrefMD;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlMeta;

/**
 * creates a JATSArticleElement from the components produced by HtmlMeta (from HW, DC, etc.)
 * 
 * tries to gather them into structured JATS
 * 
 * This is messy because metadata is messy.
 * 
 * @author pm286
 *
 */
public class HtmlMetaJATSBuilder extends JATSBuilder {

	private static final String LANDING_PAGE_HTML = "landingPage.html";
	private static final String CROSSREF_XML = "crossref.xml";

	private static final Logger LOG = Logger.getLogger(HtmlMetaJATSBuilder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	
	private Map<HtmlMetadataScheme, List<HtmlMeta>> metadataByScheme;
	private List<HtmlMeta> dcList;
	private List<HtmlMeta> hwList;
	JATS_TempElement temp;

	CProject cProject;

	private boolean outputLandingMetadata;

	private CTree currentTree;

	public Map<HtmlMetadataScheme, List<HtmlMeta>>  readMetaListsByMetadataScheme(List<HtmlMeta> metaList) {
		metadataByScheme = metaList.stream()
			.filter(meta -> Objects.nonNull(meta.getName()))
			.filter(meta -> (HtmlMetadataScheme.getScheme(meta.getName()) != null))
			.collect(Collectors.groupingBy(
			    meta -> HtmlMetadataScheme.getScheme(meta.getName())
			    )
			);
		hwList = metadataByScheme.get(HtmlMetadataScheme.HW);
		dcList = metadataByScheme.get(HtmlMetadataScheme.DC);
		return metadataByScheme;
	}
	
	public Map<HtmlMetadataScheme, List<HtmlMeta>> getMetadataByScheme() {
		return metadataByScheme;
	}

	public List<HtmlMeta> getOrCreateDCList() {
		return dcList;
	}

	public List<HtmlMeta> getOrCreateHWList() {
		return hwList;
	}

	public void setCProject(CProject cProject) {
		this.cProject = cProject;
	}


	/** currently a real mess as the various metadata schems have no common structire.
	 * 
	 * @param metadata
	 * @param metadataScheme
	 */
	public void extractMetadataFromCProject(
			AbstractMetadata metadata) {
		extractMetadataFromCProject(metadata, null);
	}

	/** currently a real mess as the various metadata schems have no common structire.
	 * 
	 * @param metadata
	 * @param metadataScheme
	 */
	public void extractMetadataFromCProject(
			AbstractMetadata metadata, HtmlMetadataScheme metadataScheme) {
		for (CTree cTree : cProject.getOrCreateCTreeList()) {
			this.currentTree = cTree;
			if (metadataScheme.equals(HtmlMetadataScheme.HW)) {
				File file = new File(cTree.getDirectory(), LANDING_PAGE_HTML); 
				metadata.extractMetadataFromFile(file);
			} else if (metadata instanceof CrossrefMD) {
				File file = new File(cTree.getDirectory(), CROSSREF_XML); 
				metadata.extractMetadataFromFile(file);
			}
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
