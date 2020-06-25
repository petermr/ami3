package org.contentmine.norma.util;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.cproject.util.CMineUtil;

public abstract class CommandRunner {
	private static final Logger LOG = LogManager.getLogger(CommandRunner.class);
//	protected static final String TESS_CONFIG = "replaceme";
	protected static final String ENCODING = "UTF-8";
	protected static final int SLEEP_TIME = 1500;
	protected static final int NTRIES = 20;
	public static final String RAW_HTML = "raw.html";
	protected File outputFileRoot;
	protected int tryCount;
	protected int sleepTimeMsec;
	protected String encoding = ENCODING;
	protected ProcessBuilder builder;
	protected Process proc;
//	protected AbstractAMITool amiTool;

	protected int exitAfterTrying() throws InterruptedException {
			int exitValue = -1;
	        int itry = 1;
	        for (; itry <= tryCount; itry++) {
				Thread.sleep(sleepTimeMsec);
			    try {
			    	exitValue = proc.exitValue();
			    	if (exitValue == 0) {
			    		LOG.trace("tesseract terminated OK");
			    		break;
			    	}
				} catch (IllegalThreadStateException e) {
	//					LOG.debug("still not terminated after: " + itry * sleepTimeMsec + " msec; keep going");
					System.err.print(">"+itry * sleepTimeMsec + " ms ");
				}
			}
			LOG.trace("tries: "+itry);
			return exitValue;
		}

	protected abstract String getProgram();

	public int getTryCount() {
		return tryCount;
	}

	public void setTryCount(int tryCount) {
		this.tryCount = tryCount;
	}

	public int getSleepTimeMsec() {
		return sleepTimeMsec;
	}

	public void setSleepTimeMsec(int sleepTimeMsec) {
		this.sleepTimeMsec = sleepTimeMsec;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	protected void setDefaults() {
		tryCount = NTRIES;
		encoding = "";
		sleepTimeMsec = SLEEP_TIME;
	}

	protected void runBuilderAndCleanUp() throws InterruptedException {
		builder.redirectError(ProcessBuilder.Redirect.INHERIT);
		proc = null;
		try {
			proc = builder.start();
		    proc.getOutputStream().close();
		} catch (IOException e) {
			CMineUtil.catchUninstalledProgram(e, getProgram());
		}
		int exitValue = exitAfterTrying();
		if (exitValue != 0) {
			proc.destroy();
			LOG.error("Process failed to terminate after :"+tryCount);
		} else {
//			LOG.debug("OCR launched");
		}
	}

//	public AbstractAMITool getAmiTool() {
//		return amiTool;
//	}
//
//	public void setAmiTool(AbstractAMITool amiTool) {
//		this.amiTool = amiTool;
//	}
	
	

}
