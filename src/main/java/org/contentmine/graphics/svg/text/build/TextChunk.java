package org.contentmine.graphics.svg.text.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Angle;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.IntRangeArray;
import org.contentmine.eucl.euclid.Real;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealArray;
import org.contentmine.eucl.euclid.Util;
import org.contentmine.eucl.xml.XMLUtil;
import org.contentmine.graphics.html.HtmlDiv;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.html.HtmlLi;
import org.contentmine.graphics.html.HtmlP;
import org.contentmine.graphics.html.HtmlUl;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.rule.horizontal.LineChunk;

import nu.xom.Element;

/** 
 * currently a container of PhraseChunks.
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
public class TextChunk extends SVGG implements Iterable<PhraseChunk> {
	private static final double PARA_SPACING_FACTOR = 1.2;
	public static final Logger LOG = Logger.getLogger(TextChunk.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public static final Double SUPERSCRIPT_Y_RATIO = 0.5;
	public static final Double SUBSCRIPT_Y_RATIO = 0.75;
//	private static final Double SUPERSCRIPT_FONT_RATIO = 1.05;

	public final static String TAG = "textChunk";
	private static final int EPS = 5;

	private List<PhraseChunk> childPhraseChunkList;
	private List<Phrase> phrases;
	private RealArray ySpacings; 
	private double paraSpacingTrigger;

	public TextChunk() {
		super();
		this.setSVGClassName(TAG);
	}
	
	public TextChunk(TextChunk textChunk) {
		this();
		getOrCreateChildPhraseChunkList();
		childPhraseChunkList.addAll(textChunk.getOrCreateChildPhraseChunkList());
	}

	public TextChunk(List<PhraseChunk> phraseChunkList) {
		this();
		getOrCreateChildPhraseChunkList();
		for (PhraseChunk phraseChunk : phraseChunkList) {
			this.add(phraseChunk);
		}
	}

	public Iterator<PhraseChunk> iterator() {
		getOrCreateChildPhraseChunkList();
		return childPhraseChunkList.iterator();
	}

	public List<PhraseChunk> getOrCreateChildPhraseChunkList() {
		if (childPhraseChunkList == null) {
			List<Element> phraseChildren = XMLUtil.getQueryElements(this, "*[local-name()='"+SVGG.TAG+"' and @class='"+PhraseChunk.TAG+"']");
			childPhraseChunkList = new ArrayList<PhraseChunk>();
			for (Element child : phraseChildren) {
				PhraseChunk phraseChunk = (PhraseChunk)child;
				childPhraseChunkList.add(phraseChunk);
			}
		}
		return childPhraseChunkList;
	}

	public String getStringValue() {
		getOrCreateChildPhraseChunkList();
		StringBuilder sb = new StringBuilder();
		for (PhraseChunk phraseChunk : childPhraseChunkList) {
			sb.append(""+phraseChunk.getStringValue()+"//");
		}
		this.setStringValueAttribute(sb.toString());
		return sb.toString();
	}

	public void add(PhraseChunk phraseChunk) {
		this.appendChild(new PhraseChunk(phraseChunk));
		childPhraseChunkList = null;
		getOrCreateChildPhraseChunkList();
	}

	public PhraseChunk get(int i) {
		getOrCreateChildPhraseChunkList();
		return (i < 0 || i >= childPhraseChunkList.size()) ? null : childPhraseChunkList.get(i);
	}
	
	protected List<? extends LineChunk> getChildChunks() {
		getOrCreateChildPhraseChunkList();
		return childPhraseChunkList;
	}


	public List<IntArray> getLeftMarginsList() {
		getOrCreateChildPhraseChunkList();
		List<IntArray> leftMarginsList = new ArrayList<IntArray>();
		for (PhraseChunk phraseChunk : childPhraseChunkList) {
			IntArray leftMargins = phraseChunk.getLeftMargins();
			leftMarginsList.add(leftMargins);
		}
		return leftMarginsList;
	}
	
	/** assumes the largest index in phraseChunk is main body of table.
	 * 
	 * @return
	 */
	public int getMaxColumns() {
		getOrCreateChildPhraseChunkList();
		int maxColumns = 0;
		for (PhraseChunk phraseChunk : childPhraseChunkList) {
			maxColumns = Math.max(maxColumns, phraseChunk.size());
		}
		return maxColumns;
	}

	public IntRangeArray getBestColumnRanges() {
		getOrCreateChildPhraseChunkList();
		int maxColumns = getMaxColumns();
		IntRangeArray columnRanges = new IntRangeArray();
		for (int i = 0; i < maxColumns; i++) {
			columnRanges.set(i, (IntRange)null);
		}
		for (PhraseChunk phraseChunk : childPhraseChunkList) {
			if (phraseChunk.size() == maxColumns) {
				for (int i = 0; i < phraseChunk.size(); i++) {
					Phrase phrase = phraseChunk.get(i);
					IntRange range = phrase.getIntRange();
					IntRange oldRange = columnRanges.get(i);
					range = (oldRange == null) ? range : range.plus(oldRange);
					columnRanges.set(i, range);
				}
			}
		}
		return columnRanges;
	}
	
	public IntRangeArray getBestWhitespaceRanges() {
		getOrCreateChildPhraseChunkList();
		int maxColumns = getMaxColumns();
		IntRangeArray bestColumnRanges = getBestColumnRanges();
		IntRangeArray bestWhitespaces = new IntRangeArray();
		if (maxColumns > 0) {
			bestWhitespaces.add(new IntRange(bestColumnRanges.get(0).getMin() - EPS, bestColumnRanges.get(0).getMax() - EPS));
			for (int i = 1; i < maxColumns; i++) {
				IntRange whitespace = new IntRange(bestColumnRanges.get(i - 1).getMax(), bestColumnRanges.get(i).getMax());
				bestWhitespaces.add(whitespace);
			}
		}
		return bestWhitespaces;
	}
	
	/** find rightmostWhitespace range which includes start of phrase.
	 * 
	 */
	public int getRightmostEnclosingWhitespace(List<IntRange> bestWhitespaces, Phrase phrase) {
		for (int i = bestWhitespaces.size() - 1; i >= 0; i--) {
			IntRange range = bestWhitespaces.get(i);
			int phraseX = (int)(double) phrase.getStartX();
			if (range.contains(phraseX)) {
				return i;
			}
		}
		return -1;
	}

	public int size() {
		getOrCreateChildPhraseChunkList();
		return childPhraseChunkList.size();
	}

	public Real2Range getBoundingBox() {
		getOrCreateChildPhraseChunkList();
		Real2Range bbox = null;
		if (childPhraseChunkList.size() > 0) {
			bbox = childPhraseChunkList.get(0).getBoundingBox();
			for (int i = 1; i < childPhraseChunkList.size(); i++) {
				bbox = bbox.plus(childPhraseChunkList.get(i).getBoundingBox());
			}
		}
		return bbox;
	}

	public void rotateAll(Real2 centreOfRotation, Angle angle) {
		getOrCreateChildPhraseChunkList();
		for (PhraseChunk phraseChunk : childPhraseChunkList) {
			phraseChunk.rotateAll(centreOfRotation, angle);
			LOG.trace("PL: "+phraseChunk.toXML());
		}
		updatePhraseListList();
	}
	
	public void updatePhraseListList() {
		for (int i = 0; i < childPhraseChunkList.size(); i++) {
			this.replaceChild(this.getChildElements().get(i), childPhraseChunkList.get(i));
		}
	}

	public Real2 getXY() {
		return this.getBoundingBox().getLLURCorners()[0];
	}

	public boolean remove(PhraseChunk phraseChunk) {
		boolean remove = false;
		if (childPhraseChunkList != null && phraseChunk != null) {
			remove = childPhraseChunkList.remove(phraseChunk);
		}
		return remove;
	}
	
	public boolean replace(PhraseChunk oldPhraseList, PhraseChunk newPhraseList) {
		boolean replace = false;
		if (childPhraseChunkList != null) {
			int idx = this.childPhraseChunkList.indexOf(oldPhraseList);
			if (idx != -1) {
				replace = this.childPhraseChunkList.set(idx, newPhraseList) != null;
			}
		}
		return replace;
	}



	/**
	 * analyses neighbouring PhraseLists to see if the font sizes and Y-coordinates
	 * are consistent with sub or superscripts. If so, merges the lines 
	 * phraseChunk.mergeByXCoord(lastPhraseList)
	 * and removes the sub/super line
	 * lines. The merged phraseChunk contains all the characters and coordinates in 
	 * phraseChunks with sub/superscript boolean flags.
	 * 
	 * getStringValue() represents the sub and superscripts by TeX notation (_{foo} and ^{bar})
	 * but the actual content retains coordinates and can be output to HTML
	 * 
	 * The ratios for the y-values and font sizes are hardcoded but will be settable later.
	 */
	public void applySubAndSuperscripts() {
		Double lastY = null;
		Double lastFontSize = null;
		Double deltaY = null;
		PhraseChunk lastPhraseList = null;
		List<PhraseChunk> removeList = new ArrayList<PhraseChunk>();
		for (PhraseChunk phraseChunk : this) {
			Double fontSize = Util.format(phraseChunk.getFontSize(), 1);
			Double y = Util.format(phraseChunk.getXY().getY(), 1);
			if (lastY != null) {
				double fontRatio = fontSize / lastFontSize;
				deltaY = y - lastY;
				if (deltaY > 0 && deltaY < fontSize * SUPERSCRIPT_Y_RATIO && fontRatio >= 1.0) {
					LOG.trace("SUPER "+lastPhraseList.getStringValue()+" => "+phraseChunk.getStringValue());
					lastPhraseList.setSuperscript(true);
					phraseChunk.mergeByXCoord(lastPhraseList);
					removeList.add(lastPhraseList);
				} else if (deltaY > 0 && deltaY < lastFontSize * SUBSCRIPT_Y_RATIO && fontRatio <= 1.0) {
					LOG.trace("SUB "+phraseChunk.getStringValue()+" => "+lastPhraseList.getStringValue());
					phraseChunk.setSubscript(true);
					lastPhraseList.mergeByXCoord(phraseChunk);
					removeList.add(phraseChunk);
				}
			}
			lastPhraseList = phraseChunk;
			lastFontSize = fontSize;
			lastY = y;
		}
		for (PhraseChunk phraseChunk : removeList) {
			remove(phraseChunk);
		}
	}

	public List<Phrase> getOrCreatePhrases() {
		if (phrases == null) {
			phrases = new ArrayList<Phrase>();
			for (PhraseChunk phraseChunk : this) {
				for (Phrase phrase : phraseChunk) {
					phrases.add(phrase);
				}
			}
		}
		return phrases;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (PhraseChunk phraseChunk : this) {
			sb.append(phraseChunk.toString()+"\n");
		}
		return sb.toString();
	}
	
	public HtmlElement toHtml() {
		HtmlDiv div = new HtmlDiv();
		createParaSpacingTrigger();
		PhraseChunk lastPhraseList = null;
		HtmlP p = new HtmlP();
		div.appendChild(p);
		for (int i = 0; i < this.size(); i++) {
			PhraseChunk phraseChunk = this.get(i);
			if (lastPhraseList != null) {
				boolean newPara = triggerNewPara(lastPhraseList, phraseChunk);
				if (newPara) {
					p = new HtmlP();
					div.appendChild(p);
				} else {
					p.appendChild(" ");
				}
			}
			XMLUtil.transferChildren((Element)phraseChunk.toHtml().copy(), p);
			lastPhraseList = phraseChunk;
		}
		return div;
	}

	private boolean triggerNewPara(PhraseChunk lastPhraseList, PhraseChunk phraseChunk) {
		boolean newPara = false;
		String lastString = lastPhraseList.getStringValue();
		if (lastString.length() > 0) {
			char lastEnd = lastString.charAt(lastString.length() - 1);
			double deltaY = phraseChunk.getY() - lastPhraseList.getY();
			double deltaX = phraseChunk.getX() - lastPhraseList.getX();
			// just do paras on separation at present
			if (deltaY > paraSpacingTrigger) {
				newPara = true;
			}
		}
		return newPara;
	}

	private void createParaSpacingTrigger() {
		paraSpacingTrigger = Double.MAX_VALUE;
		RealArray spacings = this.getOrCreateYSpacings();
		if (spacings.size() > 0) {
			double maxYSpacing = spacings.getMax();
			double minYSpacing = spacings.getMin();
			if (maxYSpacing / minYSpacing > PARA_SPACING_FACTOR) {
				paraSpacingTrigger = (maxYSpacing + minYSpacing) / 2.;
			} else {
				paraSpacingTrigger = minYSpacing * PARA_SPACING_FACTOR;
			}
		}
	}

	private RealArray getOrCreateYSpacings() {
		if (ySpacings == null) {
			ySpacings = new RealArray();
			for (int i = 1; i < this.size(); i++) {
				double y = Real.normalize(this.get(i).getY() - this.get(i - 1).getY(), 2);
				ySpacings.addElement(y);
			}
		}
		LOG.trace(ySpacings);
		return ySpacings;
	}

	public HtmlUl toHtmlUL() {
		HtmlUl ul = new HtmlUl();
		for (PhraseChunk phraseChunk : this) {
			HtmlLi li = new HtmlLi();
			li.appendChild(phraseChunk.toHtml());
			ul.appendChild(li);
		}
		return ul;
	}

	public HtmlUl getPhraseListUl() {
		HtmlUl ul = new HtmlUl();
		for (PhraseChunk phraseChunk : this) {
			HtmlLi li = new HtmlLi();
			ul.appendChild(li);
			li.appendChild(phraseChunk.toHtml().copy());
		}
		return ul;
	}

	public String getCSSStyle() {
		String pllStyle = null;
		for (PhraseChunk phraseChunk : this) {
			String plStyle = phraseChunk.getCSSStyle();
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

	public PhraseChunk getLastPhraseChunk() {
		getOrCreateChildPhraseChunkList();
		if (childPhraseChunkList.size() == 0) {
			PhraseChunk phraseChunk = new PhraseChunk();
			childPhraseChunkList.add(phraseChunk);
		}
		return childPhraseChunkList.get(0);
	}


}
