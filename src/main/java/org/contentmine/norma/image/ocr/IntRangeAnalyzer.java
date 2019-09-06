package org.contentmine.norma.image.ocr;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.eucl.euclid.IntRange;

public class IntRangeAnalyzer {
	private static final Logger LOG = Logger.getLogger(IntRangeAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	private List<IntRange> yRangeList;
	private TextLineAnalyzer textLineAnalyzer;
	
	private IntRangeAnalyzer() {
		
	}
	
	public IntRangeAnalyzer(TextLineAnalyzer textLineAnalyzer) {
		this();
		this.textLineAnalyzer = textLineAnalyzer;
	}

	List<IntRange> excludeMinorComponents() {
		yRangeList = this.mergeOverlappingYRanges();
		List<IntRange> lineList1 = new ArrayList<>();
		for (IntRange line : yRangeList) {
			if (line.getRange() < textLineAnalyzer.minYRange) continue;
			lineList1.add(line);
		}
		return lineList1;
	}

	/**
	 * @return
	 */
	List<IntRange> createMajorNonOverlappingYRanges() {
		yRangeList = createYRangeListFromYRangeMultisets();
		yRangeList = excludeMinorComponents();
		yRangeList = mergeEquivalentYRanges(1);
		if (AbstractAMITool.isDebug(textLineAnalyzer.getAmiocrTool())) {
			LOG.debug("yRangeList: "+yRangeList.size()+": "+yRangeList);
		}
		return yRangeList;
	}
	
	public List<IntRange> getYRangeList() {
		return yRangeList;
	}

	public void setYRangeList(List<IntRange> yRangeList) {
		this.yRangeList = yRangeList;
	}

	List<IntRange> mergeEquivalentYRanges(int tolerance) {
		List<IntRange> yRangeList1 = new ArrayList<>();
		int i = 0;
		while (i < yRangeList.size()) {
			IntRange line0 = yRangeList.get(i);
			if (i == yRangeList.size() - 1) {
				yRangeList1.add(line0);
				break;
			} else {
				IntRange line1 = yRangeList.get(i + 1);
				if (line0.intersectsWith(line1)) {
					IntRange mergedLine = line0.plus(line1);
					yRangeList1.add(mergedLine);
					i += 2;
				} else {
					yRangeList1.add(line0);
					i++;
				}
			}
		}
		yRangeList = yRangeList1;
		return yRangeList;
	}

	List<IntRange> mergeOverlappingYRanges() {
		List<IntRange> lineList1 = new ArrayList<>();
		int i = 0;
		while (i < yRangeList.size()) {
			IntRange line0 = yRangeList.get(i);
			if (i == yRangeList.size() - 1) {
				lineList1.add(line0);
				break;
			} else {
				IntRange line1 = yRangeList.get(i + 1);
				if (line0.intersectsWith(line1)) {
					IntRange mergedLine = line0.plus(line1);
					lineList1.add(mergedLine);
					i += 2;
				} else {
					lineList1.add(line0);
					i++;
				}
			}
		}
		yRangeList = lineList1;
		return yRangeList;
	}

	List<IntRange> createYRangeListFromYRangeMultisets() {
		yRangeList = new ArrayList<>();
		for (IntRange yRange : textLineAnalyzer.getYRangeMultiset()) {
			boolean inserted = false;
			for (int i = 0; i < yRangeList.size(); i++) {
				IntRange yRange0 = yRangeList.get(i);
				if (yRange0.intersectsWith(yRange)) {
					yRange0 = yRange0.plus(yRange);
					yRangeList.set(i, yRange0);
					inserted = true;
					break;
				} else if (yRange.getMax() < yRange0.getMin()) {
					yRangeList.add(i, yRange);
					inserted = true;
					break;
				}
			}
			if (!inserted) {
				yRangeList.add(yRange);
			}
		}
		return yRangeList;
	}



	

}
