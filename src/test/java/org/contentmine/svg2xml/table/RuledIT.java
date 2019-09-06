package org.contentmine.svg2xml.table;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.linestuff.LineMerger;
import org.contentmine.graphics.svg.linestuff.LineMerger.MergeMethod;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class RuledIT {

	/** test joining lines
	 * draws SVG showing lines to merge
	 * @throws FileNotFoundException
	 */
	@Test
	public void testLinesByWidthAndLength() throws FileNotFoundException {
		File svgFile = RuledTest.RULED1007_1;
		ComponentCache svgStore = new ComponentCache();
		svgStore.readGraphicsComponentsAndMakeCaches(svgFile);
		AbstractCMElement svgElement = (AbstractCMElement) svgStore.getExtractedSVGElement();
		List<SVGLine> lineList = SVGLine.extractSelfAndDescendantLines(svgElement);
		List<SVGLine> mergedLines = LineMerger.mergeLines(lineList, 1.0, MergeMethod.OVERLAP);
		Multiset<Double> widthSet = HashMultiset.create();
		Multiset<Double> lengthSet = HashMultiset.create();
		for (SVGLine mergedLine : mergedLines) {
			widthSet.add(Util.format(mergedLine.getStrokeWidth(), 2));
			lengthSet.add(Util.format(mergedLine.getLength(), 0));
		}
		List<Multiset.Entry<Double>> lengthEntryList = MultisetUtil.createListSortedByValue(lengthSet);
		List<Multiset.Entry<Double>> widthEntryList = MultisetUtil.createListSortedByValue(widthSet);
	}

	/** test joining lines
	 * draws SVG showing lines to merge
	 * @throws FileNotFoundException
	 */
	@Test
	public void testMergeLines() throws FileNotFoundException {
		File svgFile = RuledTest.RULED1007_1;
		ComponentCache svgStore = new ComponentCache();
		svgStore.readGraphicsComponentsAndMakeCaches(svgFile);
		AbstractCMElement svgElement = (AbstractCMElement) svgStore.getExtractedSVGElement();
		List<SVGLine> lineList = SVGLine.extractSelfAndDescendantLines(svgElement);
		Assert.assertEquals("lines", 59, lineList.size());
		RuledTest.debugLines(lineList, new File("target/ruled/lineSet.svg"));
		List<SVGLine> mergedLines = LineMerger.mergeLines(lineList, 1.0, MergeMethod.OVERLAP);
		Assert.assertEquals("merged lines", 43, mergedLines.size());
		RuledTest.debugLines(mergedLines, new File("target/ruled/mergedSet.svg"));
	}

	/** rotate element positions position
	 * @throws FileNotFoundException 
	 * 
	 */
	@Test
	public void testReadGraphicsComponents() throws FileNotFoundException {
		String[] lengths = {
			"[147.0 x 2, 409.0 x 17]",
			"[198.0 x 15]",
			"[108.0, 118.0, 268.0 x 8]",
			"[268.0 x 15]",
			"[213.0, 293.0 x 22, 422.0 x 20]",
			"[213.0, 293.0 x 22, 422.0 x 20]",
		};
		String[] widths = {
			"[0.09 x 14, 0.23 x 4, 0.47]",
			"[0.09 x 12, 0.23 x 2, 0.47]",
			"[0.09 x 5, 0.23 x 4, 0.47]",
			"[0.09 x 12, 0.23 x 2, 0.47]",
			"[0.28 x 41, 0.34, 0.4]",
			"[0.28 x 41, 0.34, 0.4]",
		};
		int i = 0;
		for (File svgFile : RuledTest.RULED_FILES) {
			ComponentCache svgStore = new ComponentCache();
			svgStore.readGraphicsComponentsAndMakeCaches(svgFile);
			AbstractCMElement svgElement = (AbstractCMElement) svgStore.getExtractedSVGElement();
			List<SVGLine> lineList = SVGLine.extractSelfAndDescendantLines(svgElement);
			List<SVGLine> mergedLines = LineMerger.mergeLines(lineList, 1.0, MergeMethod.OVERLAP);
			Multiset<Double> widthSet = HashMultiset.create();
			Multiset<Double> lengthSet = HashMultiset.create();
			for (SVGLine mergedLine : mergedLines) {
				widthSet.add(Util.format(mergedLine.getStrokeWidth(), 2));
				lengthSet.add(Util.format(mergedLine.getLength(), 0));
			}
			List<Multiset.Entry<Double>> lengthEntryList = MultisetUtil.createListSortedByValue(lengthSet);
			Assert.assertEquals("length "+i, lengths[i], lengthEntryList.toString());
			List<Multiset.Entry<Double>> widthEntryList = MultisetUtil.createListSortedByValue(widthSet);
			Assert.assertEquals("length "+i, widths[i], widthEntryList.toString());
			i++;
			
		}
	}

}
