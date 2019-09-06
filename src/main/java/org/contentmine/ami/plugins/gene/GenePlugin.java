package org.contentmine.ami.plugins.gene;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIPlugin;

/** test plugin.
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class GenePlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(GenePlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
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
