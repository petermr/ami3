package org.contentmine.image;

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.processing.ZhangSuenThinning;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class ImageProcessorTest {

	

	private static final Logger LOG = Logger.getLogger(ImageProcessorTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private ImageProcessor PROCESSOR;
	
	@Before
	public void setUp() {
		this.PROCESSOR  = new ImageProcessor();
	}
	
	/** should give a help/usage().
	 * 
	 */
	@Test
	public void testUsage() {
		String[] args = {};
		PROCESSOR.parseArgs(args);		
	}
	
	/** verify defaults
	 * 
	 */
	@Test
	public void testCommandLineDefaults() {
		String[] args = {"-d"};
		PROCESSOR.parseArgs(args);		
		Assert.assertTrue("debug", PROCESSOR.getDebug());
		Assert.assertNull("base", PROCESSOR.getBase());
		Assert.assertNull("inputFile", PROCESSOR.getInputFile());
		Assert.assertEquals("outputDir", "target", PROCESSOR.getOutputDir().toString());
		Assert.assertTrue("binarization", PROCESSOR.getBinarize());
		Assert.assertEquals("threshold", 129, PROCESSOR.getThreshold());
		Assert.assertEquals("thinning", ZhangSuenThinning.class, PROCESSOR.getThinning().getClass());
	}
	
	/** try all args.
	 * 
	 */
	@Test
	public void testCommandLine() {
		String args = "-i fred -o sue -b -d -t 180 -v z";
		PROCESSOR.parseArgs(args);		
		Assert.assertNotNull("base", PROCESSOR.getBase());
		Assert.assertEquals("base", "fred", PROCESSOR.getBase());
		Assert.assertEquals("inputFile", "fred", PROCESSOR.getInputFile().toString());
		Assert.assertNotNull("outputDir", PROCESSOR.getOutputDir());
		Assert.assertEquals("inputFile", "sue", PROCESSOR.getOutputDir().toString());
		Assert.assertTrue("binarization", PROCESSOR.getBinarize());
		Assert.assertTrue("debug", PROCESSOR.getDebug());
		Assert.assertEquals("threshold", 180, PROCESSOR.getThreshold());
		Assert.assertEquals("thinning", ZhangSuenThinning.class, PROCESSOR.getThinning().getClass());
	}
	
	
	/** some errors
	 * 
	 */
	@Test
	@Ignore // to avoid output
	public void testCommandLineErrors() {
		String args = "-i -q -o sue -b dummy -t 128 -v junk";
		PROCESSOR.parseArgsAndRun(args);		
		Assert.assertNull("base", PROCESSOR.getBase());
		Assert.assertNull("inputFile", PROCESSOR.getInputFile());
		Assert.assertNotNull("outputDir", PROCESSOR.getOutputDir());
		Assert.assertEquals("inputFile", "sue", PROCESSOR.getOutputDir().toString());
		Assert.assertTrue("binarization", PROCESSOR.getBinarize());
		Assert.assertFalse("debug", PROCESSOR.getDebug());
		Assert.assertEquals("threshold", 128, PROCESSOR.getThreshold());
		Assert.assertEquals("thinning", ZhangSuenThinning.class, PROCESSOR.getThinning().getClass());
	}
	
	/** get PixelIsland through CommandLine.
	 * 
	 */
	@Test
	public void testGetPixelIslandThroughCommandLine() {
		String argString = "--input src/test/resources/org/contentmine/image/processing/36933.png --island 0";
		 PROCESSOR.parseArgsAndRun(argString);
		 File T36933 = new File("target/36933");
		 File B36933 = new File(T36933, "binarized.png");
		 ImageIOUtil.writeImageQuietly(PROCESSOR.getBinarizedImage(), B36933);
		 File TH36933 = new File(T36933, "thinned.png");
		 ImageIOUtil.writeImageQuietly(PROCESSOR.getThinnedImage(), TH36933);
		 // and some extra 
		 PixelIsland pixelIsland = PROCESSOR.getPixelIsland();
		 Assert.assertEquals("pixelIsland",  23670, pixelIsland.size());
		 Real2Range box = pixelIsland.getBoundingBox();
		 Assert.assertTrue("box", box.isEqualTo(new Real2Range(new RealRange(60.0,1329.0), new RealRange(62.0,1330.0)) , 0.1));
		 
		 PixelIslandList pixelIslandList = PROCESSOR.getOrCreatePixelIslandList();
		 Assert.assertEquals("pixelIslandList",  221, pixelIslandList.size());
		 
	}
	static void runColours(String input, int count) {
		String filename = FilenameUtils.getBaseName(input);
		String argString = "--input "+input+" --color count "+count+" minpixel 10 maxpixel 1000000"
				+ " average"
				+ " --output target/colors/"+filename+"";
		LOG.debug("writing colours to "+filename);
		 new ImageProcessor().parseArgsAndRun(argString);
	}
	
}
