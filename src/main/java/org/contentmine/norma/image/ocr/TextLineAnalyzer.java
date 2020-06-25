package org.contentmine.norma.image.ocr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.tools.AMIOCRTool;
import org.contentmine.ami.tools.AbstractAMITool;
import org.contentmine.cproject.util.CMineUtil;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.html.HtmlTable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

public class TextLineAnalyzer {
	private static final Logger LOG = LogManager.getLogger(TextLineAnalyzer.class);
private int minEntryCount = 8;
	int minYRange = 2; // check this
	private Multiset<IntRange> yRangeMultiset;
	private Multimap<IntRange, Multiset.Entry<TextLine>> textLineEntriesByYRange;
	private Multimap<IntRange, Multiset<Integer>> lowerLimitsByYRange;
	private Multimap<IntRange, Multiset<Integer>> upperLimitsByYRange;
	private TextLineList textLineList;
	private IntArray diffArray;
	
	//  maps
	Multimap<String, CharBox> charBoxByText;
	Map<Int2Range, CharBox> charBoxByBBox;
	Multimap<String, Int2> bboxSizeByText;
	boolean disambiguate = false;
	private AMIOCRTool amiocrTool;

	
	public TextLineAnalyzer(AMIOCRTool amiocrTool) {
		this.amiocrTool = amiocrTool;
		return;
	}

	public int getMinEntryCount() {
		return minEntryCount;
	}

	public void setMinEntryCount(int minEntryCount) {
		this.minEntryCount = minEntryCount;
	}


	private /*Multimap<IntRange, Multiset.Entry<TextLine>>*/ void createMultisetsForEachTextLine() {
		
		Iterator<Entry<IntRange>> yRangeIterator = this.yRangeMultiset.entrySet().iterator();
		while (yRangeIterator.hasNext()) {
			Entry<IntRange> yRangeEntry = yRangeIterator.next();
			TextLine textLine = addEntryToContainingTextLine(yRangeEntry);
		}
		for (TextLine textLine : textLineList) {
//			LOG.debug("=====line====");
			FontGeometry fontGeometry = textLine.getOrCreateFontGeometry();
			fontGeometry.createUpperLowerLimitMultisets();
			fontGeometry.createMedianAscDescenders();
		}
				
		return;
	}

	private TextLine addEntryToContainingTextLine(Entry<IntRange> yRangeEntry) {
		IntRange yRange = yRangeEntry.getElement();
		TextLine textLine0 = null;
		for (TextLine textLine : textLineList) {
			IntRange textLineYRange = textLine.getYRange();
			if (textLineYRange.includes(yRange)) {
				textLine.addYRangeEntry(yRangeEntry);
				textLine0 = textLine;
				break;
			}
		}
		if (textLine0 == null) {
//			LOG.debug("couldn't add:"+yRange);
		}
		return textLine0;
	}
	
	private IntArray createDiffArray() {
		diffArray = new IntArray();
		for (int i = 0; i < textLineList.size(); i++) {
			TextLine textLine = textLineList.get(i);
			if (i > 0) {
				int diff = createInterlineDiff(textLineList.get(i  - 1), textLine);
				diffArray.addElement(diff);		
			}
		}
		return diffArray;
	}
	
	

	private Integer createInterlineDiff(TextLine textLine0, TextLine textLine1) {
		Integer diff = null;
		IntRange yRange0 = textLine0.getYRange();
		IntRange yRange1 = textLine1.getYRange();
		List<Multiset<Integer>> ul0 = new ArrayList<>(upperLimitsByYRange.get(yRange0));
		List<Multiset<Integer>> ll1 = new ArrayList<>(lowerLimitsByYRange.get(yRange1));
		if (ul0.size() > 0 && ll1.size() > 0) {
			Multiset<Integer> ll1set = ll1.get(0);
			Multiset<Integer> ul0set = ul0.get(0);
			if (ll1set != null && ul0set != null) {
				Comparable<Integer> ll1val = MultisetUtil.getCommonestValue(ll1set);
				Comparable<Integer> ul0val = MultisetUtil.getCommonestValue(ul0set);
				if (ll1val!= null && ul0val != null) {
					diff = (int) ll1val - (int) ul0val;
					LOG.debug("diff: "+diff);
				} else {
					LOG.debug("null diff");
				}
			}
		}
		return diff;
				
	}

	public int getMinYRange() {
		return minYRange;
	}

	public void setMinYRange(int minYRange) {
		this.minYRange = minYRange;
	}

