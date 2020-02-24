package org.contentmine.ami.tools.download;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineUtil;

/** wraps curl in java process
 * 
 * @author pm286
 *
 */
public class ChromeHeadlessBrowser {
	
	private static final Logger LOG = Logger.getLogger(ChromeHeadlessBrowser.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static final String DISABLE_GPU = "--disable-gpu";
	private static final String DUMP_DOM = "--dump-dom=";
	private static final String HEADLESS = "--headless";
	private static final String GOOGLE_CHROME = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome";
	public final static String HEADLESS_CMD = 
			GOOGLE_CHROME + " " + HEADLESS + "  " + DISABLE_GPU;
	
//	private String urlString;
	private File outputFile;
	private List<String> commandList;
	private File dumpFile;


	private String urlString;
	private String result;

	public ChromeHeadlessBrowser() {
		commandList = new ArrayList<>();
		dumpFile = new File("/Users/pm286/dump.html");
	}
	
	public ChromeHeadlessBrowser setUrlString(String urlString) {
		this.urlString = urlString;
		return this;
	}
	
	public ChromeHeadlessBrowser setOutputFile(File outputFile) {
		this.outputFile = outputFile;
		return this;
	}
	
	public String run() throws IOException {
		commandList.add(GOOGLE_CHROME); // test
		commandList.add(HEADLESS);
		commandList.add(DISABLE_GPU);
		commandList.add(DUMP_DOM+dumpFile);
		commandList.add(urlString);
//		System.out.println(commandList);
		result = run(commandList);
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

	/** A TEST */
	public static void main(String[] args) throws IOException {
		ChromeHeadlessBrowser headless = new ChromeHeadlessBrowser();
		String urlString;
		urlString = "https://www.redalyc.org/busquedaArticuloFiltros.oa?q=climate%20change";
//		urlString = "http://en.wikipedia.org/wiki/Main_Page";
		headless.setUrlString(urlString);
		String result = headless.run();
		headless.write(new File("/Users/pm286/junk.html"));
//		LOG.debug("result "+result);
	}

	private void write(File file) {
		try {
			FileUtils.write(file, result, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
