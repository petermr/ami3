package org.contentmine.image;

import org.junit.Test;

public class ImageProcessorIT {

	/** get PixelIsland through CommandLine.
		 * 
		 */
		@Test
		public void testColorAnalyzeThroughCommandLine() {
			String[] inputs = {
					
					"src/test/resources/org/contentmine/image/processing/phylo.png", // not too bad
					// OMIT too long
	//				"src/test/resources/org/contentmine/image/compound/c3dt32741h.png",
	//				"src/test/resources/org/contentmine/image/compound/c6ee02494g-f6_hi-res.gif",
	//				"src/test/resources/org/contentmine/image/compound/c5ee02740c-f8_hi-res.gif", 
	//				"src/test/resources/org/contentmine/image/compound/JV_6.gif", 
			};
			for (String input : inputs) {
				ImageProcessorTest.runColours(input, 2);
			}
	
			//		ColourAnalyzer colorAnalyzer = new ColourAnalyzer();
	//		colorAnalyzer.readImage(new File(Fixtures.PROCESSING_DIR, filename+".png"));
	//		colorAnalyzer.setStartPlot(1);
	//		colorAnalyzer.setMaxPixelSize(1000000);
	//		colorAnalyzer.setIntervalCount(4);
	//		colorAnalyzer.setEndPlot(15);
	//		colorAnalyzer.setMinPixelSize(300);
	//		colorAnalyzer.flattenImage();
	//		colorAnalyzer.setOutputDirectory(new File("target/"+filename));
	//		colorAnalyzer.analyzeFlattenedColours();
	
	//		 File T36933 = new File("target/36933");
	//		 File B36933 = new File(T36933, "binarized.png");
	//		 ImageIOUtil.writeImageQuietly(PROCESSOR.getBinarizedImage(), B36933);
	//		 File TH36933 = new File(T36933, "thinned.png");
	//		 ImageIOUtil.writeImageQuietly(PROCESSOR.getThinnedImage(), TH36933);
	//		 // and some extra 
	//		 PixelIsland pixelIsland = PROCESSOR.getPixelIsland();
	//		 Assert.assertEquals("pixelIsland",  23670, pixelIsland.size());
	//		 Real2Range box = pixelIsland.getBoundingBox();
	//		 Assert.assertTrue("box", box.isEqualTo(new Real2Range(new RealRange(60.0,1329.0), new RealRange(62.0,1330.0)) , 0.1));
			 
	//		 PixelIslandList pixelIslandList = PROCESSOR.getOrCreatePixelIslandList();
	//		 Assert.assertEquals("pixelIslandList",  221, pixelIslandList.size());
			 
		}

}
