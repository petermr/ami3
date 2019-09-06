package org.contentmine.image.plot.spectra;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.junit.Test;

import junit.framework.Assert;

public class SpectraIT {
	private static final Logger LOG = Logger.getLogger(SpectraIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	public void readSpectrum() {
		String base = "page.110.image.0";
		File sourceDir = new File(SVGHTMLFixtures.I_SPECTRA_DIR, "paper1");
		File targetDir = new File(SVGHTMLFixtures.I_SPECTRA_TARGET_DIR, base);
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer()
			.setBase(base)
			.setInputDir(sourceDir)
			.setOutputDir(targetDir)
			.setThinning(null)
			;
		diagramAnalyzer.readAndProcessInputFile();
		PixelIslandList pixelIslandList = diagramAnalyzer.getOrCreateSortedPixelIslandList();
		Assert.assertTrue("pixel islands "+pixelIslandList.size(), TestUtil.roughlyEqual(525, pixelIslandList.size(), 0.02));
		diagramAnalyzer.writeLargestPixelIsland();
		File file = new File(targetDir, "islands.svg");
		String[] fill = {"red", "blue", "green", "magenta", "cyan"};
		SVGG g = new SVGG();
		for (int i = 0; i < Math.min(100,  pixelIslandList.size()); i++) {
			pixelIslandList.get(i).getPixelList().plotPixels(g, fill[i % fill.length]);
		}
		SVGSVG.wrapAndWriteAsSVG(g, file);
		PixelIsland largestPixelIsland = pixelIslandList.get(0);
		PixelGraph largestGraph = largestPixelIsland.getOrCreateGraph();
//		LOG.debug("pg "+largestGraph);
		largestGraph.doEdgeSegmentation();
		largestGraph.setSegmentCreationTolerance(0.2);
		SVGG segments = largestGraph.createSegmentedEdgesSVG();
//		segments = new SVGG();
		File segmentsFile = new File(targetDir, "segments.svg");
		largestGraph.drawNodes(new String[] {"red"}, segments);
		SVGSVG.wrapAndWriteAsSVG(segments, segmentsFile);
	}
}
