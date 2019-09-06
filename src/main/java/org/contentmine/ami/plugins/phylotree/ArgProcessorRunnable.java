package org.contentmine.ami.plugins.phylotree;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.args.DefaultArgProcessor;

/** Runs argProcessor in thread.
 * 
 * allows process to be timed out
 * 
 * @author pm286
 *
 */
public class ArgProcessorRunnable implements Runnable {

	private final static Logger LOG = Logger.getLogger(ArgProcessorRunnable.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private String cmd;
	
	private DefaultArgProcessor argProcessor;

	public ArgProcessorRunnable(String cmd, DefaultArgProcessor argProcessor) {
		this.cmd = cmd;
		this.argProcessor = argProcessor;
	}

	public void run() {
		argProcessor.runAndOutput();
	}


}
