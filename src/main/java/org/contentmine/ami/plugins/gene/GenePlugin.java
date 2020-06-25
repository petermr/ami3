package org.contentmine.ami.plugins.gene;

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
public class GenePlugin extends AMIPlugin {

	private static final Logger LOG = LogManager.getLogger(GenePlugin.class);
public GenePlugin() {
		this.argProcessor = new GeneArgProcessor();
	}

	public GenePlugin(String[] args) {
		super();
		this.argProcessor = new GeneArgProcessor(args);
	}

	public GenePlugin(String args) {
		super();
		this.argProcessor = new GeneArgProcessor(args);
	}

	public static void main(String[] args) {
		new GeneArgProcessor(args).runAndOutput();		
	}
}
