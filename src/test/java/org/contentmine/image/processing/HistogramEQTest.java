package org.contentmine.image.processing;

import java.io.IOException;

import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageAnalysisFixtures;
import org.junit.Test;

public class HistogramEQTest {

	/** reference test from wikipedia.
	 * 
	 * @throws IOException
	 */
	@Test
	
	public void testHistogram() throws IOException {
    	HistogramEqualization histogramEQ = new HistogramEqualization();
        histogramEQ.readImage(ImageAnalysisFixtures.HISTOGRAM_PNG);
        histogramEQ.histogramEqualization();
        ImageIOUtil.writeImageQuietly(histogramEQ.getEqualized(), "target/histogram/histogram.png");

	}

}
