package org.contentmine.ami.plugins.regex;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIPlugin;

/** RegexPlugin
 * 
 * 
 * @author pm286
 *
 */
public class RegexPlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(RegexPlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public RegexPlugin() {
		this.argProcessor = new RegexArgProcessor();
	}

	public RegexPlugin(String[] args) {
		super();
		this.argProcessor = new RegexArgProcessor(args);
	}

	public RegexPlugin(String args) {
		super();
		this.argProcessor = new RegexArgProcessor(args);
	}

	public static void main(String[] args) {
		new RegexArgProcessor(args).runAndOutput();		
	}
}
