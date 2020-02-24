package org.contentmine.ami.tools.download.redalyc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.download.AbstractLandingPage;
import org.contentmine.eucl.xml.XMLUtil;

/** the first page a user/agent sees given a citation link
 * it normally has title, authors
 * often has an abstract
 * should have links to fulltext HTML and/or PDF
 * normally Biorxiv does not have fulltext landing pages
 * 
 * 

 * @author pm286
 *
 */
public class RedalycLandingPage extends AbstractLandingPage {
	private static final Logger LOG = Logger.getLogger(RedalycLandingPage.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public RedalycLandingPage() {
		super();
		System.err.println(RedalycDownloader.REDALYC_NOT_YET_IMPLEMENTED);
	}

	@Override
	public String getHtmlLink() {
		System.err.println(RedalycDownloader.REDALYC_NOT_YET_IMPLEMENTED);
		return landingPageHtml == null ? null :
			landingPageHtml.getHead().getMetaElementValue(CITATION_FULL_HTML_URL);
	}

	@Override
	public String getLinkToAbstract() {
		System.err.println(RedalycDownloader.REDALYC_NOT_YET_IMPLEMENTED);
		return landingPageHtml.getHead().getMetaElementValue(CITATION_ABSTRACT_HTML_URL);
	}

	@Override
	public String getPDFLink() {
		System.err.println(RedalycDownloader.REDALYC_NOT_YET_IMPLEMENTED);
		return landingPageHtml.getHead().getMetaElementValue(CITATION_PDF_URL);
	}

	@Override
	public List<String> getSupplemntaryLinks() {
		System.err.println(RedalycDownloader.REDALYC_NOT_YET_IMPLEMENTED);
		// TODO Auto-generated method stub
		return null;
	}

}
