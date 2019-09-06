package org.contentmine.svg2xml.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Univariate;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalElement;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalRule;
import org.contentmine.graphics.svg.text.build.PhraseChunk;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.svg2xml.table.TableGrid;
import org.contentmine.svg2xml.table.TableStructurer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** a new approach (2017) to analyzing the structure of pages.
 * uses PhraseChunk extents and HorizontalRules to estimate widths
 * 
 * @author pm286
 *
 */
public class PageLayoutAnalyzer {

	private static final Logger LOG = Logger.getLogger(PageLayoutAnalyzer.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static Pattern TABLE_N = Pattern.compile("T[Aa][Bb][Ll][Ee]\\s+(\\d+)\\s+(\\(cont(inued)?\\.?\\))?(.{0,500})");


	protected TableTextStructurer tableTextStructurer;
	protected TextChunk textChunk;
	protected TableStructurer tableStructurer;
	protected List<HorizontalRule> horizontalRulerList;
	public List<HorizontalElement> horizontalList;
	
	private Multiset<IntRange> xRangeSet = HashMultiset.create();
	private Multiset<Integer> xRangeStartSet;
	private Multiset<Integer> xRangeEndSet;
	private boolean includeRulers;
	private boolean includePhrases;
	private int xRangeRangeMin; // to exclude ticks on diagrams
	private File inputFile;
	private boolean rotatable = false;


	private boolean omitWhitespace = true;

	public PageLayoutAnalyzer() {
		setDefaults();
	}

	private void setDefaults() {
		xRangeRangeMin = 50;
		ensureXRangeSets();
		omitWhitespace = true;
	}

	private void ensureXRangeSets() {
		xRangeSet = HashMultiset.create();
		xRangeStartSet = HashMultiset.create();
		xRangeEndSet = HashMultiset.create();
	}
	
	public void createContent(File inputFile) {
		LOG.trace(inputFile.getAbsolutePath());
		this.inputFile = inputFile;
//		textStructurer = TextStructurer.createTextStructurerWithSortedLines(inputFile);
		tableTextStructurer = TableTextStructurer.createTableTextStructurerWithSortedLines(inputFile);
		AbstractCMElement inputSVGChunk = tableTextStructurer.getSVGChunk();
		cleanChunk(inputSVGChunk);
		if (rotatable  && tableTextStructurer.hasAntiClockwiseCharacters()) {
			throw new RuntimeException("rotated tabkes NYI");
//			SVGSVG.wrapAndWriteAsSVG(inputSVGChunk, new File("target/debug/preRot.svg"));
//			inputSVGChunk = tableTextStructurer.rotateClockwise();
//			SVGSVG.wrapAndWriteAsSVG(inputSVGChunk, new File("target/debug/postRot.svg"));
//			TextStructurer textStructurer1 = TableTextStructurer.createTextStructurerWithSortedLines(inputSVGChunk);
//			tableTextStructurer = textStructurer1;
		}

//		phraseListList = tableTextStructurer.getPhraseListList();
		// this has been refactored - I think it is nearly right
		textChunk = tableTextStructurer.getTextChunkList().get(0);
		LOG.trace("reading ... "+textChunk.toXML());
		tableTextStructurer.condenseSuscripts();
		textChunk.format(3);
		tableStructurer = tableTextStructurer.createTableStructurer();
		TableGrid tableGrid = tableStructurer.createGrid();
			
		if (tableGrid == null) {
			createOrderedHorizontalList();
		}
	}

	private void cleanChunk(AbstractCMElement chunk) {
		if (omitWhitespace) {
			detachWhitespaceTexts(chunk);
		}
	}

	private void detachWhitespaceTexts(AbstractCMElement chunk) {
		List<SVGText> spaceList = SVGText.extractSelfAndDescendantTexts(chunk);
		for (SVGText text : spaceList) {
			String textS = text.getText();
			if (textS == null || textS.trim().length() == 0) {
				text.detach();
			}
		}
	}

	private List<HorizontalElement> createOrderedHorizontalList() {
		Stack<PhraseChunk> phraseListStack = new Stack<PhraseChunk>();
		for (PhraseChunk phraseList : textChunk) {
			phraseListStack.push(phraseList);
		}
		Stack<HorizontalRule> horizontalRulerListStack = new Stack<HorizontalRule>();
		horizontalRulerList = tableStructurer.getHorizontalRulerList(true, 1.0);
		for (HorizontalRule ruler : horizontalRulerList) {
			horizontalRulerListStack.push(ruler);
		}
		addStacksToHorizontalListInYOrder(phraseListStack, horizontalRulerListStack);
		return horizontalList;
	}

	private void addStacksToHorizontalListInYOrder(Stack<PhraseChunk> phraseListStack, Stack<HorizontalRule> horizontalRulerListStack) {
		horizontalList = new ArrayList<HorizontalElement>();
		PhraseChunk currentPhraseChunk = null;
		HorizontalRule currentRuler = null;
		while (!phraseListStack.isEmpty() || !horizontalRulerListStack.isEmpty() ||
				currentPhraseChunk != null || currentRuler != null) {
			if (!phraseListStack.isEmpty() && currentPhraseChunk == null) {
				currentPhraseChunk = phraseListStack.pop();
			}
			if (!horizontalRulerListStack.isEmpty() && currentRuler == null) {
				currentRuler = horizontalRulerListStack.pop();
			}
			if (currentRuler != null && currentPhraseChunk != null) {
				Double rulerY = currentRuler.getY();
				Double phraseListY = currentPhraseChunk.getXY().getY();
				if (rulerY < phraseListY) {
					addPhraseChunk(currentPhraseChunk);
					currentPhraseChunk = null;
				} else {
					addRuler(currentRuler);
					currentRuler = null;
				}
			} else if (currentPhraseChunk != null) {
				addPhraseChunk(currentPhraseChunk);
				currentPhraseChunk = null;
			} else if (currentRuler != null) {
				addRuler(currentRuler);
				currentRuler = null;
			} else {
				LOG.trace("stacks empty");
			}
		}
		Collections.reverse(horizontalList);
		for (HorizontalElement horizontalElement : horizontalList) {
			LOG.trace("============"+horizontalElement.getClass()+"\n"+horizontalElement.toString());
		}
	}

	private void addRuler(HorizontalRule currentRuler) {
		horizontalList.add((HorizontalElement)currentRuler);
		LOG.trace("phrase: "+currentRuler.getStringValue()+"/"+currentRuler.getY());
	}

	private void addPhraseChunk(PhraseChunk currentPhraseChunk) {
		horizontalList.add(currentPhraseChunk);
		LOG.trace("phrase: "+currentPhraseChunk.getStringValue()+"/"+currentPhraseChunk.getY());
	}

	public List<HorizontalElement> getHorizontalList() {
		return horizontalList;
	}


	public void analyzeXRangeExtents(File inputFile) {
		createContent(inputFile);
		List<HorizontalElement> horizontalElementList = getHorizontalList();
		
		for (HorizontalElement horizontalElement : horizontalElementList) {
			IntRange xRange = new IntRange(((SVGElement)horizontalElement).getBoundingBox().getXRange().format(0));
			int round = 1;
			xRangeStartSet.add(xRange.getMin() / round * round);
			xRangeEndSet.add(xRange.getMax() / round * round);
			if (includeRulers && horizontalElement instanceof HorizontalRule ||
				includePhrases && horizontalElement instanceof PhraseChunk) {
				if (xRange.getRange() >= xRangeRangeMin) {
					xRangeSet.add(xRange);
				}
			}
		}
	}

	public List<HorizontalRule> getHorizontalRuleList() {
		return horizontalRulerList;
	}

//	private void ensureContent() {
//		if (tableStructurer == null && inputFile != null) {
//			createContent(inputFile);
//		}
//	}

	public void setIncludeRulers(boolean b) {
		this.includeRulers = b;
	}

	public void setIncludePhrases(boolean b) {
		this.includePhrases = b;
	}

	public Multiset<IntRange> getXRangeSet() {
		return xRangeSet;
	}

	public Multiset<Integer> getXRangeEndSet() {
		return xRangeEndSet;
	}

	public Multiset<Integer> getXRangeStartSet() {
		return xRangeStartSet;
	}

	public void setXRangeRangeMin(int rangeMin) {
		this.xRangeRangeMin = rangeMin;
	}

	public RealArray getXRangeStartArray() {
		return getRealArray(xRangeStartSet);
	}
	
	public Univariate getXStartUnivariate() {
		return new Univariate(getXRangeStartArray());
	}

	public RealArray getXRangeEndArray() {
		return getRealArray(xRangeEndSet);
	}

	public Univariate getXEndUnivariate() {
		return new Univariate(getXRangeEndArray());
	}


	private RealArray getRealArray(Multiset<Integer> xSet) {
		RealArray xArray = new RealArray();
		for (Multiset.Entry<Integer> entry : xSet.entrySet()) {
			for (int i = 0; i < entry.getCount(); i++) {
				xArray.addElement((double)entry.getElement());
			}
		}
		return xArray;
	}

	public TableTextStructurer getTableTextStructurer() {
		return tableTextStructurer;
	}

	public TableStructurer getTableStructurer() {
		return tableStructurer;
	}
	
	public boolean isRotatable() {
		return rotatable;
	}

	public void setRotatable(boolean rotatable) {
		this.rotatable = rotatable;
	}



}
