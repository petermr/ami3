package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class PageCacheList extends GenericAbstractList<PageCache> {
	private static final Logger LOG = Logger.getLogger(PageCacheList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
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
