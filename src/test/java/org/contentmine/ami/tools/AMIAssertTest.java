package org.contentmine.ami.tools;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIAssertTool;
import org.junit.Test;

public class AMIAssertTest {

	private static final Logger LOG = Logger.getLogger(AMIAssertTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testAssert() {
//		String source = createSourceFromProjectAndTree("-p", ForestPlotType.stata);
		File dir = new File("src/test/resources/org/contentmine/ami/tools/spssSimple");
		String source = "-p "+dir.toString();
		LOG.debug("dir "+dir+" "+Arrays.asList(dir.listFiles()));
		/** calculate projections and lines */
		new AMIAssertTool().runCommands(source
				+ " --inputname "+"raw.png"
				+ " --type image"
				+ " --size 10000 500000"
				+ " --width 300 1400"
				+ " --height 200 700"
				+ " --image"
				);
	}

}
