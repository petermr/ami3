package org.contentmine;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * toplevel main class to provide script documentation
 * @author pm286
 *
 */
public class ContentMine {
	private static final Logger LOG = Logger.getLogger(ContentMine.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			LOG.error("Programs are:");
			LOG.error("    cproject  // runs cephis tools");
			LOG.error("    norma     // runs norma (to transform files)");
			LOG.error("    ami       // runs ami (to search and analyze documents)");
		}
	}
	
}
