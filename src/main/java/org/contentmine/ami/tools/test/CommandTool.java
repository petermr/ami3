package org.contentmine.ami.tools.test;

import java.util.concurrent.Callable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.DebugPrint;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** analyses bitmaps
 * 
 * @author pm286
 *
 */
@Command(
		subcommands = {
				SubCommand1Tool.class,
//				SubCommand2Tool.class
		},
name = "command", 
aliases = "mycommand",
version = "command 0.1",
description = "test subcommand "
)

public class CommandTool implements Callable<Void> {

	private static final Logger LOG = Logger.getLogger(CommandTool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	
    @Option(names = {"--value"},
    		arity = "1",
            description = "dummy")
	public String value = "value";

    @Option(names = {"--flag"},
            description = "flag")
	public boolean flag = false;

	public CommandTool() {
		LOG.debug("TestCommand");
	}
	
    public static void main(String[] args) throws Exception {
    	new CommandTool().runCommands(args);
    }
    
	/** parse commands and pass to CommandLine
	 * calls CommandLine.call(this, args)
	 * 
	 * @param args
	 */
	public void runCommands(String[] args) {
		// add help
    	args = args.length == 0 ? new String[] {"--help"} : args;
		CommandLine.call(this, args);
		LOG.debug("command "+value+" / flag " + flag);
	}

	@Override
    public Void call() throws Exception {
		LOG.debug("**************Command*****************call()");
        return null;
    }



}
