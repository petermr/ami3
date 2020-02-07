package org.contentmine.ami.tools;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineUtil;
import org.junit.Test;

/** test OCR.
 * 
 * @author pm286
 *
 */
public class AMIDownloadTest {
	private static final Logger LOG = Logger.getLogger(AMIDownloadTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	/** 
	 * run query
	 */
	public void testBiorxiv() throws Exception {
		String args = 
				"-p target/biorxiv"
				+ " --site biorxiv"
				+ " --query coronavirus"
				+ " --pagesize 40"
				+ " --pages 1 10"
				+ " --limit 500"
			;
		new AMIDownloadTool().runCommands(args);
	}

	@Test
	/** 
	 * run query
	 */
	public void testBiorxivClimate() throws Exception {
		String args = 
				"-p target/biorxiv/climate"
				+ " --site biorxiv"
				+ " --query climate change"
				+ " --pagesize 40"
				+ " --pages 1 10"
				+ " --limit 200"
			;
		new AMIDownloadTool().runCommands(args);
	}

	// extract fulltext with div[class~="fulltext-view"]
	
	
	@Test 
	public void testCurlREST() throws Exception {
			
		String result = runCurlProcess("https://www.ebi.ac.uk/europepmc/webservices/rest/search?query=coronavirus");
		System.out.println("EBI "+result);

		result = runCurlProcess("https://www.biorxiv.org/search/coronavirus");
		System.out.println("BIOX "+result);
		
//		https://www.biorxiv.org/search/coronavirus%20numresults%3A75%20sort%3Arelevance-rank

	}

	private String runCurlProcess(String url) throws IOException {
		String[] command = new String[] {"curl", "-X", "GET", url};
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();
		String result = String.join("\n", IOUtils.readLines(process.getInputStream(), CMineUtil.UTF8_CHARSET));
		int exitCode = process.exitValue();
		return result;
	}
	

}
