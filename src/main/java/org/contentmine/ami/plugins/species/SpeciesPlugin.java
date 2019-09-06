package org.contentmine.ami.plugins.species;

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
public class SpeciesPlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(SpeciesPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
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
