package org.contentmine.ami;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class AMIProcessorXML {
	private static final Logger LOG = Logger.getLogger(AMIProcessorXML.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static void main(String[] args) {
		List<String> argList = new ArrayList<String>(Arrays.asList(args));
		if (argList.size() == 0 || AMIProcessor.HELP.equals(argList.get(0))) {
			if (argList.size() > 0) argList.remove(0);
			AMIProcessor.runHelp(argList);
		} else {
			File projectDir = null;
			String projectName = argList.get(0);
			argList.remove(0);
			if (argList.size() == 0) {
				System.err.println("No default action for project: "+projectName+" (yet)");
			} else {
				projectDir = AMIProcessor.createProjectDirAndTrimArgs(argList);
				if (projectDir != null) {
					runXML(projectDir, argList);
				}
			}
		}
	}

	private static void runXML(File projectDir, List<String> facetList) {
		AMIProcessor amiProcessor = AMIProcessor.createProcessor(projectDir);
		amiProcessor.convertJATSXMLandWriteHtml();
		amiProcessor.convertHTMLsToProjectAndRunCooccurrence(facetList);
	}

}
