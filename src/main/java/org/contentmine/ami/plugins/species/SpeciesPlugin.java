package org.contentmine.ami.plugins.species;

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
public class SpeciesPlugin extends AMIPlugin {

	private static final Logger LOG = LogManager.getLogger(SpeciesPlugin.class);
public SpeciesPlugin() {
		this.argProcessor = new SpeciesArgProcessor();
	}

	public SpeciesPlugin(String[] args) {
		super();
		this.argProcessor = new SpeciesArgProcessor(args);
	}

	public SpeciesPlugin(String args) {
		super();
		this.argProcessor = new SpeciesArgProcessor(args);
	}

	public static void main(String[] args) {
		new SpeciesArgProcessor(args).runAndOutput();		
	}
}
