package org.contentmine.graphics.svg.text.structure;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.text.line.ScriptLine;
import org.contentmine.graphics.svg.text.line.TextLine;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import nu.xom.Element;
import nu.xom.Nodes;
import nu.xom.ParentNode;
import nu.xom.Text;

/** 
 * moved from svg2xml
 * 
 * @author pm286
 *
 */
public class ScriptContainer extends AbstractContainer implements Iterable<ScriptLine> {

	public enum Side {
		LEFT,
		RIGHT,
	};

	public final static Logger LOG = Logger.getLogger(ScriptContainer.class);

	private static final double FONT_EPS = 0.01;
	private static final String SOFT_HYPHEN = "~";
	private static final PrintStream SYSOUT = System.out;

	private Multiset<String> fontFamilySet;
	private Multiset<Double> fontSizeSet;
	private List<ScriptLine> scriptLineList;
	Multiset<Double> leftIndentSet;

	private Double leftIndent0;
	private Double leftIndent1;

	private TextStructurer textStructurer;

	public ScriptContainer() {
		super((PageAnalyzer) null);
	}
	public ScriptContainer(PageAnalyzer pageAnalyzer) {
		super(pageAnalyzer);
	}

	public ScriptContainer(List<ScriptLine> scriptedLineList) {
		this();
		this.add(scriptedLineList);
	}
	
	public static ScriptContainer createScriptContainer(TextStructurer textStructurer, PageAnalyzer pageAnalyzer) {
		List<TextLine> textLineList = textStructurer.getTextLineList();
		for (TextLine textLine : textLineList) {
			LOG.trace("TLSC "+textLine);
		}
		ScriptContainer scriptContainer = new ScriptContainer(pageAnalyzer);
		List<ScriptLine> scriptedLineList = textStructurer.getScriptedLineListForCommonestFont();
		for (ScriptLine scriptLine : scriptedLineList) {
			LOG.trace("SCL "+scriptLine);
		}
		scriptContainer.setTextStructurer(textStructurer);
		scriptContainer.add(scriptedLineList);
		return scriptContainer;
	}
	
	private void setTextStructurer(TextStructurer textStructurer) {
		this.textStructurer = textStructurer;
	}

	@Override
	public HtmlElement createHtmlElement() {
		return new HtmlP("OBSOLETE do not use createHtmlElement()");
//		if (htmlElement == null) {
//			htmlElement = new HtmlDiv();
//			Real2Range boundingBox = null;
//			if (svgChunk != null) {
//				htmlElement.setId(svgChunk.getId());
//				boundingBox = svgChunk.getBoundingBox();
//			}
//			RealRange xRange = (boundingBox == null ? null : boundingBox.getXRange());
//			ScriptLine lastLine = null;
//			for (ScriptLine scriptLine : scriptLineList) {
//				if (scriptLine != null) {
//					possiblyAddParaBeforeLine(xRange, lastLine, scriptLine);
//					StyleSpans styleSpans = scriptLine.getStyleSpans();
//					addIndividualSpans(styleSpans);
//					possiblyAddParaAfterLine(xRange, scriptLine);
//					lastLine = scriptLine;
//				}
//			}
//			cleanSpaceSpans(htmlElement);
//			cleanEmptySpans(htmlElement);
//			cleanMultipleSpaces(htmlElement);
//		}
//		return htmlElement;
	}

	private void possiblyAddParaBeforeLine(RealRange xRange, ScriptLine lastLine,
			ScriptLine scriptLine) {
		boolean addPara = false;
		if (scriptLine.indentCouldStartParagraph(xRange)) {
			addPara = true;
		}
		if (!addPara && scriptLine.startsWithBoldSpan()) {
			LOG.trace("BOLD: "+scriptLine +" // "+lastLine);
			if (lastLine == null || !lastLine.endsWithBoldSpan()) {
				addPara = true;	
			}
		}
		if (addPara) {
			htmlElement.appendChild(new HtmlP());
		}
	}
	
