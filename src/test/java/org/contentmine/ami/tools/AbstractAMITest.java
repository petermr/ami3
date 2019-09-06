package org.contentmine.ami.tools;

import java.io.InputStream;

import org.contentmine.ami.MyCommand;
import org.contentmine.ami.ReusableOptions;
import org.contentmine.cproject.util.CMineUtil;
import org.junit.Test;

import picocli.CommandLine;

public class AbstractAMITest {

	/** doesn't yet work
	 * 
	 */
	@Test
	public void testCommandMixin() {
		
		MyCommand zip = new MyCommand();
		CommandLine commandLine = new CommandLine(zip);
		ReusableOptions mixin = new ReusableOptions();
		commandLine.addMixin("myMixin", mixin);
		commandLine.parse("-vv", "--wombat", "361");
	
		// the options defined in ReusableOptions have been added to the zip command
//		assert zip.myMixin.verbosityx.length == 3;
//		System.err.println("VVV "+zip.myMixin.verbosityx.length);
//		System.out.println("WOM "+zip.myMixin.vombatus);
	
	}
	
	@Test
	public void testPython() throws Exception {
//		ProcessBuilder builder = new ProcessBuilder("java", "-version");
		String[] args = {"java", "-version"};
		CMineUtil.runProcess(args, (InputStream) null);
		
		args = new String[]{"python", "--help"};
		CMineUtil.runProcess(args, (InputStream) null);
		
//		args = new String[]{"node", "--help"};
//		CMineUtil.runProcess(args, (InputStream) null);
		
		args = new String[]{"bashx", "-xx"};
		CMineUtil.runProcess(args, (InputStream) null);
		
	}

}
