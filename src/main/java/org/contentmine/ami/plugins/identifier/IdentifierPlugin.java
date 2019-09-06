package org.contentmine.ami.plugins.identifier;

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
public class IdentifierPlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(IdentifierPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public IdentifierPlugin() {
		this.argProcessor = new IdentifierArgProcessor();
	}

	public IdentifierPlugin(String args) {
		super();
		this.argProcessor = new IdentifierArgProcessor(args);
	}

	public IdentifierPlugin(String[] args) {
		super();
		this.argProcessor = new IdentifierArgProcessor(args);
	}
	
	public static void main(String[] args) {
		new IdentifierArgProcessor(args).runAndOutput();
	}

}
