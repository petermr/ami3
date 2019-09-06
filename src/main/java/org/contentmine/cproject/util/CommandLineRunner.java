package org.contentmine.cproject.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class CommandLineRunner {

	private String command;
	private List<String> arguments;

	public CommandLineRunner(String command, List<String> arguments) {
		this.command = command;
		this.arguments = arguments;
	}

	public String run() throws IOException {
		if (command == null) {
			throw new RuntimeException("No command given");
		}
		List<String> processArgs = new ArrayList<String>();
		processArgs.add(command);
		processArgs.addAll(arguments);
        ProcessBuilder commandRunner = new ProcessBuilder(processArgs.toArray(new String[0]));
        commandRunner.redirectError(ProcessBuilder.Redirect.INHERIT);
        Process process = null;
        try {
        	process = commandRunner.start();
        } catch (IOException e) {
        	CMineUtil.catchUninstalledProgram(e, command);
        }
        return IOUtils.toString(process.getInputStream());

	}

}
