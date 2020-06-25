package org.contentmine.ami.plugins.word;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIPlugin;

/** test plugin.
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class WordPlugin extends AMIPlugin {

	private static final Logger LOG = LogManager.getLogger(WordPlugin.class);
public WordPlugin() {
		this.argProcessor = new WordArgProcessor();
	}

	public WordPlugin(String[] args) {
		super();
		this.argProcessor = new WordArgProcessor(args);
	}

	public WordPlugin(String args) {
		super();
		this.argProcessor = new WordArgProcessor(args);
	}

	public static void main(String[] args) {
		new WordArgProcessor(args).runAndOutput();		
	}


}
