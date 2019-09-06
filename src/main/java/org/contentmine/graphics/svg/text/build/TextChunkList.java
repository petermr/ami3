package org.contentmine.graphics.svg.text.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.graphics.svg.SVGG;

import nu.xom.Element;

/** 
 * currently a container of TextChunks.
 * A TextChunk depends on an association between phraseChunks. It may often have only a 
 * single PhraseChunk. PhraseChunks are usually collected into a TextChunk by heuristics, such as
 * <ul>
 * <li>geometry such as common/overlapping X-coordinates (a paragraph or ladder)</li>
 * <li>Font styles and sizes</li>
 * <li>containment or proximity to graphic objects (boxes)</li>
 * <li>related content</li>
 * <li>whitespace</li>
 * </ul>
 * 
 * 
 * @author pm286
 */
public class TextChunkList extends SVGG implements Iterable<TextChunk> {
	public static final Logger LOG = Logger.getLogger(TextChunkList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public final static String TAG = "textChunkList";
	private static final int EPS = 5;

	private List<TextChunk> childTextChunkList;
//	private List<Phrase> phrases;
//	private RealArray ySpacings; 
//	private double paraSpacingTrigger;
	private TextChunk lastTextChunk;

	public TextChunkList() {
		super();
		this.setSVGClassName(TAG);
	}
	
	public TextChunkList(TextChunkList textChunkList) {
		this();
		getOrCreateChildTextChunkList();
		childTextChunkList.addAll(textChunkList.getOrCreateChildTextChunkList());
	}

	public TextChunkList(List<TextChunk> textChunkList) {
		this();
		getOrCreateChildTextChunkList();
		for (TextChunk textChunk : textChunkList) {
			this.add(textChunk);
		}
	}

	public TextChunkList(TextChunk textChunk) {
		getOrCreateChildTextChunkList();
		childTextChunkList.add(textChunk);
	}

	public Iterator<TextChunk> iterator() {
		getOrCreateChildTextChunkList();
		return childTextChunkList.iterator();
	}

	public List<TextChunk> getOrCreateChildTextChunkList() {
		if (childTextChunkList == null) {
			List<Element> textChunkChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGG.TAG+"' and @class='"+TextChunk.TAG+"']");
			childTextChunkList = new ArrayList<TextChunk>();
			for (Element child : textChunkChildren) {
				TextChunk textChunk = (TextChunk)child;
				childTextChunkList.add(textChunk);
			}
		}
		return childTextChunkList;
	}

	public String getStringValue() {
		getOrCreateChildTextChunkList();
		StringBuilder sb = new StringBuilder();
		for (TextChunk textChunk : childTextChunkList) {
			sb.append(""+textChunk.getStringValue()+"//");
		}
		this.setStringValueAttribute(sb.toString());
		return sb.toString();
	}

	public void add(TextChunk textChunk) {
		this.appendChild(new TextChunk(textChunk));
		childTextChunkList = null;
		getOrCreateChildTextChunkList();
	}

	public TextChunk get(int i) {
		getOrCreateChildTextChunkList();
		return (i < 0 || i >= childTextChunkList.size()) ? null : childTextChunkList.get(i);
	}
	
	public int size() {
		getOrCreateChildTextChunkList();
		return childTextChunkList.size();
	}

	public Real2Range getBoundingBox() {
		getOrCreateChildTextChunkList();
		Real2Range bbox = null;
		if (childTextChunkList.size() > 0) {
			bbox = childTextChunkList.get(0).getBoundingBox();
			for (int i = 1; i < childTextChunkList.size(); i++) {
				bbox = bbox.plus(childTextChunkList.get(i).getBoundingBox());
			}
		}
		return bbox;
	}

	public void rotateAll(Real2 centreOfRotation, Angle angle) {
		getOrCreateChildTextChunkList();
		for (TextChunk textChunk : childTextChunkList) {
			textChunk.rotateAll(centreOfRotation, angle);
			LOG.trace("PL: "+textChunk.toXML());
		}
		updateTextListList();
	}
	
