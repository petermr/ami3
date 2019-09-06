package org.contentmine.cproject.files;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** searches a CTree or CTreeList for files or metadata.
 * 
 * @author pm286
 *
 */
public class CTreeExplorer {
	
	private static final Logger LOG = Logger.getLogger(CTreeExplorer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String filename;

	public CTreeExplorer() {
		
	}

	public CTreeExplorer setFilename(String filename) {
		this.filename = filename;
		return this;
	}

	public String getFilename() {
		return filename;
	}

	
}
