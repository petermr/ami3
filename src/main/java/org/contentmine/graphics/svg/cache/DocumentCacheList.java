package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class DocumentCacheList extends GenericAbstractList<DocumentCache> {
	private static final Logger LOG = Logger.getLogger(DocumentCacheList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public DocumentCacheList() {
		init();
	}

	/** deep copy constructor.
	 * 
	 * @param documentCacheList
	 */
	public DocumentCacheList(DocumentCacheList documentCacheList) {
		this.genericList = new ArrayList<DocumentCache>(documentCacheList.genericList);
	}
	
	private void init() {
	}

}
