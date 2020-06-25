package org.contentmine.ami.plugins.phylotree;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.args.DefaultArgProcessor;

/** Runs argProcessor in thread.
 * 
 * allows process to be timed out
 * 
 * @author pm286
 *
 */
public class ArgProcessorRunnable implements Runnable {

	private final static Logger LOG = LogManager.getLogger(ArgProcessorRunnable.class);
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
