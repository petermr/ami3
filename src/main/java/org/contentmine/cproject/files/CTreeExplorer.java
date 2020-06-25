package org.contentmine.cproject.files;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/** searches a CTree or CTreeList for files or metadata.
 * 
 * @author pm286
 *
 */
public class CTreeExplorer {
	
	private static final Logger LOG = LogManager.getLogger(CTreeExplorer.class);
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
