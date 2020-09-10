package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.pixel.IslandRingList;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRing;
import org.contentmine.image.pixel.PixelRingList;
import org.contentmine.image.processing.HilditchThinning;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;


/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMIPixelTest extends AbstractAMIImageTest /*AbstractAMITest*/ {
	public static final Logger LOG = LogManager.getLogger(AMIPixelTest.class);
	private static final File TARGET_DIR = new AMIPixelTest().createAbsoluteTargetDir();
	private static File UCLFOREST = new File(SRC_TEST_AMI, "uclforest");
	private static File FORESTPLOTSMALL = new File(UCLFOREST, "forestplotsmall"); // project
	private static CProject SMALL_PROJECT = new CProject(FORESTPLOTSMALL);
//	private static CTree CAMPBELL = SMALL_PROJECT.getOrCreateExistingCTree("campbell");
//	private static CTree BUZICK = SMALL_PROJECT.getOrCreateExistingCTree("buzick");
	private static CProject BATTERY10_PROJECT = new CProject(TEST_BATTERY10);
	private static CProject STEFFEN_PROJECT = new CProject(new File(SRC_TEST_IMAGE, "steffen"));
	private static CTree JANEK_TREE = STEFFEN_PROJECT.getOrCreateExistingCTree("janek2011");

	private PixelIslandList islandList;
	private int minHairLength;
	private int maxIslands;
	private AMIPixelTool pixelTool;
	
	String cmd;
	
	public AMIPixelTest() {
		setDefaults();
	}
	
	@Test
	public void testHelp() {
		cmd = " pixel --help";
		AMI.execute(cmd);
	}
	
	@Test
	@Ignore // 18n sec may be slightly long
	public void testPixelForestPlotsTree() throws Exception {
		cmd = 
				" -t " +  JANEK_TREE.getDirectory()
				+ " pixel"
				+ " --maxislands 1000"
				;
		AMI.execute(cmd);
	}
	
	@Test
	/** 
	 * Needs refactoring
	 */
//	@Ignore
	// 15 sec
	// doesn't use image dirs... 
	public void testIslands() throws Exception {
		File ctree = JANEK_TREE.getDirectory();
		cmd = ""
				+ " --ctree " + ctree
				+ " pixel"
				+ " --minwidth 0"
				+ " --minheight 0"
				;
		AMIPixelTool pixelTool = AMI.execute(AMIPixelTool.class, cmd);
				
		PixelIslandList pixelIslandList = pixelTool.getPixelIslandList();
		// all the islands, includes the text (some are only 1 pixel)
		Assert.assertEquals("toplevel islands", 29,  pixelIslandList.size());
		// now the top 6 (the text is all 5 pixels high or less
		pixelTool = new AMIPixelTool();
		pixelTool.runCommands(" --ctree " + ctree
				+ " --minwidth 10"
				+ " --minheight 10"
				);
		pixelIslandList = pixelTool.getPixelIslandList();
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
		CProject cproject = STEFFEN_PROJECT;
		CTree ctree = JANEK_TREE; 
				cmd = ""
				+ " -t " + ctree.getDirectory()
				+ " pixel"
				+ " --maxwidth 1000"
				+ " --maxheight 1000"
				;
				AMI.execute(cmd);
				
				cmd = ""
				// these are not working well yet 
				+ " -p " + cproject
				+ " pixel"
				+ " --minwidth 350"
				+ " --minheight 10"
				+ " --maxislands 2000"
				+ " --includetree larraz2015,rettenwander2016"
				;
				AMI.execute(cmd);
		
	}
	

	@Test
	/** 
	 * 
	 */
	public void testJanek() throws Exception {
//		IslandRingList ringList;
		CTree ctree = JANEK_TREE;
			cmd = ""
			+ " -t " + ctree.getDirectory()
			+ " pixel"
			// these are not working well yet 
			+ " --minwidth 350"
			+ " --minheight 10"
			+ " --maxislands 2000"
//				+ " --outputDirname pixels"
			;
			AMI.execute(cmd);
		
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
		islandList.tidyAndAnalyzeLargestIslands(imageFile, maxHairLength, maxIslands);
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
		islandList.tidyAndAnalyzeLargestIslands(imageFile, maxHairLength, maxIslands);
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
	@Ignore // too big for routine, but uncomment to do complete project
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
//			Assert.assertTrue(file+" exists", file.exists());
			if (!file.exists()) {
				LOG.error("NO FILE " + file);
			} else {
				File[] imageDirs = file.listFiles();
				if (imageDirs != null) {
					for (File imageDir : imageDirs) {
						String image = imageDir.getName();
						System.out.println("......"+image);
						analyzeChannelFiles(topDir, projectName, treeName, image, layer);
					}
				}
			}
		}
	}

	@Test
	public void testSingleOctree() {
		Configurator.setLevel("org.contentmine.ami.tools.AMIPixelTest", Level.DEBUG);

		File topDir = SRC_TEST_IMAGE;
		String projectName = "steffen";
		String layer = "octree8";
		File projectDir = new File(topDir, projectName);
		String treeName = "wang2015";
		File cTreeFile = new File(projectDir, treeName);
		File pdfimagesDir = new File(cTreeFile, "pdfimages/");
		String image = "image.4.1.137_449.59_250";
		File imageDir = new File(pdfimagesDir, image);
		File layerDir = new File(imageDir, layer);
		String channel = "channel.2526f0";
		File channelFile = new File(layerDir , channel+".png");
		imageFile = channelFile;
		System.out.println("root "+channel);
		thinCreateIslandListAssertWrite(topDir, projectName, treeName, image, layer, channel);

//		analyzeChannelFiles(topDir, projectName, treeName, image, layer);
	}

	@Test
	public void testCreateIslandRinglist() {
		AMIPixelTool amiPixelTool = new AMIPixelTool(STEFFEN_PROJECT);
		File imageFile = new File(JANEK_TREE.getDirectory(), "pdfimages/image.5.2.364_487.78_207/raw.png");
		amiPixelTool.processImageFile(imageFile);
		PixelIslandList islandList = amiPixelTool.getPixelIslandList();
		
		int isl;
		isl = 0;
//		isl = 1;
		isl = 2;
		
		PixelIsland island = islandList.get(isl);
		SVGG g = island.getOrCreateSVGG();
		SVGSVG.wrapAndWriteAsSVG(g, new File("target/islands/island."+isl+".svg"));
		island.getOrCreateGraph();
		List<IslandRingList> ringListList = island.getOrCreateIslandRingListList();
		System.out.println(">islandRingList> "+ringListList);
		int i = 0;
		for (IslandRingList ringList : ringListList) {
			SVGSVG.wrapAndWriteAsSVG(ringList.getRing(0).getOrCreateSVG(), new File("target/islands/island."+isl+"."+(i++)+".svg"));
		}
		PixelRing outerRing = ringListList.get(0).get(0);
		outerRing.setIsland(island);
//		System.out.println("sorted "+outerRing.createSortedRing());
		PixelRing sortedRing = outerRing.createSortedRing3();
		if (true) return;
		SVGSVG.wrapAndWriteAsSVG(sortedRing.plotPixels(null, "blue", "red"), new File("target/islands/sortedPixelRing."+isl+"."+(i++)+".svg"));
		
		outerRing.displayAndSortRing(isl);
		
		// find end nodes in sorted list
	}

	// ====================private========================

	private AMIPixelTool getOrCreatePixelTool() {
		if (this.pixelTool == null) {
			this.pixelTool = new AMIPixelTool();
		}
		return pixelTool;
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
		islandList.tidyAndAnalyzeLargestIslands(imageFile, minHairLength, maxIslands);
		return this;
	}
	
	public void checkPixelIslandList() {
		if (islandList == null || islandList.size() == 0) {
			LOG.warn("no pixelIslandList");
		}
	}
	
	public AMIPixelTest writePixelIslandList(String type) {
		File svgFile = new File(imageFile.toString()+"."+type+".svg");
		SVGSVG.wrapAndWriteAsSVG(islandList.getOrCreateSVGG(), svgFile);
		return this;
	}


	private AMIPixelTest removeIslandsWithLessThanPixelCount(int pixelCount) {
		checkPixelIslandList();
		islandList.removeIslandsWithLessThanPixelCount(pixelCount);
		return this;
	}

	private static File createChannelImageFile(String amiDir, String cTreeName, String imageDirName, String layerName, String channelName) {
		CTree cTree = new CProject(new File(SRC_TEST_AMI, amiDir)).getCTreeByName(cTreeName);
		File pdfDir = cTree.getExistingPDFImagesDir();
		File imageDir = new File(pdfDir, imageDirName);
		File layerDir = new File(imageDir, layerName);
		File imageFile = new File(layerDir, channelName+".png");
		return imageFile;
	}
	
	
	private void analyzeChannelFiles(File topDir, String projectName, String treeName, String image, String layer) {
		File channelDir = new File(topDir, projectName+"/"+treeName+"/"+"pdfimages"+"/"+image+"/"+layer+"/");
		System.out.println(">>img>"+channelDir);
		for (File channelFile : channelDir.listFiles()) {
			String fileS = channelFile.toString();
			
			String channel = FilenameUtils.getBaseName(fileS);
			if (channel.startsWith("channel.") &&  fileS.endsWith(".png") 
					&& channel.split("\\.").length == 2) {
				imageFile = channelFile;
				System.out.println("root "+channel);
				thinCreateIslandListAssertWrite(topDir, projectName, treeName, image, layer, channel);
			}
		}
	}

	private void thinCreateIslandListAssertWrite(File topDir, String projectName, String treeName, String image, String layer, String channel) {
		String suffix = projectName+"/"+treeName+ "/pdfimages/"+image+"/"+layer+"/"+channel+".png";
		File imageFile = this.getImageFile();
		long sizeOf = FileUtils.sizeOf(imageFile);
		if (sizeOf > 50000) {
			LOG.warn("TOO BIG "+sizeOf+" / "+imageFile);
			return;
		}
		
		System.out.println("SIZE "+sizeOf);
		((AMIPixelTest)this
		.setProjectName(topDir, projectName))
//		.getOrCreatePixelTool().
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
//		.setMinHairLength(1)
		.setMaxIslands(10)
		.removeIslandsWithLessThanPixelCount(8)
		.tidyAndAnalyzeLargestIslands()
		.writePixelIslandList("zsthin1")
		.assertCanReadFile("after more thinning", this.getSVGFile(), 100)
		.assertTrue(""+getSVGFile(), getSVGFile().toString().endsWith(
				projectName+"/"+treeName+ "/pdfimages/"+image+"/"+layer+"/"+channel+".png"+".zsthin1.svg"))
		;
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

	// ===== MOVE TO ASSERTJ =========
	
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