package org.contentmine.image.plot.early.plot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntegerMultisetList;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.test.TestUtil;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGHTMLFixtures;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.util.ImageIOUtil;
import org.contentmine.image.ImageUtil;
import org.contentmine.image.colour.ColorAnalyzer;
import org.contentmine.image.diagram.DiagramAnalyzer;
import org.contentmine.image.pixel.PixelEdge;
import org.contentmine.image.pixel.PixelEdgeList;
import org.contentmine.image.pixel.PixelGraph;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelIslandList;
import org.contentmine.image.pixel.PixelNode;
import org.contentmine.image.pixel.PixelNodeList;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import junit.framework.Assert;

/** extracts structures from adrenaline paper
 * 
 * @author pm286
 *
 */
/** takes far too long
 * 
 * @author pm286
 *
 */
@Ignore
public class PlotImageIT {
	private static final Logger LOG = Logger.getLogger(PlotImageIT.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	@Test
	@Ignore // long and just finds thresholds
	public void testBrominePlotRead() {
		String base = "brominePlot";
		File targetDir = SVGHTMLFixtures.EARLY_PLOT_TARGET_DIR;
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer()
				.setBase(base)
				.setInputDir(SVGHTMLFixtures.EARLY_PLOT_DIR)
				.setOutputDir(targetDir);
		diagramAnalyzer.setBase(base);
		int[] thresholds = new int[]{/*50, 70, 90, */110, 130, 150};
		diagramAnalyzer.scanThresholds(thresholds);
		PixelIslandList pixelIslandList = diagramAnalyzer.writeLargestPixelIsland();
		Assert.assertEquals("islands", 388, pixelIslandList.size());
	}
	
	@Test
	public void testBrominePlotPoints() {
		String base = "brominePlot";
		File targetDir = SVGHTMLFixtures.EARLY_PLOT_TARGET_DIR;
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer()
			.setBase(base)
			.setInputDir(SVGHTMLFixtures.EARLY_PLOT_DIR)
			.setOutputDir(targetDir)
			.setThreshold(135);
		diagramAnalyzer.readAndProcessInputFile();
		diagramAnalyzer.writeBinarizedFile(new File(targetDir, "binarized.png"));
		PixelIslandList pixelIslandList = diagramAnalyzer.writeLargestPixelIsland();
//		Assert.assertEquals("islands", 63, pixelIslandList.size());
		Assert.assertTrue("islands", TestUtil.roughlyEqual(86, pixelIslandList.size(), 0.25));
		SVGG g = new SVGG();
		PixelIsland largestIsland = pixelIslandList.get(0);
		g.appendChild(largestIsland.getOrCreateSVGG().copy());
		int i = 0;
		for (PixelIsland pixelIsland : pixelIslandList) {
			Real2Range bbox = pixelIsland.getBoundingBox();
			double width = bbox.getXRange().getRange();
			double height = bbox.getYRange().getRange();
//			LOG.debug("W H "+width+"/"+height);
			if (width < 20 && width > 3 &&
				height < 20 && height > 3) {
				g.appendChild(pixelIsland.getOrCreateSVGG());
				Real2 xy = new Real2(bbox.getXMax(), bbox.getYMax());
				SVGText t = new SVGText(xy, ""+xy.format(0));
				t.setFontSize(12.0);
				t.setFill("blue");
				g.appendChild(t);
			}
			i++;
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "points.svg"));
		PixelGraph graph = new PixelGraph(largestIsland);
		PixelNodeList nodeList = graph.getOrCreateNodeList();
		Assert.assertTrue("nodes", TestUtil.roughlyEqual(261, nodeList.size(), 0.1));
		PixelEdgeList edgeList = graph.getOrCreateEdgeList();
		Assert.assertTrue("edges", TestUtil.roughlyEqual(374, edgeList.size(), 0.1));
		SVGG gg = new SVGG();
		Multiset<Integer> horizontalLengthSet = HashMultiset.create();
		Multiset<Integer> verticalLengthSet = HashMultiset.create();
		for (PixelEdge edge : edgeList) {
			SVGLine line = edge.getLine();
			if (line != null) {
				Integer length = new Integer((int)(double)line.getLength());
				if (length > 15) {
					if (line.isHorizontal(5.0)) {
						horizontalLengthSet.add(length);
					} else if (line.isVertical(5.0)) {
						verticalLengthSet.add(length);
					} else {
						line = null;
					}
					if (line != null) {
						line.setWidth(2.0);
						gg.appendChild(line);
						line.setStroke("blue");
					}
				}
			}
		}
		List<Multiset.Entry<Integer>> horizontalLengths = MultisetUtil.createEntryList(
				 MultisetUtil.getEntriesSortedByCount(horizontalLengthSet));
		LOG.trace("hor "+horizontalLengths);
		
		List<Multiset.Entry<Integer>> verticalLengths = MultisetUtil.createListSortedByCount(verticalLengthSet);
		LOG.trace("vert "+verticalLengths);
		SVGSVG.wrapAndWriteAsSVG(gg, new File(targetDir, "gridLines.svg"));
		
	}
	
