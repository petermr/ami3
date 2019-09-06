package org.contentmine.image.pixel;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageUtil;
import org.junit.Assert;
import org.junit.Test;

public class FloodFillTest {
	private static final Logger LOG = Logger.getLogger(FloodFillTest.class);

	/** floodfill with explicit diagonals.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testThinnedMaltoryzineDiagonal() throws IOException {
		BufferedImage image = ImageUtil.readImage(ImageAnalysisFixtures.MALTORYZINE_THINNED_PNG);
		FloodFill floodFill = new ImageFloodFill(image);
		floodFill.setDiagonal(true);
		Assert.assertEquals("pixelIslands", 5, floodFill.getIslandList().size());
		for (PixelIsland pixelIsland : floodFill.getIslandList()) {
			LOG.trace(pixelIsland.size());
		}
	}

	/** flood fill with diagonals default.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testBinaryMaltoryzine() throws IOException {
		BufferedImage image = ImageUtil.readImage(ImageAnalysisFixtures.MALTORYZINE_BINARY_PNG);
		FloodFill floodFill = new ImageFloodFill(image);
		Assert.assertEquals("pixelIslands", 5, floodFill.getIslandList().size());
	}
	
	
	/** without diagonals the fill breaks up badly.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testThinnedMaltoryzineNoDiagnal() throws IOException {
		BufferedImage image = ImageUtil.readImage(ImageAnalysisFixtures.MALTORYZINE_THINNED_PNG);
		FloodFill floodFill = new ImageFloodFill(image);
		Assert.assertEquals("pixelIslands", 202, floodFill.getIslandList().size());
	}
	
	
}