	private void createUpperLowerLimitMultisets(TextLine textLine) {
		IntRange yRange0 =  textLine.getYRange();
		List<Entry<TextLine>> entryList = new ArrayList<>(textLineEntriesByYRange.get(yRange0));
		Multiset<Integer> upperLimits = HashMultiset.create();
		upperLimitsByYRange.put(yRange0, upperLimits);
		Multiset<Integer> lowerLimits = HashMultiset.create();
		lowerLimitsByYRange.put(yRange0, lowerLimits);
		for (Entry<TextLine> entry : entryList) {
			IntRange yRange = entry.getElement().getYRange();
			int count = entry.getCount();
			int upper = yRange.getMax();
			int lower = yRange.getMin();
			upperLimits.add(upper, count);
			lowerLimits.add(lower, count);
		}
		List<Entry<Integer>> commonestUppers = MultisetUtil.createListSortedByCount(upperLimits);
		List<Entry<Integer>> commonestLowers = MultisetUtil.createListSortedByCount(lowerLimits);
		if (commonestUppers.size() > 0 && commonestLowers.size() > 0) {
			int commonestUpper = commonestUppers.get(0).getElement();
			int commonestLower = commonestLowers.get(0).getElement();
			int commonestDiff = commonestUpper - commonestLower;
			LOG.debug(yRange0+": \n"+upperLimits+"\n"+lowerLimits+"\n"+commonestDiff);
		}
		
	}

	private void createMultisetsForLine(IntRange yRangeNew) {
		boolean added = false;
		for (IntRange yRange : getYRangeMultiset()) {
			List<Multiset.Entry<TextLine>> textLineEntriesList = 
					new ArrayList<>(textLineEntriesByYRange.get(yRange));
			// does line overlap with existing 
			if (yRangeNew.intersectsWith(yRange)) {
				for (Entry<TextLine> textLineEntry : textLineEntriesList) {
					if (textLineEntry.getCount() >= minEntryCount ) {
//						textLineEntriesByYRange.put(yRangeNew, 
//								new Multiset.Entry<TextLine>(new TextLine(yRangeNew)));
						LOG.debug("overlapping, added");
						added = true;
						break;
					}
				}
			}
			if (added) {
				break;
			}
		}
		if (!added) {
			LOG.debug("range didn't overlap");
		}
	}

	TextLineList createMajorNonOverlappingTextLines(Multiset<IntRange> yRangeMultiset) {

		this.setYRangeMultiset(yRangeMultiset);
		List<Multiset.Entry<IntRange>> sortedYRangeSet = 
				MultisetUtil.createListSortedByCount(yRangeMultiset);
		if (amiocrTool.getVerbosityInt() == AbstractAMITool.DEBUG) {
			LOG.debug("total char yranges: "+yRangeMultiset.size()+" => unique yranges "+
					sortedYRangeSet.size()+": "+sortedYRangeSet);
		}
		textLineList = createInitialEmptyTextLineList();
		this.createMultisetsForEachTextLine();
		return textLineList;
	}

	private TextLineList createInitialEmptyTextLineList() {
		getOrCreateTextLineList();
		IntRangeAnalyzer intRangeAnalyzer = new IntRangeAnalyzer(this);
		
		intRangeAnalyzer.createMajorNonOverlappingYRanges();
		for (IntRange yRange : intRangeAnalyzer.getYRangeList()) {
			TextLine textLine = new TextLine(yRange);
			textLineList.add(textLine);
		}
		return textLineList;
	}

	private void getOrCreateTextLineList() {
		if (textLineList == null) {
			textLineList = new TextLineList();
		}
	}

	public Multiset<IntRange> getYRangeMultiset() {
		return yRangeMultiset;
	}

	public void setYRangeMultiset(Multiset<IntRange> yRangeMultiset) {
		this.yRangeMultiset = yRangeMultiset;
	}

	public void addCharBoxes(List<CharBox> gocrCharBoxList) {
		for (CharBox charBox : gocrCharBoxList) {
			IntRange yRange = charBox.getYRange();
			TextLine textLine0 = null;
			for (TextLine textLine : textLineList) {
				if (textLine.getYRange().includes(yRange)) {
					textLine.addCharBox(charBox);
					textLine0 = textLine;
					break;
				}
			}
			if (textLine0 == null) {
				LOG.trace("doesn't fit line: cannot add char:  "+charBox);
			}
		}
		
	}
	
	@Override
	public String toString() {
		getOrCreateTextLineList();
		StringBuilder sb = new StringBuilder();
		for (TextLine textLine : textLineList) {
			sb.append(textLine.toString()+"\n");
		}
		return sb.toString();
	}

	public String toText() {
		getOrCreateTextLineList();
		StringBuilder sb = new StringBuilder();
		for (TextLine textLine : textLineList) {
			sb.append(textLine.createPhrase()+"\n");
		}
		return sb.toString();
	}

	public void disambiguateCharacters() {
		if (disambiguate) {
			disambiguateOZero();
			disambiguate1li();
			disambiguatea8();
			disambiguateHyphenDash();
			disambiguateQuotesCommas();
		}
	}

