package org.contentmine.ami.tools;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.cache.ComponentCache;
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
				+ " --cache line polyline polygon shape text linebox"
				;
		new AMIGraphicsTool().runCommands(cmd);
	}
	
	@Test
	/** this hangs the program. test to find out where.
	 * maybe test should be in CacheTests.
	 */
	public void testLargeSVG() {
		File largeSVG = new File(SRC_TEST_SVG, "problems/fulltext-page.5LARGE.svg");
		// problem (starts) here
		ComponentCache componentCache = ComponentCache.createComponentCache(largeSVG);
	}
	
	@Test
	public void testVeryLargeHangsIT() {
		File testFile = new File(PDF2SVG2, "problems/PMC6364917");
		String cmd = "-t "+testFile
				+ " --forcemake"
				+ " --maxprimitives 500000"
				;
		new AMIPDFTool().runCommands(cmd);

	}
	
	@Test
	/** seems to write each image twice.
	 * 
	 */
	public void testDuplicateImagesIT() {
		File testFile = new File(PDF2SVG2, "problems/PMC5963300");
		String cmd = "-t "+testFile
				+ " --forcemake"
				+ " --maxprimitives 5000"
				;
		new AMIPDFTool().runCommands(cmd);

	}
	
}
