package org.contentmine.cproject.metadata.html;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.cproject.metadata.AbstractMetadata;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlMeta;
import org.contentmine.norma.sections.HtmlMetaJATSBuilder;
import org.contentmine.norma.sections.JATSArticleElement;
import org.contentmine.norma.sections.JATSElement;

/**
 * 
 * @author pm286
 *
 */
/**
DUMMY
May become parent class for DublinCore , Highwire, Prism, etc.
Or these may become separate classes.
The whole thing is a mess.
 */
public class HtmlMD extends AbstractMetadata {
	
	static final Logger LOG = Logger.getLogger(HtmlMD.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String LANDING_PAGE_METADATA_XML = "landingPageMetadata.xml";

	private static final String CTREE_RESULT_XML = "html_metadata.xml";
	private static final String CPROJECT_RESULT_XML = "html_metadata.xml";

	private boolean outputLandingMetadata = true;

	private HtmlMetaJATSBuilder htmlMetaJATSBuilder;
	
	public HtmlMD() {
		super();
	}
	
	public static AbstractMetadata createMetadata() {
		return new HtmlMD();
	}


//	@Override
//	public String getAuthorString() {
//		return getJsonStringByPath(AUTHOR_STRING);
//	}

	
//	@Override
//	public String getDOI() {
//		return getJsonStringByPath(DOI);
//	}

//
//	public String getID() {
//		return getPMCID();
//	}

//	@Override
//	public String getJournal() {
//		return getJsonStringByPath(JOURNAL_INFO);
//	}

//	public String getPMCID() {
//		return getJsonStringByPath(PMCID);
//	}

//	@Override
//	public String getTitle() {
//		return getJsonStringByPath(TITLE);
//	}
	
//	@Override
//	protected String getCTreeMetadataFilename() {
//		return CTREE_RESULT_JSON;
//	}
//
//	@Override
//	protected String getCProjectMetadataFilename() {
//		return CPROJECT_RESULT_JSON;
//	}

	public void extractMetadataFromFile(File file, HtmlMetadataScheme scheme) {
		getOrCreateHtmlMetaJATSBuilder();
		List<HtmlMeta> metaList = HtmlMeta.createMetaList(file);
		Map<HtmlMetadataScheme, List<HtmlMeta>> map = htmlMetaJATSBuilder.readMetaListsByMetadataScheme(metaList);
		processHWList();
	}

	private HtmlMetaJATSBuilder getOrCreateHtmlMetaJATSBuilder() {
		if (htmlMetaJATSBuilder == null) {
			htmlMetaJATSBuilder = new HtmlMetaJATSBuilder();
		}
		return htmlMetaJATSBuilder;
	}

	public JATSArticleElement processHWList() {
		getOrCreateHtmlMetaJATSBuilder();
		List<JATSElement> jatsList = htmlMetaJATSBuilder.getOrCreateHWList().stream()
				.map(hw -> hw.toJATS())
//				.peek(jats -> debugPrint(jats, "email"))
//				.peek(j -> debugPrint(j, "institution"))
//				.peek(j -> debugPrint(j, "orcid"))
				.collect(Collectors.toList())
				;
		JATSArticleElement article = htmlMetaJATSBuilder.tidyJATS(jatsList);
		if (outputLandingMetadata) {
			try {
				XMLUtil.debug(article, new File(createLandingPageMetadataFilename(cTree)), 1);
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

	public boolean isOutputLandingMetadata() {
		return outputLandingMetadata;
	}

	public void setOutputLandingMetadata(boolean outputLandingMetadata) {
		this.outputLandingMetadata = outputLandingMetadata;
	}

	@Override
	protected String getCTreeMetadataFilename() {
		return CTREE_RESULT_XML;
	}

	@Override
	protected String getCProjectMetadataFilename() {
		return CPROJECT_RESULT_XML;
	}




	

}
