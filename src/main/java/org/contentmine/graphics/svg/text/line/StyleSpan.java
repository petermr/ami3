package org.contentmine.graphics.svg.text.line;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.graphics.html.HtmlB;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlI;
import org.contentmine.graphics.html.HtmlSpan;
import org.contentmine.graphics.html.HtmlSub;
import org.contentmine.graphics.html.HtmlSup;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.SVGUtil;

/** 
 * moved from svg2xml
 * 
 * @author pm286
 *
 */
public class StyleSpan {

	private final static Logger LOG = Logger.getLogger(StyleSpan.class);
	private static final double EPS = 0.01;
	private static final Double MIN_WIDTH = 0.01;
	
	private boolean bold;
	private boolean italic;
	private List<SVGText> characterList;

	public StyleSpan() {
		
	}

	public StyleSpan(boolean bold, boolean italic) {
		this.bold = bold;
		this.italic = italic;
	}

	public void addCharacter(SVGText character) {
		ensureCharacterList();
		if (character.getFontSize() == null) {
			throw new RuntimeException("Missing fontSize :::: "+character.toXML());
		}
		characterList.add(character);
	}

	private void ensureCharacterList() {
		if (characterList == null) {
			characterList = new ArrayList<SVGText>();
		}
	}
	
	public boolean isBold() {return bold;}
	public boolean isItalic() {return italic;}
	
	public String getTextContentWithStyleAndSpaces() {
		StringBuilder sb = new StringBuilder();
		if (bold) {sb.append("<B>");}
		if (italic) {sb.append("<I>");}
		sb.append(getTextContentWithSpaces());
		if (italic) {sb.append("</I>");}
		if (bold) {sb.append("</B>");}
		return sb.toString();
	}

	public HtmlElement createHtmlElement() {
		HtmlElement htmlElement = new HtmlSpan();
		HtmlElement currentHtml = htmlElement;
		SVGElement character = (characterList.size() == 0) ? null : characterList.get(0);
		String suscript = (character == null) ? null : SVGUtil.getSVGXAttribute(character, ScriptLine.SUSCRIPT); 
		boolean sub = ScriptLine.SUB.equals(suscript);
		boolean sup = ScriptLine.SUP.equals(suscript);
		if (sub) {
			HtmlElement subElement = new HtmlSub();
			currentHtml.appendChild(subElement);
			currentHtml = subElement;
		}
		if (sup) {
			HtmlElement supElement = new HtmlSup();
			currentHtml.appendChild(supElement);
			currentHtml = supElement;
		}
		if (bold) {
			HtmlElement bold = new HtmlB();
			currentHtml.appendChild(bold);
			currentHtml = bold;
		}
		if (italic) {
			HtmlElement italic = new HtmlI();
			currentHtml.appendChild(italic);
			currentHtml = italic;
		}
		StringBuilder sb = new StringBuilder();
		for (SVGText charact : characterList) {
			sb.append(charact.getText());
		}
		currentHtml.appendChild(sb.toString());
		return htmlElement;
	}

	
	public static StyleSpan createSpace(Double fontSize) {
		StyleSpan styleSpan = new StyleSpan();
		SVGText text = new SVGText();
		text.setText(" ");
		text.setFontSize(fontSize);
		styleSpan.addCharacter(text);
		return styleSpan;
	}

	public String getTextContentWithSpaces() {
		StringBuilder sb = new StringBuilder();
		SVGText lastText = null;
		for (SVGText text : characterList) {
			Double fontSize = text.getFontSize();
			if (fontSize == null) {
				throw new RuntimeException("missing fontSize ::: "+text.toXML());
			}
			String sp = StyleSpan.computeInterveningSpaces(lastText, text);
			if (sp != null && !sp.equals("")) {
				sb.append(sp);
			}
			sb.append(text.getText());
			lastText = text;
			if (lastText.getFontSize() == null) {
				lastText.setFontSize(fontSize);
			}
		}
		return sb.toString();
	}

	/** add spaces corresponding to distance between last text and text
	 * 
	 * uses fontSize and width of last Text
	 * nspaces = (text.getX()-lastText.getBoundingBox().getXMax()) / lastText.getFontSize()*lastText.getFontWidth()
	 * nspaces == null => no action else returns string with computed spaces
	 * 
	 * @param lastText if null no action
	 * @param text if null no action
	 * @return null if no spaces else computed number of spaces
	 */
	public static String computeInterveningSpaces(SVGText lastText, SVGText text) {
		String spaces = null;
		if (lastText != null && text != null) {
			if (lastText.getFontSize() == null) {
				LOG.warn("Missing fontSize in :"+lastText.toXML());
			}
			if (text.getFontSize() == null) {
				LOG.warn("Missing fontSize in ::"+text.toXML());
			}
			 Double x0 = lastText.getBoundingBox().getXMax();
			 Double x1 = text.getX();
			 if (x0 != null && x1 != null) {
				 double deltax = x1 - x0;
				 Double fontSize = lastText.getFontSize();
				 Double fontWidth = lastText.getSVGXFontWidth();
				 if (fontSize != null && fontWidth != null) {
					 Double spaceWidth = fontSize * fontWidth;
					 if (spaceWidth > MIN_WIDTH) {
						 double sp = deltax / spaceWidth;
						 int nspaces = (int) sp;
						 if (nspaces > 0) {
							 StringBuilder sb = new StringBuilder();
							 for (int i = 0; i < nspaces; i++) {
								 sb.append(" ");
							 }
							 spaces = sb.toString();
						 }
					 }
				 }
			 }
		}
		return spaces;
	}
	
	public Double getFontSize() {
		Double fontSize = null;
		for (SVGElement character : characterList) {
			Double fontSize0 = character.getFontSize();
			
			// skip inserted spaces
			if (!Real.isEqual(1.0, fontSize0, EPS) && character.getValue().trim().length() != 0) {
				LOG.trace("["+character.toXML()+"] "+fontSize0);
				if (fontSize == null) {
					fontSize = fontSize0;
				} else if (!Real.isEqual(fontSize, fontSize0, EPS)) {
					throw new RuntimeException("fontsize changed in span: "+fontSize+" => "+fontSize0);
				}
			}
		}
		return fontSize;
	}
	
	@Override
	public String toString() {
		return getTextContentWithStyleAndSpaces();
	}
	
}
