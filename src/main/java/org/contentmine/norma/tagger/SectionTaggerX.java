package org.contentmine.norma.tagger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** tags sections based on regex for titles and content.
 * 
 * NOT YET WRITTEN
 * 
 * @author pm286
 *
 */
public class SectionTaggerX {

	private static final Logger LOG = LogManager.getLogger(SectionTaggerX.class);
private String filename;

	public SectionTaggerX(String filename) {
		this.filename = filename;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("file: "+filename);
		return sb.toString();
	}
	
}
