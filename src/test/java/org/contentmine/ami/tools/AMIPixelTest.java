package org.contentmine.ami.tools;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIImageTool;
import org.contentmine.ami.tools.AMIPixelTool;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.image.pixel.IslandRingList;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelRingList;
import org.junit.Test;

import junit.framework.Assert;

/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMIPixelTest {
	private static final Logger LOG = Logger.getLogger(AMIPixelTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

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


}
