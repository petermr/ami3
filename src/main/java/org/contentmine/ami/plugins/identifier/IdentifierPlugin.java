package org.contentmine.ami.plugins.identifier;

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
public class IdentifierPlugin extends AMIPlugin {

	private static final Logger LOG = LogManager.getLogger(IdentifierPlugin.class);
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
