package org.contentmine.image.processing;

import java.io.IOException;

import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.junit.Test;

public class HistogramEQIT {

	/** histogram on photograph.
	 * 
	 * not sure this is worth it. Brings up the background too much
	 * 
	 * @throws IOException
	 */
	@Test
	public void testMoleculePhotograph() throws IOException {
		HistogramEqualization histogramEQ = new HistogramEqualization();
	    histogramEQ.readImage(ImageAnalysisFixtures.MOLECULE_20131119_JPG);
	    histogramEQ.histogramEqualization();
	    ImageIOUtil.writeImageQuietly(histogramEQ.getEqualized(), "target/histogram/molecule.png");
	
	}

}
