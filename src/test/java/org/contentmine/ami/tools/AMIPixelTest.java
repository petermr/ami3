package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.pixel.IslandRingList;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRingList;
import org.contentmine.image.processing.HilditchThinning;
import org.contentmine.image.processing.ZhangSuenThinning;

import org.junit.Assert;
import org.junit.Test;


/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMIPixelTest extends AbstractAMIImageTest /*AbstractAMITest*/ {
	private static final Logger LOG = LogManager.getLogger(AMIPixelTest.class);
	private static final File TARGET_DIR = new AMIPixelTest().createAbsoluteTargetDir();
	private static File UCLFOREST = new File(SRC_TEST_AMI, "uclforest");
	private static File FORESTPLOTSMALL = new File(UCLFOREST, "forestplotsmall"); // project
	private static CProject SMALL_PROJECT = new CProject(FORESTPLOTSMALL);
	private static CTree CAMPBELL = SMALL_PROJECT.getOrCreateExistingCTree("campbell");
	private static CTree BUZICK = SMALL_PROJECT.getOrCreateExistingCTree("buzick");

	private PixelIslandList islandList;
	private int minHairLength;
	private int maxIslands;
	
	public AMIPixelTest() {
		setDefaults();
	}
	
	@Test
	public void testPixelForestPlotsSmallTree() throws Exception {
		String cmd = 
				" -t " +  new File(FORESTPLOTSMALL, "campbell") 
				+ " --maxislands 1000 --minimumx 50 --minimumy 50"
				;
		AMI.execute(cmd);
	}
	
	@Test
	public void testPixelForestPlotsSmallProject() throws Exception {
		String cmd = 
				" -p "+ SMALL_PROJECT.getDirectory()
				+ " --maxislands 50"
				+ " --minimumx 50"
				+ " --minimumy 50"
				;
		AMI.execute(cmd);
	}

	@Test
	// FAILS. Must mend
	/** 
	 * 
	 */
	public void testCampbell() throws Exception {
		File ctree = CAMPBELL.getDirectory();
		new AMIImageTool().runCommands(" --ctree " + ctree);
		AMIPixelTool amiPixel = new AMIPixelTool();
		amiPixel.runCommands(" --ctree " + ctree
				+ " --minwidth 0"
				+ " --minheight 0"
				);
		PixelIslandList pixelIslandList = amiPixel.getPixelIslandList();
		// all the islands, includes the text (some are only 1 pixel)
		Assert.assertEquals("toplevel islands", 29,  pixelIslandList.size());
		// now the top 6 (the text is all 5 pixels high or less
		amiPixel = new AMIPixelTool();
		amiPixel.runCommands(" --ctree " + ctree
				+ " --minwidth 10"
				+ " --minheight 10"
				);
		pixelIslandList = amiPixel.getPixelIslandList();
		Assert.assertEquals("toplevel islands", 6,  pixelIslandList.size());
		/*
islands > (10,10): islands: 6
[2515; ((104,276),(13,168))]  
[312; ((289,290),(13,168))] vert line
[312; ((88,89),(13,168))] vert line
[312; ((21,22),(13,168))] vert line
[203; ((164,215),(128,138))] horizontal bar + square
[169; ((160,184),(156,168))] rhombus
		 */
		// the largest pixel island (most of the plot, with horizontal, vertical lines squares and rhombus)
		PixelIsland island0 = pixelIslandList.get(0);
		PixelRingList rings = island0.getOrCreateInternalPixelRings();
		IslandRingList ringList01 = rings.get(1).getIslandRings(); 
		Assert.assertEquals(9, ringList01.size());
		IslandRingList ringList02 = rings.get(2).getIslandRings(); 
		Assert.assertEquals(9, ringList02.size());
		
		List<IslandRingList> islandRingListList = island0.getOrCreateIslandRingListList();
		LOG.debug(islandRingListList);
		int level = island0.getLevelForMaximumRingCount();
		Assert.assertEquals(1, level);
		IslandRingList ringList = islandRingListList.get(level);
		Assert.assertEquals(9, ringList.size());
		
		// vertical bar
		PixelIsland island1 = pixelIslandList.get(1);
		level = island1.getLevelForMaximumRingCount();
		Assert.assertEquals(0, level);

		// the rhombus
		PixelIsland island5 = pixelIslandList.get(5);
		level = island5.getLevelForMaximumRingCount();
		Assert.assertEquals(0, level);


		// the isolated bar
		PixelIsland island4 = pixelIslandList.get(4);
		islandRingListList = island4.getOrCreateIslandRingListList();
		LOG.debug(islandRingListList);
		level = island4.getLevelForMaximumRingCount();
		Assert.assertEquals(0, level);
		ringList = island4.getOrCreateIslandRingListList().get(level);
		Assert.assertEquals(1, ringList.size());
		
		// HAVE still to work out what is largest internal ring
		// count outwards from centre to edge and find largest increase as cutoff
		
		
		
		
	}

	@Test
	/** 
	 * LONG
	 */
	public void testProjectAndIncludeAndScale() throws Exception {
//		IslandRingList ringList;
		String cproject = "/Users/pm286/workspace/uclforest/dev";
		String ctree = cproject+"/"+"shenderovich";
		new AMIImageTool().runCommands(""
				+ " -t " + ctree
				+ " --maxwidth 1000"
				+ " --maxheight 1000"
//				+ " --includetree shenderovich "
				);
		new AMIImageTool().runCommands(""
				+ " -t " + ctree
//				+ " --includetree shenderovich "
				);
		AbstractAMITool amiPixelTool = new AMIPixelTool();
		amiPixelTool.runCommands(" -p " + cproject
				// these are not working well yet 
				+ " --minwidth 350"
				+ " --minheight 10"
				+ " --maxislands 2000"
				+ " --includetree shenderovich "
				);
		
	}
	

	@Test
	/** 
	 * 
	 */
	public void testBuzick() throws Exception {
//		IslandRingList ringList;
		CTree ctree = BUZICK;
		new AMIImageTool().runCommands(" --ctree " + ctree);
		AbstractAMITool amiPixelTool = new AMIPixelTool();
		amiPixelTool.runCommands(" --ctree " + ctree
			// these are not working well yet 
			+ " --minwidth 350"
			+ " --minheight 10"
			+ " --maxislands 2000"
//				+ " --outputDirname pixels"
			);
		
	}

	@Test
	/** 
	 * 
	 */
	public void testRSCSpectra() throws Exception {
		String ctree = "/Users/pm286/workspace/uclforest/dev/buzick";
		new AMIImageTool().runCommands(" --ctree " + ctree);
		AbstractAMITool amiPixelTool = new AMIPixelTool();
		amiPixelTool.runCommands(" --ctree " + ctree
			// these are not working well yet 
			+ " --minwidth 350"
			+ " --minheight 10"
			+ " --maxislands 2000"
//				+ " --outputDirname pixels"
			);
		
	}

	@Test
	/** refactor this */
	
	public void testSingleOctreeLayer() {
		String amiDir = "battery10";
		String cTreeName = "PMC3463005";
		String imageDirName = "image.6.2.86_509.389_714";
		String layerName = "octree";
		String channelName = "channel.1d1ce2";

		File imageFile = createChannelImageFile(amiDir, cTreeName, imageDirName, layerName, channelName);
		System.out.println(imageFile);
		
		BufferedImage image = ImageUtil.readImage(imageFile);
		image = ImageUtil.boofCVBinarization(image, 200);
		image = ImageUtil.thin(image, new HilditchThinning(image));
		
		ImageUtil.writeImageQuietly(image, new File(imageFile.toString()+".thin.png"));
		
		PixelIslandList islandList = PixelIslandList.createTidiedPixelIslandList(image);
		Assert.assertEquals("size", 147, islandList.size());
		
		int maxIslands = 10;
		int maxHairLength = 10;
		AMIPixelTest.tidyAndAnalyzeLargestIslands(imageFile, maxHairLength, islandList, maxIslands);
	}

	@Test
	/** refactor this */
	
	public void testSingleOctreeLayerDemo() {
		String amiDir = "battery10";
		String cTreeName = "PMC4062906";
		String imageDirName = "image.5.1.66_281.517_691";
		String layerName = "octree";
//		String channelName = "channel.f51d1d";
		String channelName = "channel.181616";
//		channel.f51d1d.png
//		channel.181616.png
		File imageFile = createChannelImageFile(amiDir, cTreeName, imageDirName, layerName, channelName);
		System.out.println(imageFile);
		
		BufferedImage image = ImageUtil.readImage(imageFile);
		image = ImageUtil.boofCVBinarization(image, 200);
		image = ImageUtil.thin(image, new HilditchThinning(image));
		
		ImageUtil.writeImageQuietly(image, new File(imageFile.toString()+".thin.png"));
		
		PixelIslandList islandList = PixelIslandList.createTidiedPixelIslandList(image);
//		Assert.assertEquals("size", 147, islandList.size());
		
		int maxIslands = 10;
		int maxHairLength = 10;
		AMIPixelTest.tidyAndAnalyzeLargestIslands(imageFile, maxHairLength, islandList, maxIslands);
	}


	@Test
	public void testTraceIntersectingLinesNotCompleted() {
			org.apache.logging.log4j.core.config.Configurator.setLevel("org.contentmine.ami.tools.AMIPixelTest", Level.DEBUG);

				this
				.setAMITestProjectName("battery10")
				.setTreeName("PMC3463005")
				.setImageDirName("image.6.2.86_509.389_714")
				.setLayer("octree")
				.setChannel("channel.ce4dd2")
				.assertCanReadFile(this.getImageFile() + " input", this.getImageFile(), 100)
				.assertTrue("msg",getImageFile().toString().endsWith(
					"ami3/src/test/resources/org/contentmine/ami/battery10/PMC3463005/"
					+ "pdfimages/image.6.2.86_509.389_714/octree/channel.ce4dd2.png"))
				.readImage()
				.binarize(200)
				.zhangSuenThin()
				.writeImage("zsthin")
				.assertCanReadFile("after thinning", getOutputFile(), 100)
				.createTidiedPixelIslandList()
				.setMinHairLength(10)
				.setMaxIslands(10)
				.removeIslandsWithLessThanPixelCount(8)
				.tidyAndAnalyzeLargestIslands()
				.writePixelIslandList("zsthin1")
				.assertCanReadFile("after more thinning", this.getSVGFile(), 100)
				.assertTrue(""+getSVGFile(), getSVGFile().toString().endsWith(
						"ami3/src/test/resources/org/contentmine/ami/battery10/PMC3463005/"
						+ "pdfimages/image.6.2.86_509.389_714/octree/channel.ce4dd2.png.zsthin1.svg"))
				;
	}


	@Test
	public void testMulticolorDiagrams() {
		Configurator.setLevel("org.contentmine.ami.tools.AMIPixelTest", Level.DEBUG);

		File topDir = SRC_TEST_IMAGE;
		String projectName = "steffen";
		String layer = "octree8";

		File projectDir = new File(topDir, projectName);
		System.out.println(".."+projectDir);
		Assert.assertTrue(projectDir+" exists", projectDir.exists());
		File[] cTreeFiles = projectDir.listFiles();
		for (File cTreeFile : cTreeFiles) {
			String treeName = cTreeFile.getName();
			System.out.println("...."+treeName);
			File file = new File(cTreeFile, "pdfimages/");
			Assert.assertTrue(file+" exists", file.exists());
			File[] imageDirs = file.listFiles();
			if (imageDirs != null) {
				for (File imageDir : imageDirs) {
					String image = imageDir.getName();
					System.out.println("......"+image);
					analyzeChannelFiles(topDir, projectName, treeName, image, layer);
				}
			}
		}
//		String treeName = "wang2015";
//		String image = "image.4.1.137_449.59_250";
		
	}

	private void analyzeChannelFiles(File topDir, String projectName, String treeName, String image, String layer) {
		File channelDir = new File(topDir, projectName+"/"+treeName+"/"+"pdfimages"+"/"+image+"/"+layer+"/");
		System.out.println(">>img>"+channelDir);
		for (File file : channelDir.listFiles()) {
			String fileS = file.toString();
			
			String channel = FilenameUtils.getBaseName(fileS);
			if (channel.startsWith("channel.") &&  fileS.endsWith(".png") 
					&& channel.split("\\.").length == 2) {
				imageFile = file;
				System.out.println("root "+channel);
				extractAndPlotChannel(topDir, projectName, treeName, image, layer, channel);
			}
		}
	}

	private void extractAndPlotChannel(File topDir,
			String projectName, String treeName, String image, String layer, String channel) {
		thinCreateIslandListAssertWrite(topDir, projectName, treeName, image, layer, channel);
	}

	private void thinCreateIslandListAssertWrite(File topDir, String projectName, String treeName, String image, String layer, String channel) {
		String suffix = projectName+"/"+treeName+ "/pdfimages/"+image+"/"+layer+"/"+channel+".png";
		File imageFile = this.getImageFile();
		long sizeOf = FileUtils.sizeOf(imageFile);
		if (sizeOf > 50000) {
			LOG.warn("TOO BIG "+sizeOf+" / "+imageFile);
			return;
		}
		sysou("SIZE "+sizeOf);
		((AMIPixelTest)this
		.setProjectName(topDir, projectName))
		.setTreeName(treeName)
		.setImageDirName(image)
		.setLayer(layer)
		.setChannel(channel)
		.assertCanReadFile(this.getImageFile() + " input", this.getImageFile(), 100)
		.assertTrue("img "+getImageFile()+" @ "+suffix, getImageFile().toString().endsWith(suffix))
		.readImage()
		.binarize(200)
		.zhangSuenThin()
		.writeImage("zsthin")
		.assertCanReadFile("after thinning", getOutputFile(), 100)
		.createTidiedPixelIslandList()
		.setMinHairLength(10)
		.setMaxIslands(10)
		.removeIslandsWithLessThanPixelCount(8)
		.tidyAndAnalyzeLargestIslands()
		.writePixelIslandList("zsthin1")
		.assertCanReadFile("after more thinning", this.getSVGFile(), 100)
		.assertTrue(""+getSVGFile(), getSVGFile().toString().endsWith(
				projectName+"/"+treeName+ "/pdfimages/"+image+"/"+layer+"/"+channel+".png"+".zsthin1.svg"))
		;
	}
	

	// ============================================

	private void sysou(String string) {
		// TODO Auto-generated method stub
		
	}

	private void setDefaults() {
		minHairLength = 10; //pixels
		maxIslands = 10;
	}

	private AMIPixelTest setLayer(String layer) {
		checkImageDir();
		this.layerDir = new File(imageDir, layer);
		checkLayerDir();
		return this;
	}

	private void checkLayerDir() {
		if (layerDir == null || !layerDir.exists() || !layerDir.isDirectory()) {
			throw new RuntimeException("missing layer directory under " + imageDir);
		}
	}

	private AMIPixelTest setChannel(String channel) {
		checkLayerDir();
		LOG.debug("debug channel");
		LOG.trace("trace channel");
		imageFile = new File(layerDir, channel + ".png");
		System.err.println("imageFile "+imageFile);
		checkImageFile();
		return this;
	}
	
	private AMIPixelTest setMaxIslands(int maxIslands) {
		this.maxIslands = maxIslands;
		return this;
	}

	private AMIPixelTest setMinHairLength(int minHairLength) {
		this.minHairLength = minHairLength;
		return this;
	}

	private AMIPixelTest createTidiedPixelIslandList() {
		checkImage();
		islandList = PixelIslandList.createTidiedPixelIslandList(image);
		return this;
	}

	private AMIPixelTest tidyAndAnalyzeLargestIslands() {
		checkImageFile();
		checkPixelIslandList();
		tidyAndAnalyzeLargestIslands(imageFile, minHairLength, islandList, maxIslands);
		return this;
	}
	
	private void checkPixelIslandList() {
		if (islandList == null || islandList.size() == 0) {
			LOG.warn("no pixelIslandList");
		}
	}
	
	private AMIPixelTest removeIslandsWithLessThanPixelCount(int pixelCount) {
		checkPixelIslandList();
		islandList.removeIslandsWithLessThanPixelCount(pixelCount);
		return this;
	}

	private static void tidyAndAnalyzeLargestIslands(File imageFile, int minHairLength, PixelIslandList islandList, int maxIslands) {
		islandList = islandList.sortBySizeDescending();
		for (int isl = 0; isl < Math.min(maxIslands, islandList.size()) ; isl++) {
			PixelIsland island = islandList.get(isl);
			LOG.info("is "+isl+">"+island.size());
			String filename = imageFile.toString()+"."+isl;
			ImageUtil.writeImageQuietly(island.createImage(), new File(FilenameUtils.getBaseName(filename) + ".png"));
			
			PixelGraph pixelGraph = new PixelGraph(island)
					.tidyNodesAndEdges(minHairLength)
					.repairEdges();
			
			SVGG svgg = pixelGraph.drawEdgesAndNodes();
			SVGSVG.wrapAndWriteAsSVG(svgg, new File(filename+".svg"));
		}
	}


	private static File createChannelImageFile(String amiDir, String cTreeName, String imageDirName, String layerName, String channelName) {
		CTree cTree = new CProject(new File(SRC_TEST_AMI, amiDir)).getCTreeByName(cTreeName);
		File pdfDir = cTree.getExistingPDFImagesDir();
		File imageDir = new File(pdfDir, imageDirName);
		File layerDir = new File(imageDir, layerName);
		File imageFile = new File(layerDir, channelName+".png");
		return imageFile;
	}
	
	
	private AMIPixelTest writePixelIslandList(String type) {
		checkPixelIslandList();
		svgFile = new File(imageFile.toString()+"."+type+".svg");
		SVGSVG.wrapAndWriteAsSVG(islandList.getOrCreateSVGG(), svgFile);
		return this;
	}

	// ======================================

	public PixelIslandList getIslandList() {
		return islandList;
	}

	public File getLayerDir() {
		return layerDir;
	}
	
	private PixelIslandList getPixelIslandList() {
		checkPixelIslandList();
		return islandList;
	}
	
	// ===== overridden to change return type ===

	@Override
	protected AMIPixelTest assertTrue(String msg, boolean condition) {
		return (AMIPixelTest) super.assertTrue(msg, condition);
	}

	@Override
	protected AMIPixelTest assertEquals(String msg, Object expected, Object actual) {
		return (AMIPixelTest) super.assertEquals(msg, expected, actual);
	}

	@Override
	protected AMIPixelTest assertCanReadFile(String msg, File file, long minFileSize) {
		return (AMIPixelTest) super.assertCanReadFile(msg, file, minFileSize);
	}

	@Override
	protected AMIPixelTest setAMITestProjectName(String projectName) {
		return (AMIPixelTest) super.setAMITestProjectName(projectName);
	}

	@Override
	protected AMIPixelTest setTreeName(String treeName) {
		return (AMIPixelTest) super.setTreeName(treeName);
	}

	@Override
	protected AMIPixelTest setImageName(String treeName) {
		return (AMIPixelTest) super.setImageName(treeName);
	}

	@Override
	protected AMIPixelTest setImageDirName(String imageDirName) {
		return (AMIPixelTest) super.setImageDirName(imageDirName);
	}
	
	@Override
	protected AMIPixelTest readImage() {
		return (AMIPixelTest) super.readImage();
	}
	
	@Override
	protected AMIPixelTest writeImage(String type) {
		return (AMIPixelTest) super.writeImage(type);
	}

	@Override
	protected AMIPixelTest binarize(int thresh) {
		return (AMIPixelTest) super.binarize(thresh);
	}
	
	@Override
	protected AMIPixelTest hilditchThin() {
		return (AMIPixelTest) super.hilditchThin();
	}
	
	@Override
	protected AMIPixelTest zhangSuenThin() {
		return (AMIPixelTest) super.zhangSuenThin();
	}


	// ======================================
	
	
	
}