	private void possiblyAddParaAfterLine(RealRange xRange, ScriptLine scriptLine) {
		if (scriptLine.couldEndParagraph(xRange)) {
			htmlElement.appendChild(new HtmlP());
		}
	}

//	private void addIndividualSpans(StyleSpans styleSpans) {
//		for (int j = 0; j < styleSpans.size(); j++) {
//			StyleSpan styleSpan = styleSpans.get(j);
//			HtmlElement htmlElement1 = styleSpan.createHtmlElement();
//			addJoiningSpace(htmlElement1);
//			PageIO.copyChildElementsFromTo(htmlElement1, htmlElement);
//		}
//	}

	/** remove any spans with just whitespace
	 * 
	 * @param htmlElement
	 */
	private void cleanSpaceSpans(HtmlElement htmlElement) {
		Nodes spans = htmlElement.query(".//*[local-name()='span' and count(*) = 0 and text()[normalize-space(.)='']]");
		for (int i = 0; i < spans.size(); i++) {
			Element span = (Element) spans.get(i);
			String value = span.getValue();
			ParentNode parent = span.getParent();
			parent.replaceChild(span, new Text(value));
		}
	}

	/** remove any spans with just whitespace
	 * 
	 * @param htmlElement
	 */
	private void cleanMultipleSpaces(HtmlElement htmlElement) {
		Nodes spans = htmlElement.query(".//text()[normalize-space(.)='' and string-length(.) > 1]");
		for (int i = 0; i < spans.size(); i++) {
			Text text = (Text) spans.get(i);
			text.setValue(" ");
		}
	}

	/** remove any spans with just whitespace
	 * 
	 * @param htmlElement
	 */
	private void cleanEmptySpans(HtmlElement htmlElement) {
		Nodes spans = htmlElement.query(".//*[local-name()='span' and count(node()) = 0]");
		for (int i = 0; i < spans.size(); i++) {
			Element span = (Element) spans.get(i);
			span.detach();
		}
	}

	private void addJoiningSpace(HtmlElement htmlElement) {
		String value = htmlElement.getValue();
		HtmlElement spaceElement = new HtmlSpan();
		if (/*!(value.endsWith(".")) && */ !(value.endsWith("-"))) {
			spaceElement.setValue(" ");
		} else {
			addSoftHyphen(spaceElement);
			LOG.trace("no space: "+value);
		}
		htmlElement.appendChild(spaceElement);
	}

	private void addSoftHyphen(HtmlElement spaceElement) {
		Nodes texts = spaceElement.query(".//text()");
		if (texts.size() > 0 ) {
			Text lastText = (Text) texts.get(texts.size() - 1);
			String textValue = lastText.getValue();
			textValue += SOFT_HYPHEN;
			lastText.setValue(textValue);
			LOG.trace(".. "+lastText);
		}
	}


	public List<ScriptLine> getScriptLineList() {
		return scriptLineList;
	}

	public void add(ScriptLine scriptLine) {
		ensureScriptList();
		scriptLineList.add(scriptLine);
	}

	private void ensureScriptList() {
		if (scriptLineList == null) {
			this.scriptLineList = new ArrayList<ScriptLine>();
		}
	}

	public AbstractCMElement createSVGGChunk() {
		AbstractCMElement g = new SVGG();
		for (ScriptLine scriptLine : scriptLineList) {
			if (scriptLine != null) {
				List<SVGText> textList = scriptLine.getTextList();
				for (SVGElement text : textList) {
					g.appendChild(new SVGText(text));
				}
			}
		}
		return g;
	}

	public void add(List<ScriptLine> scriptLineList) {
		ensureScriptList();
		this.scriptLineList.addAll(scriptLineList);
	}

	public Double getSingleFontSize() {
		Double fontSize = null;
		for (ScriptLine scriptLine : scriptLineList) {
			if (scriptLine == null) continue;
			Double size = scriptLine.getFontSize();
			if (fontSize == null) {
				fontSize = size;
			} else {
				if (fontSize == null || size == null || !Real.isEqual(fontSize, size, FONT_EPS)) {
					return null;
				}
			}
		}
		return fontSize;
	}

