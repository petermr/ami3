package org.contentmine.ami.tools.download;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.xml.XMLUtil;

/** the first page a user/agent sees gievn a citation link
 * it normally has title, authors
 * often has an abstract
 * should have links to fulltext HTML and/or PDF
 * normally Biorxiv does not have fulltext landing pages
 * 
 * 

 * @author pm286
 *
 */
public class SDLandingPage extends AbstractLandingPage {
	private static final Logger LOG = Logger.getLogger(SDLandingPage.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public SDLandingPage() {
		super();
	}

	// fix these
	@Override
	public String getHtmlLink() {
		return landingPageHtml.getHead().getMetaElementValue(CITATION_FULL_HTML_URL);
	}

	@Override
	public String getLinkToAbstract() {
		return landingPageHtml.getHead().getMetaElementValue(CITATION_ABSTRACT_HTML_URL);
	}

	@Override
	public String getPDFLink() {
		return landingPageHtml.getHead().getMetaElementValue(CITATION_PDF_URL);
	}

	@Override
	public List<String> getSupplemntaryLinks() {
		// TODO Auto-generated method stub
		return null;
	}

}
