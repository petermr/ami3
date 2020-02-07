package org.contentmine.ami.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineUtil;

/** wraps curl in java process
 * 
 * @author pm286
 *
 */
public class CurlDownloader {
	private static final Logger LOG = Logger.getLogger(CurlDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String CURL = "curl";
	
	private String urlString;
	private File outputFile;
	private List<String> commandList;

	public CurlDownloader() {
		commandList = new ArrayList<>();
	}
	
	public CurlDownloader setUrlString(String urlString) {
		this.urlString = urlString;
		return this;
	}
	
	public CurlDownloader setOutputFile(File outputFile) {
		this.outputFile = outputFile;
		return this;
	}
	
	public String run() throws IOException {
		commandList.add(CURL);
		if (outputFile != null) {
			commandList.add("--output");
			commandList.add(outputFile.toString());
		}
		if (urlString == null) {
			throw new RuntimeException("curl: must set URL");
		}
		commandList.add(urlString);
		String result = run(commandList);
		return result;
	}

	private String run(List<String> commandList) throws IOException {
		String[] commands = commandList.toArray(new String[0]);
		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		Process process = processBuilder.start();
		String result = String.join("\n", IOUtils.readLines(process.getInputStream(), CMineUtil.UTF8_CHARSET));
//		int exitCode = process.exitValue();
//		if (exitCode != 0) {
//			System.err.println("EXITCode: "+exitCode);
//		}
		return result;
	}
	
	
}
