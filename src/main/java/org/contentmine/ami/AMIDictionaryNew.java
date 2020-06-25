package org.contentmine.ami;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * runs ami-dictionary to manage dictionary creation
 * 
 * @author pm286
 *
 */
public class AMIDictionaryNew {
	private static final Logger LOG = LogManager.getLogger(AMIDictionaryNew.class);
private static void runAMISearches(List<String> argList) {
		String projectName = argList.get(0);
		argList.remove(0);
		if (argList.size() == 0) {
			System.err.println("No default action for project: "+projectName+" (yet)");
		} else {
			AMIProcessor amiProcessor = AMIProcessor.createProcessor(projectName);
			amiProcessor.runSearchesAndCooccurrence(argList);
		}
	}

}