	public void updateTextListList() {
		// there may be an artificial TextChunk
		for (int i = 0; i < this.getChildElements().size(); i++) {
			this.replaceChild(this.getChildElements().get(i), childTextChunkList.get(i));
		}
	}

	public Real2 getXY() {
		return this.getBoundingBox().getLLURCorners()[0];
	}

	public boolean remove(TextChunk textChunk) {
		boolean remove = false;
		if (childTextChunkList != null && textChunk != null) {
			remove = childTextChunkList.remove(textChunk);
		}
		return remove;
	}
	
	public boolean replace(TextChunk oldTextChunk, TextChunk newTextChunk) {
		boolean replace = false;
		if (childTextChunkList != null) {
			int idx = this.childTextChunkList.indexOf(oldTextChunk);
			if (idx != -1) {
				replace = this.childTextChunkList.set(idx, newTextChunk) != null;
			}
		}
		return replace;
	}



//	/**
//	 * analyses neighbouring TextLists to see if the font sizes and Y-coordinates
//	 * are consistent with sub or superscripts. If so, merges the lines 
//	 * phraseList.mergeByXCoord(lastPhraseList)
//	 * and removes the sub/super line
//	 * lines. The merged phraseList contains all the characters and coordinates in 
//	 * phraseLists with sub/superscript boolean flags.
//	 * 
//	 * getStringValue() represents the sub and superscripts by TeX notation (_{foo} and ^{bar})
//	 * but the actual content retains coordinates and can be output to HTML
//	 * 
//	 * The ratios for the y-values and font sizes are hardcoded but will be settable later.
//	 */
//	public void applySubAndSuperscripts() {
//		Double lastY = null;
//		Double lastFontSize = null;
//		Double deltaY = null;
//		PhraseChunk lastPhraseList = null;
//		List<PhraseChunk> removeList = new ArrayList<PhraseChunk>();
//		for (PhraseChunk phraseList : this) {
//			Double fontSize = Util.format(phraseList.getFontSize(), 1);
//			Double y = Util.format(phraseList.getXY().getY(), 1);
//			if (lastY != null) {
//				double fontRatio = fontSize / lastFontSize;
//				deltaY = y - lastY;
//				if (deltaY > 0 && deltaY < fontSize * SUPERSCRIPT_Y_RATIO && fontRatio >= 1.0) {
//					LOG.trace("SUPER "+lastPhraseList.getStringValue()+" => "+phraseList.getStringValue());
//					lastPhraseList.setSuperscript(true);
//					phraseList.mergeByXCoord(lastPhraseList);
//					removeList.add(lastPhraseList);
//				} else if (deltaY > 0 && deltaY < lastFontSize * SUBSCRIPT_Y_RATIO && fontRatio <= 1.0) {
//					LOG.trace("SUB "+phraseList.getStringValue()+" => "+lastPhraseList.getStringValue());
//					phraseList.setSubscript(true);
//					lastPhraseList.mergeByXCoord(phraseList);
//					removeList.add(phraseList);
//				}
//			}
//			lastPhraseList = phraseList;
//			lastFontSize = fontSize;
//			lastY = y;
//		}
//		for (PhraseChunk phraseList : removeList) {
//			remove(phraseList);
//		}
//	}

//	public List<Phrase> getOrCreatePhrases() {
//		if (phrases == null) {
//			phrases = new ArrayList<Phrase>();
//			for (PhraseChunk phraseList : this) {
//				for (Phrase phrase : phraseList) {
//					phrases.add(phrase);
//				}
//			}
//		}
//		return phrases;
//	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TextChunk textChunk : this) {
			sb.append(textChunk.toString()+"\n");
		}
		return sb.toString();
	}
	
	public HtmlElement toHtml() {
		HtmlDiv div = new HtmlDiv();
		HtmlP p = new HtmlP("FIXME; ");
//		createParaSpacingTrigger();
//		TextChunk lastTextList = null;
//		HtmlP p = new HtmlP();
//		div.appendChild(p);
//		for (int i = 0; i < this.size(); i++) {
//			TextChunk phraseList = this.get(i);
//			if (lastTextList != null) {
//				boolean newPara = triggerNewPara(lastTextList, phraseList);
//				if (newPara) {
//					p = new HtmlP();
//					div.appendChild(p);
//				} else {
//					p.appendChild(" ");
//				}
//			}
//			XMLUtil.transferChildren((Element)phraseList.toHtml().copy(), p);
//			lastTextList = phraseList;
//		}
		return div;
	}

