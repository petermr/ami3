package org.contentmine.cproject.metadata.quickscrape;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.metadata.AbstractMetadata;

public class QuickscrapeMD extends AbstractMetadata {
	
	

//	citation_springer_api_url x 117

	static final Logger LOG = Logger.getLogger(QuickscrapeMD.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	

	static {
        TERMS.add(ABSTRACT);
        TERMS.add(ABSTRACT_HTML);
        TERMS.add(AUTHOR);
        TERMS.add(AUTHORS);
        TERMS.add(CONTRIBUTORS);
        TERMS.add(COPYRIGHT);
        TERMS.add(CREATORS);
        TERMS.add(DATE);
        TERMS.add(DESCRIPTION);
        TERMS.add(DOI);
        TERMS.add(FIGURE);
        TERMS.add(FIGURE_CAPTION);
        TERMS.add(FIRST_PAGE);
        TERMS.add(FULLTEXT_HTML);
        TERMS.add(FULLTEXT_PDF);
        TERMS.add(FULLTEXT_XML);
        TERMS.add(IDENTIFIER);
        TERMS.add(ISSN);
        TERMS.add(ISSUE);
        TERMS.add(JOURNAL);
        TERMS.add(LANGUAGE);
        TERMS.add(LAST_PAGE);
        TERMS.add(LICENSE);
        TERMS.add(PUBLISHER);
        TERMS.add(SOURCE);
        TERMS.add(SUPP_MATERIAL);
        TERMS.add(TITLE);
        TERMS.add(VOLUME);
        TERMS.add(TYPE);
        TERMS.add(URL);
	}
	
	// this is an anomaly // should be singular?
	public static final String OLD_VERSION = "oldVersion";
	public static final String CTREE_RESULT_JSON_OLD = "results.json";
	private static final String CTREE_RESULT_JSON = "quickscrape_result.json";
	// doesn't yet exist - and may never
	private static final String CPROJECT_RESULT_JSON = "quickscrape_results.json";

	private static final String VALUE = ".value";
	private static final String $_ABSTRACT_VALUE      = "$."+ABSTRACT+VALUE;
	private static final String $_AUTHOR_INSTITUTION  = "$."+AUTHOR_INSTITUTION+VALUE;
	private static final String $_AUTHOR_VALUE        = "$."+AUTHOR+VALUE;
	private static final String $_COPYRIGHT_VALUE     = "$."+COPYRIGHT+VALUE;
	private static final String $_DATE_VALUE          = "$."+DATE+VALUE;
	private static final String $_DESCRIPTION_VALUE   = "$."+DESCRIPTION+VALUE;
	private static final String $_DOI_VALUE           = "$."+DOI+VALUE;
	private static final String $_FIRSTPAGE_VALUE     = "$."+FIRST_PAGE+VALUE;
	private static final String $_FULLTEXT_HTML_VALUE = "$."+FULLTEXT_HTML+VALUE;
	private static final String $_FULLTEXT_PDF_VALUE  = "$."+FULLTEXT_PDF+VALUE;
	private static final String $_FULLTEXT_XML_VALUE  = "$."+FULLTEXT_XML+VALUE;
	private static final String $_ISSN_VALUE          = "$."+ISSN+VALUE;
	private static final String $_ISSUE_VALUE         = "$."+ISSUE+VALUE;
	private static final String $_JOURNAL_VALUE       = "$."+JOURNAL+VALUE;
	private static final String $_LICENSE_VALUE       = "$."+LICENSE+VALUE;
	private static final String $_PUBLISHER_VALUE     = "$."+PUBLISHER+VALUE;
	private static final String $_TITLE_VALUE         = "$."+TITLE+VALUE;
	private static final String $_URL_VALUE           = "$."+URL+VALUE;
	private static final String $_VOLUME_VALUE        = "$."+VOLUME+VALUE;
	
	public QuickscrapeMD() {
		super();
		hasQuickscrapeMetadata = true;
	}
	
	public static AbstractMetadata createMetadata() {
		return new QuickscrapeMD();
	}

	@Override
	public String getAbstract() {
		return getJsonArrayStringByPath($_ABSTRACT_VALUE);
	}

	@Override
	public String getAbstractURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthorEmail() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAuthorListAsStrings() {
		return getJsonArrayByPath($_AUTHOR_VALUE);
	}

	@Override
	public String getAuthorInstitution() {
		return getJsonValueOrHtmlMetaContent($_AUTHOR_INSTITUTION, new String[] {CITATION_AUTHOR_INSTITUTION});
	}

	@Override
	public String getCitations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCopyright() {
		return getJsonArrayStringByPath($_COPYRIGHT_VALUE);
	}

	@Override
	public String getCreator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDate() {
		return getJsonArrayStringByPath($_DATE_VALUE);
	}

	@Override
	public String getDescription() {
		return getJsonArrayStringByPath($_DESCRIPTION_VALUE);
	}

	@Override
	public String getDOI() {
		return getJsonArrayStringByPath($_DOI_VALUE);
	}

	@Override
	public String getFirstPage() {
		return getJsonArrayStringByPath($_FIRSTPAGE_VALUE);
	}

	@Override
	public String getFulltextHTMLURL() {
		return getJsonArrayStringByPath($_FULLTEXT_HTML_VALUE);
	}

	@Override
	public String getFulltextPDFURL() {
		return getJsonArrayStringByPath($_FULLTEXT_PDF_VALUE);
	}

	@Override
	public String getFulltextPublicURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFulltextXMLURL() {
		return getJsonArrayStringByPath($_FULLTEXT_XML_VALUE);
	}

	@Override
	public String getISSN() {
		return getJsonValueOrHtmlMetaContent($_ISSN_VALUE, new String[] {CITATION_ISSN});
	}

	@Override
	public String getIssue() {
		return getJsonArrayStringByPath($_ISSUE_VALUE);
	}

	@Override
	public String getJournal() {
		return getJsonValueOrHtmlMetaContent($_JOURNAL_VALUE, new String[] {CITATION_JOURNAL_TITLE});
	}

	@Override
	public String getKeywords() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLastPage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLicense() {
		return getJsonValueOrHtmlMetaContent($_LICENSE_VALUE, new String[] {DC_RIGHTS});		
	}

	@Override
	public String getPublicURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPublisher() {
		return getJsonValueOrHtmlMetaContent($_PUBLISHER_VALUE, new String[] {CITATION_PUBLISHER, DC_PUBLISHER});
	}

	@Override
	public String getReferenceCount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRights() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		return getJsonArrayStringByPath($_TITLE_VALUE);
	}


	@Override
	public String getURL() {
		return getJsonArrayStringByPath($_URL_VALUE);
	}

	@Override
	public String getVolume() {
		return getJsonArrayStringByPath($_VOLUME_VALUE);
	}

	@Override
	public String getLinks() {
		return null;
	}

	@Override
	public String getPrefix() {
		return null;
	}

	@Override 
	public String hasQuickscrapeMetadata() {
		hasQuickscrapeMetadata = (cTree != null && cTree.getExistingQuickscrapeMD() != null);
		return hasQuickscrapeMetadata ? "Y" : "N";
	}
	
	@Override
	public String getCTreeMetadataFilename() {
		if (OLD_VERSION.equals(version)) {
			return CTREE_RESULT_JSON_OLD;
		}
		return CTREE_RESULT_JSON;
	}

	@Override
	protected String getCProjectMetadataFilename() {
		return CPROJECT_RESULT_JSON;
	}

}
