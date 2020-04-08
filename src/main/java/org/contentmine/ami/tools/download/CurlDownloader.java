package org.contentmine.ami.tools.download;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
	private static final String TRACE_TIME = "--trace-time";
	private static final String TRACE_ASCII = "--trace-ascii";
	private static final Logger LOG = Logger.getLogger(CurlDownloader.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String CURL = "curl";
	public final static String _OUT = "-o";
	private static final String GET = "GET";
	private static final String CURL_X = "-X";
	
	private String urlString;
	private File outputFile;
	private List<String> commandList;

	private List<CurlPair> curlPairList;
	private String traceFile;
	private boolean traceTime;

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
		addTrace();
		addCurlPairsOrOutput();
		addOutputFile();
//		System.out.println("curl: "+commandList);
		String result = run(commandList);
		return result;
	}

	private void addTrace() {
//		--trace-ascii d.txt --trace-time
		if (traceFile != null) {
			commandList.add(TRACE_ASCII);
			commandList.add(traceFile.toString());
			if (traceTime) {
				commandList.add(TRACE_TIME);
			}
		}
	}

	private void addOutputFile() {
		if (outputFile != null) {
			commandList.add("--output");
			commandList.add(outputFile.toString());
		}
	}

	private void addCurlPairsOrOutput() {
		getOrCreateCurlPairList();
		if (curlPairList.size() > 0 ) {
			for (CurlPair curlPair : curlPairList) {
				commandList.add(_OUT);
				commandList.addAll(curlPair.toList());
			}
		} else {
			if (urlString == null) {
				throw new RuntimeException("curl: must set URL");
			} else {
				commandList.add(urlString);
			}
		}
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

	public void addCurlPair(CurlPair curlPair) {
		getOrCreateCurlPairList();
		curlPairList.add(curlPair);
	}

	public List<CurlPair> getOrCreateCurlPairList() {
		if (curlPairList == null) {
			curlPairList = new ArrayList<>();
		}
		return curlPairList;
	}

	public void setTraceFile(String tracefile) {
		this.traceFile = tracefile;
	}
	
	public void setTraceTime(boolean traceTime) {
		this.traceTime = traceTime;
	}
	
	// CURL Runners
	public static String runCurlGet(String url) throws IOException {
		String[] command = new String[] {CURL, CURL_X, GET, url};
		String result = runCurl(command);
		return result;
	}

	private static String runCurl(String[] command) throws IOException {
		
		System.out.println("running "+Arrays.asList(command));
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		Process process = processBuilder.start();
		String result = String.join("\n", IOUtils.readLines(process.getInputStream(), CMineUtil.UTF8_CHARSET));
		try {
			int exitCode = process.exitValue();
			if (exitCode != 0) {
				System.err.println("EXITCode: "+exitCode);
			}
		} catch (java.lang.IllegalThreadStateException itse) {
			// not sure yet why this happens
			System.err.println("Error after running curl, maybe: process hasn't exited");
			itse.printStackTrace();
		}
		return result;
	}

	public List<String> getCommandList() {
		return commandList;
	}
	
}
