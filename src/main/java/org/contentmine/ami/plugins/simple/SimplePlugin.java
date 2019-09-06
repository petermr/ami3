package org.contentmine.ami.plugins.simple;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIPlugin;
import org.contentmine.ami.plugins.sequence.SequenceArgProcessor;

/** test plugin.
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class SimplePlugin extends AMIPlugin {

	private static final Logger LOG = Logger.getLogger(SimplePlugin.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

//	private SimpleArgProcessor argProcessor;
	
	public SimplePlugin(String[] args) {
		super();
		this.argProcessor = new SimpleArgProcessor(args);
	}
	
	public static void main(String[] args) {
		SimpleArgProcessor argProcessor = new SimpleArgProcessor(args);
		argProcessor.runAndOutput();
	}

	public SimplePlugin(String args) {
		super();
		this.argProcessor = new SimpleArgProcessor(args);
	}

	

}
