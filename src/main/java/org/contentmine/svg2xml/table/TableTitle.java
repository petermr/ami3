package org.contentmine.svg2xml.table;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TableTitle {

	private static final Logger LOG = LogManager.getLogger(TableTitle.class);
private String title;
	private String chunkName;

	public TableTitle(String title, String chunkName) {
		this(title);
		this.chunkName = chunkName.trim();
	}

	public TableTitle(String title) {
		this.title = title.trim();
	}

	public String getTitle() {
		return title;
	}
	
	public String getChunkName() {
		return chunkName;
	}
	
	public String toString() {
		return title+": "+chunkName;
	}
	
}
