package org.contentmine.cproject.metadata.quickscrape;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** this is not an actual downloader as we can't call Node from Java.
 * 
 * It can create a list of URLs to scrape and can work out whether those URLs have been downloaded
 * into a CProject
 * 
 * @author pm286
 *
 */
public class QuickscrapeDownload {

	private static final Logger LOG = Logger.getLogger(QuickscrapeDownload.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public QuickscrapeDownload() {
		
	}
}
