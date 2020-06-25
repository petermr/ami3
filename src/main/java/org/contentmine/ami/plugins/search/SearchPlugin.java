package org.contentmine.ami.plugins.search;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIPlugin;
import org.contentmine.ami.plugins.AbstractSearchArgProcessor;

/** test plugin.
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class SearchPlugin extends AMIPlugin {

	private static final Logger LOG = LogManager.getLogger(SearchPlugin.class);
public SearchPlugin() {
		this.argProcessor = new SearchArgProcessor();
	}

	public SearchPlugin(String[] args) {
		super();
		this.argProcessor = new SearchArgProcessor(args);
	}

	public SearchPlugin(String args) {
		super();
		this.argProcessor = new SearchArgProcessor(args);
	}

	public static void main(String[] args) {
		System.err.println(""
				+ "*******************************\n"
				+ "WARNING: AMISearch has been replaced by AMISearchTool (ami-search-new)\n"
				+ "*******************************\n");

//		new SearchArgProcessor(args).runAndOutput();		
	}
}
