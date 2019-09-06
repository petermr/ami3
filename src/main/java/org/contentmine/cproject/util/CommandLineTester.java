package org.contentmine.cproject.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** tool for testing commandline 
 * 
 * @author pm286
 *
 */
public class CommandLineTester {

	private String command = null;
	private String outputString;
	private String commandLine;
	private List<String> arguments;

	public CommandLineTester() {
		arguments = new ArrayList<String>();
	}

	public void setCommand(String cmd) {
		this.command  = cmd;
	}

	public void run() throws IOException {
		CommandLineRunner commandLineRunner = new CommandLineRunner(command, arguments);
		outputString = commandLineRunner.run();
	}

	public String getOutputString() {
		return outputString;
	}

	public void addArgument(String arg) {
		arguments.add(arg);
	}
	
	
}