//	private boolean triggerNewPara(TextChunk lastTextList, TextChunk phraseList) {
//		boolean newPara = false;
//		String lastString = lastTextList.getStringValue();
//		if (lastString.length() > 0) {
//			char lastEnd = lastString.charAt(lastString.length() - 1);
//			double deltaY = phraseList.getY() - lastTextList.getY();
//			double deltaX = phraseList.getX() - lastTextList.getX();
//			// just do paras on separation at present
//			if (deltaY > paraSpacingTrigger) {
//				newPara = true;
//			}
//		}
//		return newPara;
//	}

//	private void createParaSpacingTrigger() {
//		paraSpacingTrigger = Double.MAX_VALUE;
//		RealArray spacings = this.getOrCreateYSpacings();
//		if (spacings.size() > 0) {
//			double maxYSpacing = spacings.getMax();
//			double minYSpacing = spacings.getMin();
//			if (maxYSpacing / minYSpacing > PARA_SPACING_FACTOR) {
//				paraSpacingTrigger = (maxYSpacing + minYSpacing) / 2.;
//			} else {
//				paraSpacingTrigger = minYSpacing * PARA_SPACING_FACTOR;
//			}
//		}
//	}

//	private RealArray getOrCreateYSpacings() {
//		if (ySpacings == null) {
//			ySpacings = new RealArray();
//			for (int i = 1; i < this.size(); i++) {
//				double y = Real.normalize(this.get(i).getY() - this.get(i - 1).getY(), 2);
//				ySpacings.addElement(y);
//			}
//		}
//		LOG.trace(ySpacings);
//		return ySpacings;
//	}

	public HtmlUl toHtmlUL() {
		HtmlUl ul = new HtmlUl();
		for (TextChunk textChunk : this) {
			HtmlLi li = new HtmlLi();
			li.appendChild(textChunk.toHtml());
			ul.appendChild(li);
		}
		return ul;
	}

	public HtmlUl getTextListUl() {
		HtmlUl ul = new HtmlUl();
		for (TextChunk textChunk : this) {
			HtmlLi li = new HtmlLi();
			ul.appendChild(li);
			li.appendChild(textChunk.toHtml().copy());
		}
		return ul;
	}

	public String getCSSStyle() {
		String pllStyle = null;
		for (TextChunk textChunk : this) {
			String plStyle = textChunk.getCSSStyle();
			if (pllStyle == null) {
				plStyle = pllStyle;
			} else if (pllStyle.equals(plStyle)) {
				// OK
			} else {
				pllStyle = MIXED_STYLE;
			}
		}
		return pllStyle;
	}

	public void add(PhraseChunk phraseChunk) {
		getOrCreateChildTextChunkList();
		TextChunk lastTextChunk = getLastTextChunk();
		lastTextChunk.add(phraseChunk);
	}

	/**
	 * ensure there is always at least one TextChunk on the list.
	 * 
	 * @return
	 */
	public TextChunk getLastTextChunk() {
		if (lastTextChunk == null) {
			getOrCreateChildTextChunkList();
			if (childTextChunkList.size() == 0) {
				lastTextChunk = new TextChunk();
				childTextChunkList.add(lastTextChunk);
			}
			lastTextChunk = childTextChunkList.get(childTextChunkList.size() - 1);
		}
		return lastTextChunk;
	}
}
