package org.contentmine.ami.plugins.regex;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.plugins.AMIPlugin;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DebugPrint;
import org.contentmine.eucl.euclid.Util;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses bitmaps
 * 
 * @author pm286
 *
 */
@Command(
name = "ami-regex", 
aliases = "regex",
version = "ami-regex 0.1",
description = "runs regex on HTML or XML files "
)

public class AMIRegexTool extends AbstractAMITool {
	private static final Logger LOG = Logger.getLogger(AMIRegexTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	
    @Option(names = {"--context"},
    		arity = "2",
//    		defaultValue = "[40,40]",
            description = "characters before and after regex")
    private List<Integer> contextList = Arrays.asList(new Integer[] {30, 40});
	
    @Option(names = {"--regex"},
    		arity = "1..*",
            description = "List of regex files (hardcoded, local, relative, absolute.")
    private List<String> regexList;
	
    @Option(names = {"--xpath"},
    		arity = "1",
            description = "xpath for sectioned documents")
    private String xpath = null;
	


    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIRegexTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIRegexTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIRegexTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
    	setDefaults();
		System.out.println("context             " + contextList);
		System.out.println("input               " + input);
		System.out.println("output              " + output);
		System.out.println("regex               " + regexList);
		System.out.println("xpath               " + xpath);
		System.out.println();
	}

	private void setDefaults() {
		input = "scholarly.html";
    	output = "output.xml";
	}

    @Override
    protected void runSpecifics() {
    	if (processTrees()) { 
    	} else {
			DebugPrint.debugPrint(Level.ERROR, "must give cProject or cTree ");
	    }
    }

	public void processTree() {
		System.out.println("cTree: "+cTree.getName());
		runRegex();
		
	}

	private void runRegex() {
	    LOG.debug("running regex");
	    String regexS = Util.createWhitespaceSeparatedTokens(regexList);
//	    System.out.println(">ss>"+ss);
		String args = 
				""
				+ "-q " + cTree.getDirectory()
				+ " -i " + input
				+ " -o "+output
				+ " --context " + contextList.get(0) + " " + contextList.get(1)
				+ " --r.regex " + regexS;
		LOG.debug("args: "+args);
		AMIPlugin regexPlugin = new RegexPlugin(args);
		RegexArgProcessor argProcessor = (RegexArgProcessor) regexPlugin.getArgProcessor();
		argProcessor.runAndOutput();
	}

}