	public Double getLargestFontSize() {
		Double fontSize = null;
		for (ScriptLine script : scriptLineList) {
			Double size = script.getFontSize();
			if (fontSize == null) {
				fontSize = size;
			} else {
				if (size > fontSize) {
					fontSize = size;
				}
			}
		}
		return fontSize;
	}

	public String getSingleFontFamily() {
		String fontFamily = null;
		for (ScriptLine script : scriptLineList) {
			String family = script.getFontFamily();
			if (fontFamily == null) {
				fontFamily = family;
			} else {
				if (!fontFamily.equals(family)) {
					return null;
				}
			}
		}
		return fontFamily;
	}

	/** creates a multiset from addAll() on multisets for each line
	 *  
	 * @return
	 */
	public Multiset<String> getFontFamilyMultiset() {
		if (fontFamilySet == null) {
			fontFamilySet = HashMultiset.create();
			for (ScriptLine script : scriptLineList) {
				String family = script.getFontFamily();
				fontFamilySet.add(family);
			}
		}
		return fontFamilySet;
	}

	/** 
	 *  
	 * @return
	 */
	public Multiset<Double> getFontSizeMultiset() {
		if (fontSizeSet == null) {
			fontSizeSet = HashMultiset.create();
			for (ScriptLine script : scriptLineList) {
				Double fontSize = script.getFontSize();
				if (fontSize != null) {
					fontSizeSet.add(fontSize);
				}
			}
		}
		return fontSizeSet;
	}

	/** gets commonest font
	 *
	 * @return
	 */
	public String getCommonestFontFamily() {
		getFontFamilyMultiset();
		String commonestFontFamily = null;
		int highestCount = -1;
		Set<String> fontFamilyElementSet = fontFamilySet.elementSet();
		for (String fontFamily : fontFamilyElementSet) {
			int count = fontFamilySet.count(fontFamily);
			if (count > highestCount) {
				highestCount = count;
				commonestFontFamily = fontFamily;
			}
		}
		return commonestFontFamily;
	}

	/** gets commonest font
	 *
	 * @return
	 */
	public Double getCommonestFontSize() {
		getFontSizeMultiset();
		Double commonestFontSize = null;
		int highestCount = -1;
		Set<Double> fontSizeElementSet = fontSizeSet.elementSet();
		for (Double fontSize : fontSizeElementSet) {
			int count = fontSizeSet.count(fontSize);
			if (count > highestCount) {
				highestCount = count;
				commonestFontSize = fontSize;
			}
		}
		return commonestFontSize;
	}


	public Boolean isBold() {
		Boolean fontWeight = null;
		for (ScriptLine script : scriptLineList) {
			if (script == null) continue;
			boolean weight = script.isBold();
			if (fontWeight == null) {
				fontWeight = weight;
			} else {
				if (!fontWeight.equals(weight)) {
					return null;
				}
			}
		}
		return fontWeight;
	}

	@Override 
	public String summaryString() {
		StringBuilder sb = new StringBuilder(">>>Script>>>"+" lines: "+scriptLineList.size()+"\n");
		for (ScriptLine script : scriptLineList) {
			if (script != null) {
				sb.append(script.summaryString()+"");
			}
		}
		sb.append("<<<Script<<<");
		String s = sb.toString();
		return s;
	}
	
	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()+" lines: "+scriptLineList.size()+"\n");
		for (ScriptLine script : scriptLineList) {
			if (script != null) {
				sb.append(script.toString());
			}
		}
		String s = sb.toString();
		return s;
	}

//	public void addToBoldIndex(Double fontSize) {
//		throw new RuntimeException("index "+fontSize);
//	}
//
//	public void addToIndexes(PDFIndex pdfIndex) {
//		indexBoldTextByFontSize(pdfIndex);
//		indexByTextContent(pdfIndex);
//	}
//
//	private void indexByTextContent(PDFIndex pdfIndex) {
//		String content = getTextContentWithSpaces();
//		pdfIndex.indexByTextContent(content, this.getChunkId());
//	}

