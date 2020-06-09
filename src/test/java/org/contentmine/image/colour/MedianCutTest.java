package org.contentmine.image.colour;

import java.awt.image.BufferedImage;
import java.io.File;

import org.contentmine.ami.tools.AbstractAMITest;
import org.contentmine.image.ImageUtil;
import org.junit.Test;

/** uses a classic MedianCut routine from 1995.
 * 
 * @author pm286
 *
 */
public class MedianCutTest extends AbstractAMITest {

	@Test
	public void testMedianCut() {
		File xrd = new File(SRC_TEST_AMI, "battery10/PMC4062906/pdfimages/image.4.2.66_281.103_251/raw.png");
		BufferedImage image = ImageUtil.readImageQuietly(xrd);
		MedianCut medianCut = new MedianCut(image);
		medianCut.convert(8);
	}
	
	
}
