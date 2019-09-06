package org.contentmine.ami.plugins.dummy;

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
public class DummyPlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(DummyPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public DummyPlugin() {
		this.argProcessor = new DummyArgProcessor();
	}

	public DummyPlugin(String[] args) {
		super();
		this.argProcessor = new DummyArgProcessor(args);
	}

	public DummyPlugin(String args) {
		super();
		this.argProcessor = new DummyArgProcessor(args);
	}

	public static void main(String[] args) {
		new DummyArgProcessor(args).runAndOutput();		
	}
}
