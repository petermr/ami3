package org.contentmine.ami.tools;

import java.io.File;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AMIImageTool;
import org.junit.Test;

/** test cleaning.
 * 
 * @author pm286
 *
 */
public class AMIImageTest {
	private static final String OLD_DEVTEST = "/Users/pm286/workspace/uclforest/devtest/";
	private static final Logger LOG = Logger.getLogger(AMIImageTest.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void testHelp() {
		new AMIImageTool().runCommands("--help");
	}
	
	@Test
	/** 
	 */
	public void testFilterTrees() throws Exception {
		String args = 
//				"-t /Users/pm286/workspace/uclforest/devtest/bowmann-perrottetal_2013"
//				"-t /Users/pm286/workspace/uclforest/devtest/buzick_stone_2014_readalo"
//				"-t /Users/pm286/workspace/uclforest/devtest/campbell_systematic_revie"
//				"-t /Users/pm286/workspace/uclforest/devtest/case_systematic_review_ar"
				"-t " + OLD_DEVTEST + "mcarthur_etal2012_cochran"
//				"-t /Users/pm286/workspace/uclforest/devtest/puziocolby2013_co-operati"
//				"-t /Users/pm286/workspace/uclforest/devtest/torgersonetal_2011dferepo"
//				"-t /Users/pm286/workspace/uclforest/devtest/zhengetal_2016"
				+ " --sharpen sharpen4"
				+ " --threshold 180"
				+ " --binarize GLOBAL_ENTROPY"
//				+ " --rotate 270"
				+ " --priority SCALE"
				;
		new AMIImageTool().runCommands(args);
	}

	@Test
	/** 
	 * mainly for visual inspection of results
	 */
	public void testBinarize() throws Exception {
		String args = 
//				"-t /Users/pm286/workspace/uclforest/devtest/bowmann-perrottetal_2013"
//				"-t /Users/pm286/workspace/uclforest/devtest/buzick_stone_2014_readalo"
//				"-t /Users/pm286/workspace/uclforest/devtest/campbell_systematic_revie"
				"-t /Users/pm286/workspace/uclforest/devtest/case_systematic_review_ar"
//				"-t /Users/pm286/workspace/uclforest/devtest/mcarthur_etal2012_cochran"
//				"-t /Users/pm286/workspace/uclforest/devtest/puziocolby2013_co-operati"
//				"-t /Users/pm286/workspace/uclforest/devtest/torgersonetal_2011dferepo"
//				"-t /Users/pm286/workspace/uclforest/devtest/zhengetal_2016"
//				+ " --sharpen sharpen4"
				+ " --threshold 180"
				+ " --binarize BLOCK_OTSU"
//				+ " --rotate 270"
				+ " --priority SCALE"
				;
		new AMIImageTool().runCommands(args);
	}
	
	@Test
	/** 
	 * mainly for visual inspection of results
	 */
	public void testBinarizeMethods() throws Exception {
		String args = "-t /Users/pm286/workspace/uclforest/devtest/case_systematic_review_ar --binarize ";
		String[] methods = {
			"GLOBAL_MEAN",
			"GLOBAL_ENTROPY",
			"BLOCK_MIN_MAX",
			"BLOCK_OTSU",
//			"LOCAL_MEAN",    // large spurius blocks
//			"BLOCK_MEAN",    // many large spurious blocks
//			"LOCAL_GAUSSIAN", // spurious blocks
//			"LOCAL_SAUVOLA",  // stripes
//			"LOCAL_NICK",    // erosion
//			"GLOBAL_OTSU",   // spurious blocks
		};
		for (String method : methods) {
			new AMIImageTool().runCommands(args + method);
		}
	}

	@Test
	/** 
	 * mainly for visual inspection of results
	 */
	public void testBinarizeMethodsAll() throws Exception {
		String[] names = {
            "bowmann-perrottetal_2013",
            "buzick_stone_2014_readalo",
            "campbell_systematic_revie",
            "case_systematic_review_ar",
            "case-systematic-review-ju",
            "cole_2014",
            "davis2010_dissertation",
            "donkerdeboerkostons2014_l",
            "ergen_canagli_17_",
            "fanetal_2017_meta_science",
            "higginshallbaumfieldmosel",
            "kunkel_2015",
            "marulis_2010-300-35review",
            "mcarthur_etal2012_cochran",
            "puziocolby2013_co-operati",
            "rui2009_meta_detracking",
            "shenderovichetal_2016_pub",
            "torgersonetal_2011dferepo",
            "zhengetal_2016",
		};
		String[] methods = {
			"GLOBAL_MEAN",
			"GLOBAL_ENTROPY",
			"BLOCK_MIN_MAX",
			"BLOCK_OTSU",
		};
		for (String name : names) {
			String tree = "-t /Users/pm286/workspace/uclforest/devtest/";
			String treename = tree + name;
			String args = treename + " --binarize ";
			for (String method : methods) {
//				new AMIImageTool().runCommands(args + method);
			}
			for (int threshold : new int[] {120, 140, 160, 180, 200, 220}) {
				args = treename + " --monochrome --small --duplicate --sharpen sharpen4 --threshold "+threshold;
				new AMIImageTool().runCommands(args);
			}
		}
	}



	/** 
	imageProcessor.setThreshold(180);
	This is a poor subpixel image which need thresholding and sharpening
 */
	@Test
	public void testFilterProject() throws Exception {
		String args = 
				"-p /Users/pm286/workspace/uclforest/devtest/"
				;
		new AMIImageTool().runCommands(args);
	}


	@Test
	/** 
	 */
	public void testBinarizeTrees() throws Exception {
		String args = 
//				"-t /Users/pm286/workspace/uclforest/devtest/bowmann-perrottetal_2013"
				"-t /Users/pm286/workspace/uclforest/devtest/buzick_stone_2014_readalo"
//				"-t /Users/pm286/workspace/uclforest/devtest/campbell_systematic_revie"
//				"-t /Users/pm286/workspace/uclforest/devtest/mcarthur_etal2012_cochran"
//				"-t /Users/pm286/workspace/uclforest/devtest/puziocolby2013_co-operati"
//				"-t /Users/pm286/workspace/uclforest/devtest/torgersonetal_2011dferepo"
//				"-t /Users/pm286/workspace/uclforest/devtest/zhengetal_2016"
//				+ " --binarize xLOCAL_MEAN"
				+ " --threshold 180"
				+ " --sharpen x"
				;
		new AMIImageTool().runCommands(args);
	}


	@Test
	/** 
		imageProcessor.setThreshold(180);
		This is a poor subpixel image which need thresholding and sharpening
	 */
	public void testScale() throws Exception {
		String args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --scalefactor 0.5"
				+ " --maxwidth 100"
				+ " --maxheight 100"
						
				+ " --basename scale0_5";
		new AMIImageTool().runCommands(args);
	}

	@Test
	/** 
	 * rotate by multiples of 90 degrees
	 */
	public void testRotate() throws Exception {
		String args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --rotate 90"
				+ " --basename rot90";
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --rotate 180"
				+ " --basename rot180";
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --rotate 270"
				+ " --basename rot270";
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --rotate 0"
				+ " --basename rot0";
		new AMIImageTool().runCommands(args);
	}

	@Test
	/** 
		imageProcessor.setThreshold(180);
		This is a poor subpixel image which need thresholding and sharpening
	 */
	public void testThreshold() throws Exception {
		String args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --basename noop";
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --threshold 160"
				+ " --basename thresh160";
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --threshold 20"
				+ " --basename thresh20"
				;
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --threshold 30"
				+ " --basename thresh30"
				;
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --threshold 35"
				+ " --basename thresh35"
				;
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --threshold 40"
				+ " --basename thresh40"
				;
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --threshold 200"
//				+ " --thinning none"
//				+ " --binarize min_max"
				+ " --basename threshold200";
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --threshold 220"
//				+ " --thinning none"
//				+ " --binarize min_max"
				+ " --basename threshold220";
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --threshold 225"
//				+ " --thinning none"
//				+ " --binarize min_max"
				+ " --basename threshold225";
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --threshold 230"
//				+ " --thinning none"
//				+ " --binarize min_max"
				+ " --basename threshold230";
		new AMIImageTool().runCommands(args);
		args = 
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --threshold 240"
//				+ " --thinning none"
//				+ " --binarize min_max"
				+ " --basename threshold240";
		new AMIImageTool().runCommands(args);
	}
	
	@Test
	/** 
	 */
	public void testBitmapForestPlotsSmall() throws Exception {
		String args = 
				"-p /Users/pm286/workspace/uclforest/forestplotssmall"
				+ " --threshold 180"
//				+ " --thinning none"
				+ " --binarize entropy"
				;
		new AMIImageTool().runCommands(args);
	}
	
//	@Test
//	/** 
//	 */
//	public void testSharpen() throws Exception {
//		String[] args = {
//				"-p /Users/pm286/workspace/uclforest/forestplotssmall"
//				+ " --sharpen laplacian"
//				};
//		new AMIBitmapTool().runCommands(args);
//	}
	
	@Test
	/** 
	 */
	public void testSharpen() throws Exception {
		String args =
				"-p /Users/pm286/workspace/uclforest/forestplotssmall"
				+ " --sharpen sharpen4"
				+ " --basename sharpen4"
				;
		new AMIImageTool().runCommands(args);
		args =
				"-p /Users/pm286/workspace/uclforest/forestplotssmall"
				+ " --sharpen sharpen8"
				+ " --basename sharpen8"
				;
		new AMIImageTool().runCommands(args);
		args =
				"-p /Users/pm286/workspace/uclforest/forestplotssmall"
				+ " --sharpen laplacian"
				+ " --basename laplacian"
				;
		new AMIImageTool().runCommands(args);
	}

	@Test
	/** 
	 */
	public void testSharpenBoofcv() throws Exception {
		String args =
				"-t /Users/pm286/workspace/uclforest/forestplotssmall/cole"
				+ " --sharpen sharpen4"
				+ " --basename sharpen4mean"
//				+ " --binarize local_mean"
				;
		new AMIImageTool().runCommands(args);
		args =
				"-p /Users/pm286/workspace/uclforest/forestplotssmall"
				+ " --sharpen laplacian"
				+ " --basename laplacian"
				;
		new AMIImageTool().runCommands(args);
	}
	
	@Test
	/** 
	 */
	public void testSharpenThreshold1() throws Exception {
		String args =
				"-p /Users/pm286/workspace/uclforest/forestplotssmall"
				+ " --sharpen sharpen8"
				+ " --basename sharpen8otsu"
				+ " --binarize block_otsu"

				;
		new AMIImageTool().runCommands(args);
		args =
				"-p /Users/pm286/workspace/uclforest/forestplotssmall"
				+ " --sharpen laplacian"
				+ " --basename laplacian180"
				+ " --threshold 180"
				;
		new AMIImageTool().runCommands(args);
	}

	@Test
	/** 
	 * 
	 */
	public void testImageForestPlotsSmall() throws Exception {
		String[] args = {
				"-p", "/Users/pm286/workspace/uclforest/forestplotssmall",
				"--monochrome", "true",
				"--monochromedir", "monochrome",
				"--minwidth", "100",
				"--minheight", "100",
				"--smalldir", "small",
				"--duplicates", "true", 
				"--duplicatedir", "duplicates",
				};
		AMIImageTool amiImage = new AMIImageTool();
		amiImage.runCommands(args);
	}
	
	@Test
	/** 
	 * 
	 */
	public void testImagePanels() throws Exception {
		// NYI
		String userDir = System.getProperty("user.home");
		File projectDir = new File(userDir, "projects/forestplots/spss");
		String args = 
				"-p "+projectDir+
				" --minwidth 100"+
				" --minheight 100"+
				" --monochrome monochrome"+
				" --small small"+
				" --duplicate duplicate"
				;
		AMIImageTool amiImage = new AMIImageTool();
		amiImage.runCommands(args);
	}

	@Test
	public void testAddBorders() {
		String userDir = System.getProperty("user.home");
		File projectDir = new File(userDir, "projects/forestplots/spss");
		File treeDir = new File(projectDir, "PMC5502154");
		String args = 
				"-t "+treeDir+
				" --scalefactor 2.0"+
				" --erodedilate" +
				" --borders 10 "
				;
		AMIImageTool amiImage = new AMIImageTool();
		amiImage.runCommands(args);
		
	}

	@Test
	public void testImageBug() {
		String userDir = System.getProperty("user.home");
		File projectDir = new File(userDir, "projects/carnegiemellon");
		File treeDir = new File(projectDir, "p2nax");
		String args = 
				"-t "+treeDir
				;
		AMIImageTool amiImage = new AMIImageTool();
		amiImage.runCommands(args);
		
	}

	@Test
	public void testTemplate() {
		String userDir = System.getProperty("user.home");
		File projectDir = new File(userDir, "projects/carnegiemellon");
		File treeDir = new File(projectDir, "p2nax");
		String args = 
				"-t "+treeDir+
//				" --template" +
				" --help"
				;
		AMIImageTool amiImage = new AMIImageTool();
		amiImage.runCommands(args);
		
	}


	



}
