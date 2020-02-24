package org.contentmine.ami.tools.download.osf;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractLandingPage;
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
public class OSFLandingPage extends AbstractLandingPage {
	private static final Logger LOG = Logger.getLogger(OSFLandingPage.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public OSFLandingPage() {
		super();
	}

	// fix these
	@Override
	public String getHtmlLink() {
		return landingPageHtml == null ? null :
			landingPageHtml.getHead().getMetaElementValue(CITATION_FULL_HTML_URL);
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
