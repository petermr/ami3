package org.contentmine.image.panel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.contentmine.cproject.util.CMineTestFixtures;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.contentmine.image.ImageUtil;
import org.junit.Assert;
import org.junit.Test;

public class ImagePanelTest {
	private static Logger LOG = Logger.getLogger(ImagePanelTest.class);

	@Test
	public void testMakePanels() throws IOException {
		File targetDir = new File("target/panel");
		CMineTestFixtures.cleanAndCopyDir(ImageAnalysisFixtures.COMPOUND_DIR, targetDir);
		BufferedImage image = ImageUtil.readImage(new File(targetDir, "2x2panel.png"));
		IntRange[] xr = new IntRange[] {new IntRange(0,  1000), new IntRange(1000,  1000*2)};
		IntRange[] yr = new IntRange[] {new IntRange(0,  768), new IntRange(768,  768*2)};
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				writeSubimage(targetDir, "panel"+x+"_"+y+".png", image, new Int2Range(xr[x], yr[y]));
			}
		}
	}

	private void writeSubimage(File targetDir, String filename, BufferedImage image, Int2Range ibbox) throws IOException {
		BufferedImage subImage = ImageUtil.clipSubImage(image, ibbox);
		File file = new File(targetDir, filename);
		if (!file.exists()) {
			ImageIOUtil.writeImageQuietly(subImage, file);
		} else {
			BufferedImage image1 = ImageUtil.readImage(file);
			Assert.assertTrue("images", image1.equals(subImage));
		}
	}
	
	
}
