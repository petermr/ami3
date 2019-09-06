package org.contentmine.graphics.svg.cache;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.svg.text.SVGTextLine;
import org.contentmine.graphics.svg.text.SVGTextLineList;

import nu.xom.Node;
import nu.xom.Text;

/** formats textlines especially wrapping, de-hyphenation.
 * defaults:
 * 		dehyphenate = true;
		joinLines = true;
		indentedParagraphs = true;
		indentedContinuation = false;
		addSpaceInLineJoins = true;
		ndecimal = 2; 
		minimumFontScaledLeftIndent = 1.3;

 * @author pm286
 *
 */
public class TextLineFormatter {
	private static final Logger LOG = Logger.getLogger(TextLineFormatter.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final String SPACE = " ";

	private static final String HYPHEN = "-";

	private boolean dehyphenate = true;
	private boolean joinLines = true;
	private boolean indentedParagraphs = true;
	private boolean indentedContinuation = false;
	private boolean addSpaceInLineJoins = true;
	private HtmlDiv htmlDiv;
	private HtmlSpan previousSpan;
	private boolean processedHyphen;
	private int ndecimal = 1; 
	private double minimumFontScaledLeftIndent = 1.3;
	private TextCache textCache;

	private SVGTextLineList textLines;
	
	public TextLineFormatter(TextCache textCache) {
		setDefaults();
		this.setTextCache(textCache);
	}

	private void setDefaults() {
		dehyphenate = true;
		joinLines = true;
		indentedParagraphs = true;
		indentedContinuation = false;
		addSpaceInLineJoins = true;
		ndecimal = 2; 
		minimumFontScaledLeftIndent = 1.3;
	}

	/** create formatter for math equations.
	 * currently:
		lineFormatter.dehyphenate = false;
		lineFormatter.joinLines = false;
		lineFormatter.indentedParagraphs = false;
		lineFormatter.indentedContinuation = true;
		lineFormatter.addSpaceInLineJoins = true;
		ndecimal = 2; 
		minimumFontScaledLeftIndent = 1.3;
	 * 
	 * @return
	 */
	public static TextLineFormatter createEquationFormatter(TextCache textCache) {
		TextLineFormatter lineFormatter = new TextLineFormatter(textCache);
		lineFormatter.dehyphenate = false;
		lineFormatter.joinLines = false;
		lineFormatter.indentedParagraphs = false;
		lineFormatter.indentedContinuation = true;
		lineFormatter.addSpaceInLineJoins = true;
		return lineFormatter;
	}

	/** create formatter for math equations.
	 * currently:
		lineFormatter.dehyphenate = false;
		lineFormatter.joinLines = false;
		lineFormatter.indentedParagraphs = false;
		lineFormatter.indentedContinuation = true;
		lineFormatter.addSpaceInLineJoins = true;
		ndecimal = 2; 
		minimumFontScaledLeftIndent = 1.3;
	 * 
	 * @return
	 */
	public static TextLineFormatter createReferenceFormatter(TextCache textCache) {
		TextLineFormatter lineFormatter = new TextLineFormatter(textCache);
		lineFormatter.dehyphenate = false;
		lineFormatter.joinLines = false;
		lineFormatter.indentedParagraphs = false;
		lineFormatter.indentedContinuation = true;
		lineFormatter.addSpaceInLineJoins = true;
		return lineFormatter;
	}
	

	/** default formatter
	 * currently:
	dehyphenate = true;
	joinLines = true;
	indentedParagraphs = true;
	indentedContinuation = false;
	addSpaceInLineJoins = true;

	 * @return
	 */
	public static TextLineFormatter createDefaultFormatter(TextCache textCache) {
		TextLineFormatter lineFormatter = new TextLineFormatter(textCache);
		lineFormatter.setDefaults();
		return lineFormatter;
	}

	public boolean isDehyphenate() {
		return dehyphenate;
	}

	/** removes trailing hyphen.
	 * 
	 * @param dehyphenate
	 */
	public void setDehypenate(boolean dehyphenate) {
		this.dehyphenate = dehyphenate;
	}

	public boolean isJoinLines() {
		return joinLines;
	}

	/** join line to previous.
	 * 
	 * @param joinLines
	 */
	public void setJoinLines(boolean joinLines) {
		this.joinLines = joinLines;
	}

	public boolean isIndentedParagraphs() {
		return indentedParagraphs;
	}

	/** treat left indents as new paragraphs.
	 * 
	 * @param indentedParagraphs
	 */
	public void setIndentedParagraphs(boolean indentedParagraphs) {
		this.indentedParagraphs = indentedParagraphs;
	}

	public boolean isIndentedContinuation() {
		return indentedContinuation;
	}

	/** treat left indents as line continuations.
	 * this is common in small paras (e.g. table cells or equations)
	 * 
	 * @return
	 */
	public void setIndentedContinuation(boolean indentedContinuation) {
		this.indentedContinuation = indentedContinuation;
	}

	public boolean isAddSpaceInLineJoins() {
		return addSpaceInLineJoins;
	}

	/** add space when joining lines
	 * 
	 * @param addSpaceInLineJoins
	 */
	public void setAddSpaceInLineJoins(boolean addSpaceInLineJoins) {
		this.addSpaceInLineJoins = addSpaceInLineJoins;
	}

	public void appendLine(HtmlSpan lineSpan) {
		getOrCreateDiv();
		if (previousSpan == null) {
			previousSpan = lineSpan;
			htmlDiv.appendChild(previousSpan);
		} else {
			if (dehyphenate) {
				processedHyphen = removeTrailingHyphen();
			} 
			if (addSpaceInLineJoins && !processedHyphen) {
				previousSpan.appendChild(SPACE);
			}
			addLineSpanChildNodes(lineSpan);
		}
	}

	private boolean removeTrailingHyphen() {
		boolean dehyphenated = false;
		if (previousSpan != null) {
			Text previousChildText = previousSpan.getFinalTextNode();
			if (previousChildText != null) {
				String text = previousChildText.getValue();
				if (text.endsWith(HYPHEN)) {
					text = text.substring(0, text.length() - 1);
					previousChildText.setValue(text);
					dehyphenated = true;
				}
			}
		}
		return dehyphenated;
	}

	private void addLineSpanChildNodes(HtmlSpan lineSpan) {
		for (int i = 0; i < lineSpan.getChildCount(); i++) {
			Node child = lineSpan.getChild(i);
			previousSpan.appendChild(child.copy());
		}
	}

	public HtmlDiv getOrCreateDiv() {
		if (htmlDiv == null) {
			htmlDiv = new HtmlDiv();
		}
		return htmlDiv;
	}

	/** packages lines into HtmlP or Div.
	 * later may do lists  and equations.
	 * 
	 * @return
	 */
	public HtmlElement createHtmlElement() {
		return htmlDiv;
	}

	public void setTextLines(SVGTextLineList textLines) {
		this.textLines = textLines;
	}

	public SVGTextLineList addSuscriptsAndJoinWrappedLines() {
		
		getTextCache().getSuscriptFormatter().addSuscripts(getTextCache());
		textLines = getTextCache().getOrCreateTextLines();
		getTextCache().processedTextLines = joinFollowingIndentedLines(getTextCache().getLargestCurrentFont());
		return getTextCache().processedTextLines;
	}

	public SVGTextLineList joinFollowingIndentedLines(Double fontSize) {
		getOrCreateTextLines();
		if (textLines != null) {
			RealArray xLeftArray = textLines.calculateIndents(ndecimal);
			if (xLeftArray.size() > 0) {
				double minimumLeftX = xLeftArray.getMin();
				for (int index = textLines.size() - 1; index > 0; index--) {
					SVGTextLine textLine = textLines.get(index);
					if (textLine.isLeftIndented(minimumFontScaledLeftIndent * fontSize, minimumLeftX)) {
						if (index > 0) {
							SVGTextLine precedingTextLine = textLines.get(index - 1);
							precedingTextLine.append(textLine, fontSize);
							precedingTextLine.forceFullSVGElement();
						}
						textLines.remove(index);
					}
				}
			}
		}
		return textLines;
	}

	private SVGTextLineList getOrCreateTextLines() {
		if (textLines == null) {
			if (textCache != null) {
				textLines = textCache.getOrCreateTextLines();
			}
		}
		return textLines;
	}

	/** joins lines if they are indented.
	 * 
	 * @param textCache TODO
	 * @param ndecimal TODO
	 * @param minIndentFactor TODO
	 * @return
	 */
	public SVGTextLineList createAndJoinIndentedTextLineList() {
		textCache.getTextLinesForLargestFont();
		textCache.textLineListForLargestFont = joinFollowingIndentedLines(textCache.largestCurrentFont);
		return textCache.textLineListForLargestFont;
	}

	public TextCache getTextCache() {
		return textCache;
	}

	public void setTextCache(TextCache textCache) {
		this.textCache = textCache;
	}
	
	
}
