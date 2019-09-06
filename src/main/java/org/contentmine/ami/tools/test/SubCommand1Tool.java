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
name = "subcommand1", 
aliases = "mysubcommand1",
version = "subcommand1 0.1",
description = "test subcommand1 "
)

public class SubCommand1Tool implements Callable<Void> {


	private static final Logger LOG = Logger.getLogger(SubCommand1Tool.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	
    @Option(names = {"--value1"},
    		arity = "1",
            description = "default1")
	public String value1 = "none";

    @Option(names = {"--flag1"},
    		arity = "0",
            description = "flag1")
	public boolean flag1 = false;


	public SubCommand1Tool() {
		LOG.debug("TestSubCommand1");
	}
	
    public static void main(String[] args) throws Exception {
    	LOG.debug("SubCommand1Tool().runCommands()");
    	new SubCommand1Tool().runCommands(args);
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
		LOG.debug("value1 " + value1 + " / flag1 " + flag1);
	}

	@Override
    public Void call() throws Exception {
		LOG.debug("*******Subcommand1**************call()");
        return null;
    }



}
