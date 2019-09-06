package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.text.SVGTextLine;
import org.contentmine.graphics.svg.text.SVGTextLineList;

public class SuscriptFormatter {

	private static final Logger LOG = Logger.getLogger(SuscriptFormatter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	/** merges lines to create subscripted lines
	 * removes any lines merged as suscripts
	 * @param textCache TODO
	 */
	public void addSuscripts(TextCache textCache) {
		SVGTextLineList allTextLineList = textCache.getOrCreateTextLines();
		SVGTextLineList largeTextLineList = textCache.getTextLinesForLargestFont();
		allTextLineList.removeDuplicates(largeTextLineList);
		if (largeTextLineList.size() == 0 || allTextLineList.size() == 0) {
			return;
		}
		
		int indexAll = 0;
		SVGTextLine lineAll = allTextLineList.get(0);
		Real2Range allBBox = lineAll.getBoundingBox().format(1);
		
		int indexLarge = 0;
		SVGTextLine lineLarge = largeTextLineList.get(0);
		Real2Range largeBBox = lineLarge.getBoundingBox().format(1);
		
		List<SVGTextLine> removedLines = new ArrayList<SVGTextLine>();
		while (indexAll < allTextLineList.size() && indexLarge < largeTextLineList.size()) {
			if (allBBox.hasAllYCompletelyLowerThan(largeBBox)) {
				if (++indexAll >= allTextLineList.size()) break;
				lineAll = allTextLineList.get(indexAll);
				allBBox = lineAll.getBoundingBox().format(1);
			} else if (largeBBox.hasAllYCompletelyLowerThan(allBBox)) {
				if (++indexLarge >= largeTextLineList.size()) break;
				lineLarge = largeTextLineList.get(indexLarge);
				largeBBox = lineLarge.getBoundingBox().format(1);
			} else {
				if (lineLarge.compareTo(lineAll) == 0) {
					LOG.trace("SKIPPED DUPLICATE");
				} else {
					lineLarge.mergeLine(lineAll);
					removedLines.add(lineAll);
				}
				if (++indexAll >= allTextLineList.size()) break;
				lineAll = allTextLineList.get(indexAll);
				allBBox = lineAll.getBoundingBox().format(1);
			}
		}
		textCache.textLines.removeAll(removedLines);
		textCache.textLines.addAll(largeTextLineList);
	}

}
