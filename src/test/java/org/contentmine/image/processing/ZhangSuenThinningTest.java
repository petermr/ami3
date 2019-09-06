package org.contentmine.image.processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageUtil;
import org.junit.Assert;
import org.junit.Test;

public class ZhangSuenThinningTest {


	@Test
	public void testMolecule() throws IOException {
	       BufferedImage image = ImageUtil.readImage(ImageAnalysisFixtures.MALTORYZINE_BINARY_PNG);
	       Thinning thinningService = new ZhangSuenThinning(image);
	       thinningService.doThinning();
	       image = thinningService.getThinnedImage();
	       File thinnedPng = ImageIOUtil.writeImageQuietly(image, "target/thinning/zhangSuenMaltoryzine.png");
	       Assert.assertTrue(thinnedPng.exists());
	}

	// ==========================================================================
	
}
