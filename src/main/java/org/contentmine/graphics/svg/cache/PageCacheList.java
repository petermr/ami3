package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class PageCacheList extends GenericAbstractList<PageCache> {
	private static final Logger LOG = LogManager.getLogger(PageCacheList.class);
public PageCacheList() {
		init();
	}

	/** deep copy constructor.
	 * 
	 * @param pageCacheList
	 */
	public PageCacheList(PageCacheList pageCacheList) {
		this.genericList = new ArrayList<PageCache>(pageCacheList.genericList);
	}
	
	private void init() {
	}

	public void sortBySerial() {
	}

	
	
}
