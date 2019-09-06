package org.contentmine.ami.tools.test;

import org.junit.Test;

import picocli.CommandLine;

public class CommandTest {

	@Test
	public void testSubCommands() {
		new CommandTool().runCommands(("--value value"
				+ " subcommand1 --value1 value1 --flag1"
//				+ " subcommand2 --value2 value2 --flag2"
				+ "").split("\\s+"));
		System.out.println("===========subcommand2===========");
		new CommandTool().runCommands(("--value value"
//				+ " subcommand1 --value1 value1 --flag1"
				+ " subcommand2 --value2 value2 --flag2"
				+ "").split("\\s+"));
	}
	

	@Test
	public void testSubCommandsLine() {
		CommandLine commandLine = new CommandLine(new CommandTool())
			    .addSubcommand(new SubCommand1Tool())
			    .addSubcommand(new SubCommand2Tool())
			    ;
			    String[] cmd = 
					    (
					    "subcommand1 --value1 value1 --flag1"
//								+ " subcommand2 --value2 value2 --flag2"
						+ "").split("\\s+");
					    commandLine.parseArgs(cmd);
				cmd = (
					    "subcommand2 --value2 value2 --flag2"
						+ "").split("\\s+");
					    commandLine.parseArgs(cmd);
	}
	
	/** from SO
	 * 
	 */

}
