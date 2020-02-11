package org.contentmine.ami.tools.download;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlHtml;

/** the first page a user/agent sees gievn a citation link
 * it normally has title, authors
 * often has an abstract
 * should have links to fulltext HTML and/or PDF
 * MAY have fulltext, but this varies considerably.


 * @author pm286
 *
 */
public abstract class AbstractLandingPage {
	private static final Logger LOG = Logger.getLogger(AbstractLandingPage.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/**
	<meta name="citation_public_url" content="https://www.biorxiv.org/content/10.1101/864827v1" />
	<meta name="citation_abstract_html_url" content="https://www.biorxiv.org/content/10.1101/864827v1.abstract" />
	<meta name="citation_full_html_url" content="https://www.biorxiv.org/content/10.1101/864827v1.full" />
	<meta name="citation_pdf_url" content="https://www.biorxiv.org/content/biorxiv/early/2019/12/06/864827.full.pdf" />
	*/
	protected static final String CITATION_PDF_URL = "citation_pdf_url";
	protected static final String CITATION_FULL_HTML_URL = "citation_full_html_url";
	protected static final String CITATION_ABSTRACT_HTML_URL = "citation_abstract_html_url";
	protected static final String CITATION_PUBLIC_URL = "citation_public_url";

	protected HtmlHtml landingPageHtml;
	
	public AbstractLandingPage() {
		
	}

	public void readHtml(HtmlHtml landingPageHtml) {
		this.landingPageHtml = landingPageHtml;
	}

	public abstract String getHtmlLink();
	public abstract String getLinkToAbstract();
	public abstract String getPDFLink();
	public abstract List<String> getSupplemntaryLinks();


}
