package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class DocumentCacheList extends GenericAbstractList<DocumentCache> {
	private static final Logger LOG = LogManager.getLogger(DocumentCacheList.class);
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
