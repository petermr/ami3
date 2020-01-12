package org.contentmine.ami.tools;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Test;

public class AMIGraphicsTest extends AbstractAMITest {

	private static final Logger LOG = Logger.getLogger(AMIGraphicsTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testSimpleCache() {
		File testProject = new File(PDF2SVG2, "test");
		String cmd = "-p "+testProject
				+ " --cache line linebox"
				;
		new AMIGraphicsTool().runCommands(cmd);
	}
}
