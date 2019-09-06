package org.contentmine.image.pixel;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import javax.imageio.ImageIO;

import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageProcessor;
import org.contentmine.image.ImageUtil;
import org.junit.Assert;
import org.junit.Test;

public class PixelIT {

	/** Large JPG with small fonts (<= 10)
	 * 
	 * @throws IOException
	 */
	@Test
	
	public void testLargePhyloJpg() throws IOException {
		File phyloDir = new File("target/phylo/");
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessorAndProcess(ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG));
		imageProcessor.setParameters(null);
		PixelIslandList islands = imageProcessor.getOrCreatePixelIslandList();
		Collections.sort(islands.getList(), new PixelIslandComparator(
				PixelComparator.ComparatorType.SIZE));
		Assert.assertEquals("islands:"+islands.size(), 2003, islands.size());
		SVGSVG.wrapAndWriteAsSVG(islands.getOrCreateSVGG(), new File(phyloDir, "largePhyloBoxes.svg"));
	}

	/** extracts all boxes over h=12 and width <= 20.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testExtractLargeCharacters() throws IOException {
		File charDir = new File("target/chars/");
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessorAndProcess(ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG));
		PixelIslandList islands = imageProcessor.getOrCreatePixelIslandList();
		PixelIslandList characters = islands.isContainedIn(new RealRange(0.,
				20.), new RealRange(12., 25.));
		Assert.assertEquals("all chars "+characters.size(), 60, characters.size());
		PixelIslandTest.plotBoxes(characters, new File(charDir, "charsHeightLarge.svg"));
	}

	/** extracts all smallish boxes with 0 or 1 height.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testExtractZeroHeightCharacterBoxes() throws IOException {
		File charDir = new File("target/chars/");
		ImageProcessor imageProcessor = ImageProcessor.createDefaultProcessorAndProcess(ImageUtil.readImage(ImageAnalysisFixtures.LARGE_PHYLO_JPG));
		PixelIslandList islands = imageProcessor.getOrCreatePixelIslandList();
		imageProcessor.setParameters(null);
		PixelIslandList characters = islands.isContainedIn(new RealRange(0.,
				20.), new RealRange(0., 1.));
		Assert.assertEquals("all chars "+characters.size(), 196, characters.size());
		PixelIslandTest.plotBoxes(characters, new File(charDir, "chars0-1.svg"));
	}

}
