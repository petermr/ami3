package org.contentmine.cproject.args;

import org.apache.log4j.Logger;

/** for communication between Tool family and ArgProcessor  objects.
 * ArgProcessors provide functionality for some tools, especially Words and Search
 * 
 * Messy , since retrofitted
 * 
 * @author pm286
 *
 */
public interface AbstractTool {

	/** get the verbosity (set by picocli) */
	int getVerbosityInt();
	
//	/** output message at gven picocli level */
	public static void debug(AbstractTool abstractTool, int level, String message, Logger LOG) {
		if (abstractTool == null || abstractTool.getVerbosityInt() >= level) LOG.debug(message);
	}



}
