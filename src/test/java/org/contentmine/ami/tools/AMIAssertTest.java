package org.contentmine.ami.tools;

import java.io.File;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIAssertTool;
import org.junit.Test;

import static org.junit.Assert.*;

public class AMIAssertTest {

	private static final Logger LOG = LogManager.getLogger(AMIAssertTest.class);
@Test
	public void testAssert() {
//		String source = createSourceFromProjectAndTree("-p", ForestPlotType.stata);
		File dir = new File("src/test/resources/org/contentmine/ami/tools/spssSimple");
		assertTrue("Missing resource dir " + dir, dir.isDirectory());
		assertTrue("Missing resource files in " + dir, dir.listFiles().length > 0);
		LOG.debug("dir "+dir+" "+Arrays.asList(dir.listFiles()));

		/** calculate projections and lines */
		AMI.execute("-p " + dir
				+ " --inputname "+"raw.png"
				+ " assert "
				+ " --type image"
				+ " --size 10000 500000"
				+ " --width 300 1400"
				+ " --height 200 700"
				+ " --image"
				.split(" ")
				);
	}

}
