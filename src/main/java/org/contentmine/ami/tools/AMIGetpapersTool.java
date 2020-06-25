package org.contentmine.ami.tools;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.cproject.files.CProject;
import org.contentmine.norma.getpapers.GetpapersRunner;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** queries a remote and downloads papers using 'getpapers'
 * 
 * 
 * @author Rik Smith-Unna (getpapers)
 * @author pm286
 *
 */
@Command(
name = "getpapers",
description = "Runs getpapers in java environment."
)
public class AMIGetpapersTool extends AbstractAMITool {
	private static final Logger LOG = LogManager.getLogger(AMIGetpapersTool.class);
/** already in parent
	 * 
	 */
//    @Option(names = {"-o", "--output"},
//    		arity = "1",
//            description = "output directory")
//    private List<Integer> output = null;

    @Option(names = {"-q", "--query"},
    		arity = "1",
            description = "query to issue")
    private String query = null;

    @Option(names = {"-k", "--limit"},
    		arity = "1",
    		defaultValue="100",
            description = "limit to download")
    private Integer limit = null;

    // more later

    
    /** used by some non-picocli calls
     * obsolete it
     * @param cProject
     */
	public AMIGetpapersTool(CProject cProject) {
		this.cProject = cProject;
	}
	
	public AMIGetpapersTool() {
	}
	
    public static void main(String[] args) throws Exception {
    	new AMIGetpapersTool().runCommands(args);
    }

    @Override
	protected void parseSpecifics() {
//		System.out.println("pages                " + pages);
//		System.out.println("regexes              " + regexList);
//		System.out.println("regexfile            " + regexFilename);
		System.out.println();
	}


    @Override
    protected void runSpecifics() {
    	runGetpapers();
    }

	private void runGetpapers() {
		
//		File inputDir = cTree.getDirectory();
//		File inputFile = new File(inputDir, CTree.FULLTEXT_PDF);
//		outputDir = new File(cTree.getDirectory(), "tei/");
//		outputDir.mkdirs();
		File outputDir = cProject.getDirectory();
		GetpapersRunner getpapersRunner = new GetpapersRunner();
		getpapersRunner.setTryCount(200); // fix this later
//		if (1 == 1) throw new RuntimeException("Not yet running");
		getpapersRunner.runGetpapers(outputDir);
	}
	

}
