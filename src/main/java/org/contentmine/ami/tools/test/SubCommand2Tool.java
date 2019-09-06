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
name = "subcommand2", 
aliases = "mysubcommand2",
version = "subcommand2 0.1",
description = "test subcommand2 "
)

public class SubCommand2Tool implements Callable<Void> {


	private static final Logger LOG = Logger.getLogger(SubCommand2Tool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	
    @Option(names = {"--value2"},
    		arity = "1",
            description = "value2")
	public String value2 = "defaultValue2";

    @Option(names = {"--flag2"},
    		arity = "0",
            description = "flag2")
	public boolean flag2 = false;


	public SubCommand2Tool() {
		LOG.debug("TestSubCommand2");
	}
	
    public static void main(String[] args) throws Exception {
    	new SubCommand2Tool().runCommands(args);
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
		LOG.debug("value2 " + value2 + " / flag2 " + flag2);
	}

	@Override
    public Void call() throws Exception {
		LOG.debug("*******Subcommand2**************call()");
        return null;
    }



}
