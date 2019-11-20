package org.contentmine.norma.getpapers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CTree;
import org.contentmine.norma.image.ocr.HOCRConverter;
import org.contentmine.norma.util.CommandRunner;

public class GetpapersRunner extends CommandRunner {

	public final static Logger LOG = Logger.getLogger(GetpapersRunner.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	/**
/usr/local/n/versions/node/6.2.1/bin/getpapers
 */

	/** NOT YET WORKING */
	public final static String USER_HOME = System.getProperty("user.home");
	public static final String GETPAPERS = "/usr/local/n/versions/node/6.2.1/bin/getpapers";
	public static final String QUERY = "-q";
	public final static String LIMIT = "-k";
	public final static String OUT = "-o";
	
	private String query = "zika";
	private int limit = 100;

	public GetpapersRunner() {
		setDefaults();
	}
	
    /** runs getpapers
     */
    public void runGetpapers(File outputDir) {

    	outputDir.mkdirs();
    	
		builder = new ProcessBuilder(
				getProgram(), 
				QUERY,
				query,
				LIMIT,
				String.valueOf(limit),
				OUT,
				outputDir.toString()
				
				);
        try {
			runBuilderAndCleanUp();
		} catch (Exception e) {
			throw new RuntimeException("Cannot run Grobid: ", e);
		}
    }

	protected String getProgram() {
		return "node";
//		return GETPAPERS; 
	}
	
}