//	public ChunkId getChunkId() {
//		super.getChunkId();
//		if (this.chunkId == null) {
//			this.chunkId = textStructurer == null ? null : textStructurer.getChunkId();
////			if (chunkId == null) {
////				chunkId = pageAnalyzer.getChunkId();
////			}
//		} 
//		return this.chunkId;
//	}

	public String getTextContentWithSpaces() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (ScriptLine scriptLine : scriptLineList) {
			String s = scriptLine.getTextContentWithSpaces();
			if (i++ > 0) sb.append(" ");
			sb.append(s);
		}
		return sb.toString();
	}

//	private void indexBoldTextByFontSize(PDFIndex pdfIndex) {
//		Double fontSize = getSingleFontSize();
//		Boolean isBold = isBold();
//		if (isBold != null && isBold) {
//			pdfIndex.addToBoldIndex(fontSize, this);
//		}
//	}
	
	@Override
	public String getRawValue() {
		StringBuilder sb = new StringBuilder();
		for (ScriptLine script : scriptLineList) {
			if (script != null) {
				sb.append(script.getRawValue());
			}
		}
		return sb.toString();
	}

	public Iterator<ScriptLine> iterator() {
		return scriptLineList.iterator();
	}

	void createLeftIndent01() {
		setLeftIndent0(null);
		setLeftIndent1(null);
		for (Double d : leftIndentSet.elementSet()) {
			if (getLeftIndent0() == null) {
				setLeftIndent0(d);
			} else {
				if (d < getLeftIndent0()) {
					setLeftIndent1(getLeftIndent0());
					setLeftIndent0(d);
				} else {
					setLeftIndent1(d);
				}
			}
		}
	}


	Multiset<Double> createLeftIndentSet(int decimalPlaces) {
		if (leftIndentSet == null) {
			leftIndentSet = HashMultiset.create();
			for (ScriptLine scriptLine : this) {
				Real2Range boundingBox = scriptLine.getBoundingBox();
				boundingBox.format(decimalPlaces);
				RealRange range = boundingBox.getXRange();
				Double leftIndent = (range == null) ? null : range.getMin();
				if (leftIndent != null) {
					LOG.trace("BB "+boundingBox+" / "+leftIndent+" / "+((int)scriptLine.toString().charAt(0))+" / "+scriptLine);
					leftIndentSet.add(leftIndent);
				}
			}
		}
		return leftIndentSet;
	}

	public Multiset<Double> getLeftIndentSet() {
		return leftIndentSet;
	}

	public Double getLeftIndent0() {
		return leftIndent0;
	}

	public void setLeftIndent0(Double leftIndent0) {
		this.leftIndent0 = leftIndent0;
	}

	public Double getLeftIndent1() {
		return leftIndent1;
	}

	public void setLeftIndent1(Double leftIndent1) {
		this.leftIndent1 = leftIndent1;
	}

	public void debug() {
		SYSOUT.println("fontFamilySet "+fontFamilySet);
		SYSOUT.println("fontSizeSet "+fontSizeSet);
		SYSOUT.println("scriptLineList ");
		for (ScriptLine scriptLine : scriptLineList) {
			SYSOUT.println("> "+scriptLine.getTextContentWithSpaces());
		}
		SYSOUT.println("leftIndentSet "+leftIndentSet);
		SYSOUT.println("leftIndent0 "+leftIndent0);
		SYSOUT.println("leftIndent1 "+leftIndent1);

	}

	public static ScriptContainer createScriptContainer(File file) {
//		SVGG svgg = (SVGSVG) SVGElement.readAndCreateSVG(file);
//		SVGSVG svgPage = new SVGSVG();
		SVGSVG svgPage = (SVGSVG) SVGElement.readAndCreateSVG(file);
//		SVGSVG svgPage = new SVGSVG();
//		svgPage.appendChild(svgg);
		TextStructurer textStructurer = TextStructurer.createTextStructurerWithSortedLines(file);
		List<TextLine> textLineList = textStructurer.getTextLineList();
		for (TextLine textLine : textLineList) {
			LOG.trace("L> "+String.valueOf(textLine));
		}
		PageAnalyzer pageAnalyzer = new PageAnalyzer(svgPage);
		ScriptContainer sc = ScriptContainer.createScriptContainer(textStructurer, pageAnalyzer);
		return sc;
	}


}
