package org.contentmine.image.colour;

import java.awt.image.BufferedImage;
import java.io.File;

import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.image.ImageUtil;
import org.junit.Test;

public class QuantizeFilterTest extends AbstractAMITest {

	@Test
	public void testQuantize() {
		File xrd = new File(SRC_TEST_AMI, "battery10/PMC4062906/pdfimages/image.4.2.66_281.103_251/raw.png");
		BufferedImage inImage = ImageUtil.readImageQuietly(xrd);
		Octree octree = new Octree();
		octree.readImage(inImage).setColourCount(32).quantize();
		BufferedImage outImage = octree.getOutImage();

		ImageUtil.writeImageQuietly(outImage,
				new File(SRC_TEST_AMI, "battery10/PMC4062906/pdfimages/image.4.2.66_281.103_251/flatten.png"));
		
		
	}

}