	private void disambiguateQuotesCommas() {
		if (disambiguate) {
			displayOccurrences("'");
			displayOccurrences("\"");
			displayOccurrences(",");
			displayOccurrences(";");
		}
	}

	private void disambiguateHyphenDash() {
		if (disambiguate) {
			displayOccurrences("-");
			displayOccurrences("_");
		}
	}

	private void displayOccurrences(String character) {
		LOG.debug("disambiguate: "+character+" "+getMultisetSortedByCount(character));
	}

	private void disambiguate1li() {
		displayOccurrences("]");
		displayOccurrences("[");
		displayOccurrences("l");
		displayOccurrences("i");
		displayOccurrences("I");
		displayOccurrences("1");
		displayOccurrences("t");
		displayOccurrences("|");
		
	}
	private void disambiguatea8() {
		displayOccurrences("a");
		displayOccurrences("8");
		
	}
	private void disambiguatecCG() {
		displayOccurrences("c");
		displayOccurrences("C");
		displayOccurrences("G");
		
	}

	private void disambiguateegi() {
		displayOccurrences("e");
		displayOccurrences("g");
		displayOccurrences("i");
		
	}

	private void disambiguateOZero() {
		displayOccurrences("O");
		displayOccurrences("o");
		displayOccurrences("0");
		displayOccurrences("a");
	}

	private void disambiguate2Z() {
		displayOccurrences("2");
		displayOccurrences("Z");
	}

	private List<Entry<Int2>> getMultisetSortedByCount(String character) {
		CharBoxList bigOCharBoxList = new CharBoxList(charBoxByText.get(character));
		Multiset<Int2> bigOSet = bigOCharBoxList.getBBoxSizeMultiset();
		List<Entry<Int2>> bigOList = MultisetUtil.createListSortedByCount(bigOSet);
		return bigOList;
	}

	Multiset<IntRange> createYRangeMultiset(CharBoxList charBoxList) {
		this.bboxSizeByText = ArrayListMultimap.create();
		this.charBoxByText = ArrayListMultimap.create();
		this.charBoxByBBox = new HashMap<>();
		Multiset<IntRange> yRangeMultiset = HashMultiset.create();
		for (CharBox gocrCharBox : charBoxList) {
			String text = gocrCharBox.getSvgText().getText();
			text = text == null ? null : text.substring(0,1);
			this.charBoxByText.put(text, gocrCharBox);
			this.bboxSizeByText.put(text, gocrCharBox.getBoundingBoxSize());
			this.charBoxByBBox.put(gocrCharBox.getBoundingBox(), gocrCharBox);
			IntRange yrange = gocrCharBox.getYRange();
			yRangeMultiset.add(yrange);
		}
		return yRangeMultiset;
	}

	public void analyzeCharBoxes() {
		for (String text : charBoxByText.keys()) {
			List<CharBox> charBoxList = new ArrayList<CharBox>(charBoxByText.get(text));
			LOG.debug(text + ":" +charBoxList);
		}
	}
	
	public Multimap<String, Int2> getBboxSizeByText() {
		return bboxSizeByText;
	}

	public Multimap<String, CharBox> getCharBoxByText() {
		return charBoxByText;
	}

	public Map<Int2Range, CharBox> getCharBoxByBBox() {
		return charBoxByBBox;
	}

	public HtmlTable makeTable(int start) {
		return makeTable(start, textLineList.size());
	}

	public HtmlTable makeTable(int start, int end) {
		HtmlTable table = null;
		TextLine lastTextLine = null;
		for (int i = start; i < end; i++) {
			TextLine textLine = textLineList.get(i);
			Integer baseY = textLine.getBaseY();
			Integer baseY2 = lastTextLine == null ? null : lastTextLine.getBaseY();
			int delta = baseY != null && baseY2 != null ?
					baseY - baseY2 : 0;
			List<String> phrases = textLine.getPhrases();
//			LOG.debug(textLine.getBaseY()+" "+delta+" "+phrases.size()+ " "+phrases);
			lastTextLine = textLine;
		}
		return table;
	}

	public void outputText(File gocrTextFile) {
		String s = this.toText();
		try {
			FileUtils.write(gocrTextFile, s, CMineUtil.UTF8_CHARSET);
		} catch (IOException e) {
			throw new RuntimeException("Cannot write gocr output", e);
		}
	}

	public boolean isDisambiguate() {
		return disambiguate;
	}

	public void setDisambiguate(boolean disambiguate) {
		this.disambiguate = disambiguate;
	}

	public AMIOCRTool getAmiocrTool() {
		return amiocrTool;
	}

	public void setAmiocrTool(AMIOCRTool amiocrTool) {
		this.amiocrTool = amiocrTool;
	}


	

}
