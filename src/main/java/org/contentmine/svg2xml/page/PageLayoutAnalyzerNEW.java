package org.contentmine.svg2xml.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Univariate;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGShape;
import org.contentmine.graphics.svg.cache.ComponentCache;
import org.contentmine.graphics.svg.cache.TextChunkCache;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalElement;
import org.contentmine.graphics.svg.rule.horizontal.HorizontalRule;
import org.contentmine.graphics.svg.text.build.PhraseChunk;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.graphics.svg.text.build.TextChunkList;
import org.contentmine.graphics.svg.text.structure.TextStructurer;
import org.contentmine.svg2xml.table.TableGrid;
import org.contentmine.svg2xml.table.TableStructurer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/** a new approach (2017) to analyzing the structure of pages.
 * uses PhraseList extents and HorizontalRulers to estimate widths
 * 
 * Now (2017) uses Caches to process the SVG input in TableContentCreator.CreateContent()
 * 
 * @author pm286
 *
 */
public class PageLayoutAnalyzerNEW {

	private static final char CHAR_P = 'P';
	private static final char CHAR_L = 'L';
	private static final String LP = "LP";
	private static final String LP_1 = LP+"{1}";
	private static final String X = "X";
	private static final String T = "T";

	private static final Logger LOG = Logger.getLogger(PageLayoutAnalyzerNEW.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static Pattern TABLE_N = Pattern.compile("T[Aa][Bb][Ll][Ee]\\s+(\\d+)\\s+(\\(cont(inued)?\\.?\\))?(.{0,500})");


	protected TextStructurer textStructurer;
	protected TextChunk textChunk;
	protected TableStructurer tableStructurer;
	protected List<HorizontalRule> horizontalRuleList;
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
	protected SVGElement svgChunk;
	protected ComponentCache componentCache;

	public PageLayoutAnalyzerNEW() {
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
		this.inputFile = inputFile;
		svgChunk = SVGElement.readAndCreateSVG(inputFile);
		createContent(svgChunk);
	}

	/** uses the SVG classes and methods
	 * 
	 * @param svgElement
	 */
	public void createContent(AbstractCMElement svgElement) {
		componentCache = new ComponentCache();
		componentCache.readGraphicsComponentsAndMakeCaches(svgElement);
//		TextChunkCache textChunkCache = componentCache.getOrCreateTextChunkCache();
//		TextStructurer textStructurer = textChunkCache.getOrCreateTextStructurer();
//		TextChunkList textChunkList = textChunkCache.getOrCreateTextChunkList();
//		LOG.debug("components: "+componentCache.toString());
//		// should probably move TextStructure to TextChunkCache
//		textStructurer = TextStructurer.createTextStructurerWithSortedLines(svgElement);
//		SVGElement inputSVGChunk = textStructurer.getSVGChunk();
//		cleanChunk(inputSVGChunk);
		if (rotatable  && textStructurer.hasAntiClockwiseCharacters()) {
			throw new RuntimeException("refactored rot90");
//			inputSVGChunk = rotateClockwise(textStructurer);
//			TextStructurer textStructurer1 = TextStructurer.createTextStructurerWithSortedLines(inputSVGChunk);
//			textStructurer = textStructurer1;
		}

//		LOG.trace(">pll>"+phraseListList.size()+" ... "+phraseListList.toXML());
//		textStructurer.condenseSuscripts();
//		phraseListList.format(3);
		TextChunkCache textChunkCache = componentCache.getOrCreateTextChunkCache();
		// is this a good idea here?
		TextStructurer textStructurer = textChunkCache.getOrCreateTextStructurer();
		textChunk = textStructurer.getTextChunkList().getLastTextChunk();
		tableStructurer = PageLayoutAnalyzerNEW.createTableStructurer(textStructurer); // this deletes outer rect
		TableGrid tableGrid = tableStructurer.createGrid();
			
		if (tableGrid == null) {
			createOrderedHorizontalList();
			LOG.trace("hlist: " + PageLayoutAnalyzerNEW.createSig(horizontalList));
		}
		return;
	}
	
	/** rotates text clockwise to create new tables.
	 * probable obsolete here.
	 * static because of refactoring
	 * 
	 * @param textStructurer
	 * @return
	 */
	public static SVGElement rotateClockwise(TextStructurer textStructurer) {
		SVGG rotatedVerticalText = textStructurer.createChunkFromVerticalText(new Angle(-1.0 * Math.PI / 2));
		TableStructurer tableStructurer = TableStructurer.createTableStructurer(textStructurer);
		SVGElement chunk = textStructurer.getSVGChunk();
		Angle angle = new Angle(-1.0 * Math.PI / 2);
		List<SVGShape> shapeList = tableStructurer.getOrCreateShapeList();
		SVGElement.rotateAndAlsoUpdateTransforms(shapeList, chunk.getCentreForClockwise90Rotation(), angle);
		chunk.removeChildren();
		XMLUtil.transferChildren(rotatedVerticalText, chunk);
		for (SVGElement shape : shapeList) {
			shape.detach();
			chunk.appendChild(shape);
		}
		return chunk;
	}


	private static TableStructurer createTableStructurer(TextStructurer textStructurer) {
		TextChunkList textChunkList = textStructurer.getOrCreateTextChunkListFromWords();
		TableStructurer tableStructurer = new TableStructurer(textChunkList.getLastTextChunk());
		tableStructurer.setTextStructurer(textStructurer);
		tableStructurer.analyzeShapeList();
		return tableStructurer;
	}

	public static String createSig(List<HorizontalElement> horizontalList) {
		StringBuilder sb;
		String sig = createLPList(horizontalList);
		LOG.trace(">>"+sig);
		String lpc = createLPCountList(sig);
		LOG.trace(">>>"+lpc);
		String lpccond = contractLP1(lpc);
		LOG.trace(">>>>"+lpccond);
		return lpccond;
	}

	private static String createLPList(List<HorizontalElement> horizontalList) {
		StringBuilder sb = new StringBuilder();
		for (HorizontalElement helem :horizontalList) {
			if (helem instanceof HorizontalRule) {
				sb.append("L");
			} else if (helem instanceof PhraseChunk) {
				sb.append("P");
			} else {
				sb.append(helem.getClass().getSimpleName());
			}
		}
		String sig = sb.toString();
		return sig;
	}

	private static String createLPCountList(String sig) {
		StringBuilder sb;
		sb = new StringBuilder();
		int pcount = 0;
		for (int i = 0; i < sig.length(); i++) {
			char c = sig.charAt(i);
			if (c == CHAR_P) {
				pcount++;
			} else if (c == CHAR_L) {
				if (pcount > 0) {
					sb.append(CHAR_P+"{"+pcount+"}");
				}
				pcount = 0;
				sb.append("L");
			}
		}
		if (pcount > 0) {
			sb.append(CHAR_P+"{"+pcount+"}");
		}
		return sb.toString();
	}

	private static String contractLP1(String sig) {
		String cond = sig.replace(LP_1, X);
		StringBuilder sb = new StringBuilder();
		int xcount = 0;
		int i = 0;
		while (i < cond.length()) {
			String s = cond.substring(i, i+1);
			if (X.equals(s)) {
				if (xcount == 1) {
					sb.append(X);
				}
				xcount++;
			} else {
				if (xcount > 1) {
					sb.append(T+"{"+(xcount - 1) +"}");
					xcount = 0;
				}
				sb.append(s);
			}
			i++;
		}
		if (xcount > 0) {
			sb.append(T+"{"+xcount+"}");
		}
		String s = sb.toString();
		s = s.replace(X, LP);
		s = s.replace(T+"{1}", LP);
		return s;
	}


	private List<HorizontalElement> createOrderedHorizontalList() {
		Stack<PhraseChunk> phraseListStack = new Stack<PhraseChunk>();
		for (PhraseChunk phraseList : textChunk) {
			phraseListStack.push(phraseList);
		}
		Stack<HorizontalRule> horizontalRulerListStack = new Stack<HorizontalRule>();
		horizontalRuleList = tableStructurer.getHorizontalRulerList(true, 1.0);
		for (HorizontalRule ruler : horizontalRuleList) {
			horizontalRulerListStack.push(ruler);
		}
		addStacksToHorizontalListInYOrder(phraseListStack, horizontalRulerListStack);
		return horizontalList;
	}

	private void addStacksToHorizontalListInYOrder(Stack<PhraseChunk> phraseListStack, Stack<HorizontalRule> horizontalRulerListStack) {
		horizontalList = new ArrayList<HorizontalElement>();
		PhraseChunk currentPhraseList = null;
		HorizontalRule currentRuler = null;
		while (!phraseListStack.isEmpty() || !horizontalRulerListStack.isEmpty() ||
				currentPhraseList != null || currentRuler != null) {
			if (!phraseListStack.isEmpty() && currentPhraseList == null) {
				currentPhraseList = phraseListStack.pop();
			}
			if (!horizontalRulerListStack.isEmpty() && currentRuler == null) {
				currentRuler = horizontalRulerListStack.pop();
			}
			if (currentRuler != null && currentPhraseList != null) {
				Double rulerY = currentRuler.getY();
				Double phraseListY = currentPhraseList.getXY().getY();
				if (rulerY < phraseListY) {
					addPhraseList(currentPhraseList);
					currentPhraseList = null;
				} else {
					addRuler(currentRuler);
					currentRuler = null;
				}
			} else if (currentPhraseList != null) {
				addPhraseList(currentPhraseList);
				currentPhraseList = null;
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

	private void addPhraseList(PhraseChunk currentPhraseList) {
		horizontalList.add(currentPhraseList);
		LOG.trace("phrase: "+currentPhraseList.getStringValue()+"/"+currentPhraseList.getY());
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

	public List<HorizontalRule> getHorizontalRulerList() {
		return horizontalRuleList;
	}


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

	public TextStructurer getOrCreateTextStructurer() {
		if (textStructurer == null && componentCache != null) {
			textStructurer = componentCache.getOrCreateTextChunkCache().getOrCreateTextStructurer();
			// this is tacky
			textStructurer.setSVGChunk(svgChunk);
		}
		return textStructurer;
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
