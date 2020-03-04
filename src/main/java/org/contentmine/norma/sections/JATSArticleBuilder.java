package org.contentmine.norma.sections;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.html.HtmlMeta;

/** builds article-meta from components
 * First implementation is conversion from HTML <meta>
 * 
 * 
 * @author pm286
 *
 */
public class JATSArticleBuilder {
	private static final Logger LOG = Logger.getLogger(JATSArticleBuilder.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public JATSArticleBuilder() {
		
	}
	
	
	public void add(HtmlMeta htmlMeta) {
		
	}
}
