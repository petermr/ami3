package org.contentmine.ami.plugins.phylotree;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIPlugin;
import org.contentmine.ami.plugins.sequence.SequenceArgProcessor;

/** only for external calling
 * 
 * Very simple tasks for testing and tutorials.
 * 
 * @author pm286
 *
 */
public class PhyloTreePlugin extends AMIPlugin {

	private static final Logger LOG = LogManager.getLogger(PhyloTreePlugin.class);
@Deprecated //
	public PhyloTreePlugin(String[] args) {
		super();
		this.argProcessor = new PhyloTreeArgProcessor(args);
	}
	
	public static void main(String[] args) {
		PhyloTreeArgProcessor argProcessor = new PhyloTreeArgProcessor(args);
		argProcessor.runAndOutput();
	}

	@Deprecated
	public PhyloTreePlugin(String args) {
		super();
		this.argProcessor = new PhyloTreeArgProcessor(args);
	}

	
	

}
