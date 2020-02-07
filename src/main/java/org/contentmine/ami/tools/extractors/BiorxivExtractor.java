package org.contentmine.ami.tools.extractors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.eucl.xml.XMLUtil;

import nu.xom.Element;

/** extracts from biorxiv pages
 * 
 * 

 * @author pm286
 *
 */
public class BiorxivExtractor extends AbstractExtractor {
	static final Logger LOG = Logger.getLogger(BiorxivExtractor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	private static final String HIGHWIRE_SEARCH_RESULTS_LIST = "highwire-search-results-list";
	public static final String BIORXIV_BASE = "https://www.biorxiv.org";
	public static final String BIORXIV_SEARCH = BIORXIV_BASE+"/search/";
	
	public BiorxivExtractor() {
		init();
	}

	private void init() {
		this.setBase(BIORXIV_BASE);
	}

	public BiorxivExtractor(CProject cProject) {
		super(cProject);
		init();
	}

	/**
    https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank?page=1
	 */

	/**
	 * <ul class="highwire-search-results-list">
	 <li class="first odd search-result result-jcode-biorxiv search-result-highwire-citation">
	 * @return 
	 */
	public List<AbstractMetadata> extractSearchResultsIntoMetadata(String result) {
		Element element = XMLUtil.parseCleanlyToXML(result);
		List<Element> ulList = XMLUtil.getQueryElements(element, 
				".//*[local-name()='ul' and @class='" + HIGHWIRE_SEARCH_RESULTS_LIST + "']");
		if (ulList.size() == 0) {
//			System.out.println(result);
//			throw new RuntimeException("empty array");
			System.err.println("empty array");
			return new ArrayList<>();
		}
		Element ul = ulList.get(0);
		return createMetadataList(ul);
	}

	protected AbstractMetadata createMetadata(Element liElement) {
		BiorxivMetadata metadata = new BiorxivMetadata(this);
		metadata.read(liElement);
		return metadata;
	}

	@Override
	protected String getDOIFromUrl(String fullUrl) {
		if (fullUrl == null) return null;
		String[] parts = fullUrl.split("content/");
		return parts[1];
	}
	
}
