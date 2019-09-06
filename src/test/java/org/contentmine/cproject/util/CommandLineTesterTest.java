package org.contentmine.cproject.util;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/** tests CommandLineTester.
 * 
 * @author pm286
 *
 */
public class CommandLineTesterTest {

	@Test
	@Ignore // pwd is not consistent for different users
	public void testPwd() throws Exception {
		CommandLineTester tester = new CommandLineTester();
		tester.setCommand("pwd");
		tester.run();
		String output = tester.getOutputString();
		Assert.assertEquals("pwd", System.getProperty("user.home")+"/workspace/cproject\n", output);
	}

	@Test
	public void testLs() throws Exception {
		CommandLineTester tester = new CommandLineTester();
		tester.setCommand("ls");
		tester.addArgument("-l");
		tester.addArgument("pom.xml");
		tester.run();
		String output = tester.getOutputString();
		Assert.assertTrue("ls: "+output, output.endsWith("pom.xml\n"));
		Assert.assertTrue("ls: "+output, output.split("\\s+").length > 8); 
	}
}
