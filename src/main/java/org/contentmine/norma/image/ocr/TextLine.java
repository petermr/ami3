package org.contentmine.norma.image.ocr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.svg.text.SVGPhrase;
import org.contentmine.graphics.svg.text.SVGWord;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** holds a horizontal line of characters with coordinates and bboxes
 * probably a holding area for analysis
 * 
 * @author pm286
 *
 */
public class TextLine {
	static final Logger LOG = LogManager.getLogger(TextLine.class);
private IntRange yRange;
	private List<Entry<IntRange>> yRangeMultisetEntryList;

	private CharBoxList charBoxList;
	private IntArray widthArray;
	private Integer averageWidth;
	private double spaceRelativeToAverageWidth = 0.7;
	private double tabRelativeToAverageWidth   = 1.7;
	private String sentence;
	private List<SVGPhrase> svgPhrases;
	private List<SVGWord> svgWords;
	private List<String> phrases;
	private List<String> words;

	private FontGeometry fontGeometry;
	
	private TextLine() {
		
	}

	public TextLine(IntRange yRange) {
		this();
		this.yRange = yRange;
	}

	public boolean intersectsWith(IntRange yRange) {
		return this.yRange != null && yRange != null && this.yRange.intersectsWith(yRange);
	}

	public int getMin() {
		return yRange.getMin();
	}

	public int getRange() {
		return yRange.getRange();
	}

	public TextLine plus(IntRange yRange) {
		this.yRange.plus(yRange);
		return this;
	}

	public boolean intersectsWith(TextLine line) {
		return this.yRange.intersectsWith(line.getYRange());
	}

	public IntRange getYRange() {
		return yRange;
	}

	public TextLine plus(TextLine line1) {
		return new TextLine(this.yRange.plus(line1.getYRange()));
	}

	public void addYRangeEntry(Entry<IntRange> yRangeEntry) {
		getOrCreateYRangeMultisetEntryList();
		yRangeMultisetEntryList.add(yRangeEntry);
	}

    private void getOrCreateYRangeMultisetEntryList() {
    	if (yRangeMultisetEntryList == null) {
    		yRangeMultisetEntryList = new ArrayList<>();
    	}
    }

	public List<Entry<IntRange>> getYRangeEntryList() {
		return yRangeMultisetEntryList;
	}
	
	public void addCharBox(CharBox charBox) {
		getOrCreateCharBoxList();
		this.charBoxList.add(charBox);
	}

	private void getOrCreateCharBoxList() {
		if (charBoxList == null) {
			charBoxList = new CharBoxList();
		}
	}

	public String createPhrase() {
		StringBuilder sb = new StringBuilder();
		this.getAverageWidth();
		for (int i = 0; i < charBoxList.size();i++) {
			CharBox charBox = charBoxList.get(i);
			String s = charBox.getBestCharacter();
			sb.append(s);
			if (i < charBoxList.size() - 1) {
				CharBox charBox1 = charBoxList.get(i + 1);
				int space = charBox1.getXMin() - charBox.getXMax();
				if (space > averageWidth * tabRelativeToAverageWidth) {
					sb.append(" \t ");
				} else if (space > averageWidth * spaceRelativeToAverageWidth) {
					sb.append(" ");
				}
			}
		}
		sentence = sb.toString();
		phrases = Arrays.asList(sentence.split(" \t "));
		words = Arrays.asList(sentence.split("\\s+"));
		return sentence;
	}
	public Integer getAverageWidth() {
		if (averageWidth == null) {
			this.getOrCreateWidthArray();
			averageWidth = widthArray.getMean();
		}
		return averageWidth;
	}

	public IntArray getOrCreateWidthArray() {
		if (widthArray == null) {
			getOrCreateCharBoxList();
			widthArray = new IntArray();
			for (CharBox charBox : charBoxList) {
				Integer width = charBox.getWidth();
				widthArray.addElement((int)width);
			}
		}
		return widthArray;
	}

	@Override
	public String toString() {
		getOrCreateCharBoxList();
		StringBuilder sb = new StringBuilder();
		sb.append(this.createPhrase() + " ~ ");
		sb.append(charBoxList.toString());
		return sb.toString();
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public List<String> getPhrases() {
		return phrases;
	}

	public void setPhrases(List<String> phrases) {
		this.phrases = phrases;
	}

	public List<String> getWords() {
		return words;
	}

	public void setWords(List<String> words) {
		this.words = words;
	}

	public List<Entry<IntRange>> getYRangeMultisetEntryList() {
		return yRangeMultisetEntryList;
	}

	public Integer getBaseY() {
		return fontGeometry == null ? null : fontGeometry.getBaseY();
	}

	public FontGeometry getOrCreateFontGeometry() {
		if (fontGeometry == null) {
			fontGeometry = new FontGeometry(this);
		}
		return fontGeometry;
	}

	

}