	@Test
	public void testBrominePlotCoords() {
		String base = "brominePlot";
		File targetDir = SVGHTMLFixtures.EARLY_PLOT_TARGET_DIR;
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer()
			.setBase(base)
			.setInputDir(SVGHTMLFixtures.EARLY_PLOT_DIR)
			.setOutputDir(targetDir)
			.setThreshold(135);
		diagramAnalyzer.readAndProcessInputFile();
		PixelIsland largestIsland = diagramAnalyzer.getLargestIsland();

		PixelGraph graph = new PixelGraph(largestIsland);
		PixelNodeList nodeList = graph.getOrCreateNodeList();
		Assert.assertTrue("nodes", TestUtil.roughlyEqual(281, nodeList.size(), 1.1));
		Multiset<Integer> xSet = HashMultiset.create();
		Multiset<Integer> ySet = HashMultiset.create();
		SVGG g = new SVGG();
		for (PixelNode node : nodeList) {
			if (node.edgeCountLongerThan(100) > 0) {
				Real2 xy = node.getReal2();
				Integer ix = new Integer((int)xy.getX());
				Integer iy = new Integer((int)xy.getY());
				xSet.add(ix);
				ySet.add(iy);
				SVGCircle circle = new SVGCircle(xy, 3.0);
				g.appendChild(circle);
				g.appendChild(SVGText.createDefaultText(xy, xy.trimToIntegersWithoutBrackets()
						, 10, "blue"));
			}
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "nodes.svg"));
		List<Multiset.Entry<Integer>> xEntries = MultisetUtil.createEntryList(
				 MultisetUtil.getEntriesSortedByValue(xSet));
		Assert.assertEquals(57, xEntries.size());
		List<Multiset.Entry<Integer>> yEntries = MultisetUtil.createEntryList(
				 MultisetUtil.getEntriesSortedByValue(ySet));
		Assert.assertEquals(75, yEntries.size());
	}
	
	@Test
	public void testBromineFindGrid() {
		String base = "brominePlot";
		File targetDir = SVGHTMLFixtures.EARLY_PLOT_TARGET_DIR;
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer()
			.setBase(base)
			.setInputDir(SVGHTMLFixtures.EARLY_PLOT_DIR)
			.setOutputDir(targetDir)
			.setThreshold(135);
		diagramAnalyzer.readAndProcessInputFile();
		PixelIsland largestIsland = diagramAnalyzer.getLargestIsland();

		PixelGraph graph = new PixelGraph(largestIsland);
		PixelNodeList nodeList = graph.getOrCreateNodeList();
//		Assert.assertEquals("nodes", 256,  nodeList.size());
		Multiset<Integer> xSet = HashMultiset.create();
		Multiset<Integer> ySet = HashMultiset.create();
		SVGG g = new SVGG();
		int deltaX = 20;
		int deltaY = 20;
		IntegerMultisetList xBinList = new IntegerMultisetList();
		xBinList.createMultisets(nodeList.getXArray(), deltaX);
		IntegerMultisetList yBinList = new IntegerMultisetList();
		yBinList.createMultisets(nodeList.getYArray(), deltaY);
		
		for (PixelNode node : nodeList) {
			Int2 xy = node.getInt2();
			Integer ix = new Integer((int)xy.getX());
			if (ix < 75) continue;
			ix = ((ix + deltaX/2) / deltaX) * deltaX;
			Integer iy = new Integer((int)xy.getY());
			if (iy < 75 || iy > 900) continue;
			iy = ((iy + deltaY/2) / deltaY) * deltaY;
			xSet.add(ix);
			ySet.add(iy);
			Real2 xyr = new Real2(xy);
			String stroke = "blue";
			double strokeWidth = 3.0;
			double rad = 5.0;
			if (node.edgeCountLongerThan(100) > 0) {
				if (node.edgeCountLongerThan(100) > 1) {
					stroke = "red";   
					strokeWidth = 1.0;
					rad = 3.0;
				}
//				g.appendChild(SVGText.createDefaultText(xyr, String.valueOf(xy), 10, "blue"));
			} else if (node.getEdges().size() > 1) {
				stroke = "green";
			}
			SVGCircle circle = new SVGCircle(xyr, rad);
			circle.setFill("none").setStroke(stroke).setStrokeWidth(strokeWidth);
			g.appendChild(circle);
		}
		
		SVGSVG.wrapAndWriteAsSVG(g, new File(targetDir, "nodes.svg"));
		List<Multiset.Entry<Integer>> xEntries = MultisetUtil.createEntryList(
				 MultisetUtil.getEntriesSortedByValue(xSet));
		IntArray xBins = createIndexBins(xEntries);
		LOG.trace("x "+xBins);
		LOG.trace(MultisetUtil.createEntriesWithCountGreater(xEntries, 6));
		List<Multiset.Entry<Integer>> yEntries = MultisetUtil.createEntryList(
				 MultisetUtil.getEntriesSortedByValue(ySet));
		IntArray yBins = createIndexBins(yEntries);
		LOG.trace(MultisetUtil.createEntriesWithCountGreater(yEntries, 6));
	}

	@Test
	public void testCharacters() throws IOException {
		String base = "brominePlot";
		File targetDir = SVGHTMLFixtures.EARLY_PLOT_TARGET_DIR;
		BufferedImage image = ImageUtil.readImage(new File(SVGHTMLFixtures.EARLY_PLOT_DIR, base+".png"));
		ColorAnalyzer colorAnalyzer = new ColorAnalyzer(image);
		//fails (I think wrong image type)
		BufferedImage grayImage = colorAnalyzer.getGrayscaleImage();
		ImageIOUtil.writeImageQuietly(grayImage, new File(targetDir, "grayscale.png"));
		
		DiagramAnalyzer diagramAnalyzer = new DiagramAnalyzer()
			.setBase(base)
			.setInputDir(SVGHTMLFixtures.EARLY_PLOT_DIR)
			.setOutputDir(targetDir)
			.setThinning(null)
			.setThreshold(135);
		diagramAnalyzer.readAndProcessInputFile();
		diagramAnalyzer.writeBinarizedFile(new File(targetDir, "binarizedNoThin.png"));
		// didn't really work
	}

	//  ===========================
	
	private IntArray createIndexBins(List<Entry<Integer>> entries) {
		IntArray intArray = new IntArray();
		for (Entry<Integer> entry : entries) {
			intArray.addElement(entry.getElement());
		}
		return intArray;
	}

	
}
