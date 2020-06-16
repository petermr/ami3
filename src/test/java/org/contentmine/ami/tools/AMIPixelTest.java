package org.contentmine.ami.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.cproject.files.CProject;
import org.contentmine.cproject.files.CTree;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.pixel.IslandRingList;
import org.contentmine.image.pixel.PixelEdgeList;
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
public class AMIPixelTest extends AbstractAMITest {
	private static final Logger LOG = Logger.getLogger(AMIPixelTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private File pdfImageDir;
	private File imageDir;
	private File layerDir;
	private File imageFile;
	private BufferedImage image;
	private PixelIslandList islandList;
	private int minHairLength;
	private int maxIslands;
	
	public AMIPixelTest() {
		setDefaults();
	}
	
	private void setDefaults() {
		minHairLength = 10; //pixels
		maxIslands = 10;
	}

	private AMIPixelTest setImageDirName(String imageDirName) {
		checkCTree();
		pdfImageDir = cTree.getExistingPDFImagesDir();
		checkPDFImagesDir();
		imageDir = new File(pdfImageDir, imageDirName);
		checkImageDir();
		return this;
	}

	private void checkPDFImagesDir() {
		if (pdfImageDir == null) {
			throw new RuntimeException("missing pdfImages directory under " + cTree.getDirectory());
		}
	}

	private void checkImageDir() {
		if (imageDir == null) {
			throw new RuntimeException("missing image directory under " + pdfImageDir);
		}
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
		this.imageFile = new File(layerDir, channel + ".png");
		checkImageFile();
		return this;
	}

	private void checkImageFile() {
		if (!imageFile.exists() || imageFile.isDirectory()) {
			throw new RuntimeException("missing image file under " + layerDir);
		}
	}

	private File getImageFile() {
		checkImageFile();
		return imageFile;
	}

	private AMIPixelTest readImage() {
		checkImageFile();
		image = ImageUtil.readImage(imageFile);
		checkImage();
		return this;
	}

	private void checkImage() {
		if (image == null) {
			throw new RuntimeException(" no image");
		}
	}

	private AMIPixelTest setMaxIslands(int maxIslands) {
		this.maxIslands = maxIslands;
		return this;
	}

	private AMIPixelTest setMinHairLength(int minHairLength) {
		this.minHairLength = minHairLength;
		return this;
	}

	private AMIPixelTest writeImage(String type) {
		checkImage();
		ImageUtil.writeImageQuietly(image, 
				new File(imageFile.toString()+"."+type+".png"));
		return this;
	}

	private AMIPixelTest createTidiedPixelIslandList() {
		checkImage();
		islandList = PixelIslandList.createTidiedPixelIslandList(image);
		return this;
	}

	private AMIPixelTest binarize(int thresh) {
		checkImage();
		image = ImageUtil.boofCVBinarization(image, thresh);
		return this;
	}

	private AMIPixelTest hilditchThin() {
		checkImage();
		image = ImageUtil.thin(image, new HilditchThinning(image));
		return this;
	}

	private AMIPixelTest zhangSuenThin() {
		checkImage();
		image = ImageUtil.thin(image, new ZhangSuenThinning(image));
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
			throw new RuntimeException("no pixelIslandList");
		}
	}
	
	private AMIPixelTest removeIslandsWithLessThanPixelCount(int pixelCount) {
		checkPixelIslandList();
		islandList.removeIslandsWithLessThanPixelCount(pixelCount);
		return this;
	}

	private static void tidyAndAnalyzeLargestIslands(File imageFile, int minHairLength, PixelIslandList islandList, int maxIslands) {
		for (int isl = 0; isl < Math.min(maxIslands, islandList.size()) ; isl++) {
			PixelIsland island = islandList.get(isl);
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



// ======================================
	
	
	@Test
	public void testPixelForestPlotsSmallTree() throws Exception {
		String[] args = {
//				"-t", "/Users/pm286/workspace/uclforest/forestplotssmall/buzick",
				"-t", "/Users/pm286/workspace/uclforest/forestplotssmall/campbell",
//				"-p", "/Users/pm286/workspace/uclforest/forestplotssmall",
				"--maxislands", "1000",
				"--minimumx", "50",
				"--minimumy", "50",
				};
		new AMIPixelTool().runCommands(args);
	}
	
	@Test
	public void testPixelForestPlotsSmallProject() throws Exception {
		String[] args = {
				"-p", "/Users/pm286/workspace/uclforest/forestplotssmall",
				"--maxislands", "50",
				"--minimumx", "50",
				"--minimumy", "50",
				};
		new AMIPixelTool().runCommands(args);
	}

	@Test
	/** 
	 * 
	 */
	public void testCampbell() throws Exception {
		String ctree = "/Users/pm286/workspace/uclforest/dev/campbell";
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
	public void testTraceIntersectingLines() {
		AMIPixelTest amiPixelTest = new AMIPixelTest();
		amiPixelTest
				.setAMITestProjectName("battery10")
				.setTreeName("PMC3463005");
		amiPixelTest
				.setImageDirName("image.6.2.86_509.389_714")
				.setLayer("octree")
				.setChannel("channel.ce4dd2")
				.readImage()
				.binarize(200)
				.zhangSuenThin()
				.writeImage("zsthin")
				.createTidiedPixelIslandList()
				.setMinHairLength(10)
				.setMaxIslands(10)
				.removeIslandsWithLessThanPixelCount(8)
				.tidyAndAnalyzeLargestIslands()
				.writePixelIslandList("zsthin1")

				;
		PixelIslandList islandList = amiPixelTest.getPixelIslandList();
		System.out.println("islands "+islandList.size());
		
		PixelEdgeList edgeList = amiPixelTest.getPixelEdgeList();
//		System.out.println(imageFile);
		
//		BufferedImage image = ImageUtil.readImage(imageFile);
//		image = ImageUtil.thin(image, new HilditchThinning(image));
		
//		ImageUtil.writeImageQuietly(image, new File(imageFile.toString()+".thin.png"));
		
//		PixelIslandList islandList = PixelIslandList.createTidiedPixelIslandList(image);
//		Assert.assertNotNull(islandList);
//		Assert.assertEquals("size", 721, islandList.size());
		
//		int maxIslands = 10;
//		int maxHairLength = 10;
//		AMIPixelTest.tidyAndAnalyzeLargestIslands(imageFile, maxHairLength, islandList, maxIslands);
	}

	private AMIPixelTest writePixelIslandList(String type) {
		checkPixelIslandList();
		SVGSVG.wrapAndWriteAsSVG(islandList.getOrCreateSVGG(), new File(imageFile.toString()+"."+type+".svg"));
		return this;
	}

	private PixelIslandList getPixelIslandList() {
		checkPixelIslandList();
		return islandList;
	}

	private PixelEdgeList getPixelEdgeList() {
		return null;
	}

	// ============================================
	
}