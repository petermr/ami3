package org.contentmine.ami.plugins.word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.ami.plugins.AMIArgProcessor;
import org.contentmine.cproject.files.ResultElement;
import org.contentmine.cproject.files.ResultsElement;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlHtml;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlStyle;
import org.contentmine.eucl.xml.XMLUtil;

/** contains results for bag of words
 * 
 * @author pm286
 *
 */
public class WordResultsElement extends ResultsElement {

	
	private static final Logger LOG = LogManager.getLogger(WordResultsElement.class);
private Set<String> wordSet;

	public WordResultsElement(String title) {
		super(title);
	}

	public WordResultsElement(ResultsElement resultsElement) {
		if (resultsElement == null) {
			throw new RuntimeException("Null ResultsElement");
		}
		XMLUtil.copyAttributesFromTo(resultsElement, this);
		for (ResultElement resultElement : resultsElement) {
			WordResultElement wordResultElement = new WordResultElement(resultElement);
			this.appendChild(wordResultElement);
		}
	}

	private void ensureSet() {
		getOrCreateResultElementList();
		wordSet = new HashSet<String>();
		for (ResultElement resultElement : resultElementList) {
			String word = ((WordResultElement) resultElement).getWord();
			wordSet.add(word);
		}
	}

	public boolean contains(String word) {
		ensureSet();
		return wordSet.contains(word);
	}

	public IntArray getCountArray() {
		getOrCreateResultElementList();
		IntArray countArray = new IntArray();
		for (ResultElement resultElement : resultElementList) {
			WordResultElement wordResultElement = (WordResultElement) resultElement;
			countArray.addElement(wordResultElement.getCount());
		}
		return countArray;
	}

	IntArray createOrderedFontSizeArray() {
		IntArray fontSizeIntArray = null;
		IntArray countArray = getCountArray();
		try {
			IntRange countRange = countArray.getRange();
			RealRange realCountRange = new RealRange(countRange);
			RealRange fontRange = new RealRange(WordArgProcessor.MIN_FONT, WordArgProcessor.MAX_FONT);
			double countToFont = realCountRange.getScaleTo(fontRange);
			RealArray fontSizeArray = new RealArray(countArray);
			fontSizeArray = fontSizeArray.multiplyBy(countToFont);
			fontSizeArray = fontSizeArray.addScalar(WordArgProcessor.MIN_FONT);
			fontSizeIntArray = fontSizeArray.createIntArray();
		} catch (ArrayIndexOutOfBoundsException e) {
			// return null
		}
		return fontSizeIntArray;
	}

	public void writeResultsElementAsHTML(File outputFile, AMIArgProcessor wordArgProcessor) {
		IntArray fontSizeIntArray = createOrderedFontSizeArray();
		if (fontSizeIntArray != null) {
			Set<Integer> fontSizeSet = fontSizeIntArray.createIntegerSet();
			HtmlElement html = createHtmlElement(wordArgProcessor, fontSizeIntArray, fontSizeSet);
			try {
				outputFile.getParentFile().mkdirs();
				LOG.trace("font html "+outputFile);
				XMLUtil.debug(html, new FileOutputStream(outputFile), 1);
			} catch (IOException e) {
				throw new RuntimeException("Cannot write file "+outputFile, e);
			}
		}
	}

	HtmlElement createHtmlElement(AMIArgProcessor wordArgProcessor, IntArray fontSizeIntArray, Set<Integer> fontSizeSet) {
		HtmlHtml html = HtmlHtml.createUTF8Html();
		HtmlStyle style = new HtmlStyle();
		html.appendChild(style);
		style.addCss("* { font-family : helvetica;}");
		for (Integer fontSize : fontSizeSet) {
			String cssStyle = ".font"+fontSize+" { font-size : "+fontSize+"; }";
			style.addCss(cssStyle);
		}
		HtmlP p = addWordsWithFontSizesInSpans(fontSizeIntArray);
		html.getOrCreateBody().appendChild(p);
		return html;
	}

	HtmlP addWordsWithFontSizesInSpans(IntArray fontSizeIntArray) {
		HtmlP p = new HtmlP();
		int i = 0;
		for (ResultElement resultElement : this) {
			WordResultElement wordResultElement = (WordResultElement) resultElement;
			String word = wordResultElement.getWord();
			String length = String.valueOf(wordResultElement.getLength());
//			int count = wordResultElement.getCount();
			int fontSize = fontSizeIntArray.elementAt(i);
			HtmlSpan span = new HtmlSpan();
			span.setClassAttribute("font"+fontSize);
			span.appendChild(word != null ? word+" " : length);
			p.appendChild(span);
			i++;
		}
		return p;
	}


}
