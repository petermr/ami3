package org.contentmine.graphics.svg.text.line;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.text.build.Phrase;
import org.contentmine.graphics.svg.text.build.PhraseChunk;
import org.contentmine.graphics.svg.text.build.SusType;
import org.contentmine.graphics.svg.text.build.TextChunk;

/** merges phrases which might be related as sub or superscripts
 * 
 * moved from SVG2XML
 * 
 * @author pm286
 *
 */
public class SuscriptEditor {

	private static final Logger LOG = Logger.getLogger(SuscriptEditor.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private TextChunk textChunk;
	private double minSubFontRatio = 0.4;
	private double maxSubFontRatio = 0.8;
	private double minSubOffsetRatio = 0.2;
	private double maxSubOffsetRatio = 0.8;
	private double minSuperFontRatio = 0.4;
	private double maxSuperFontRatio = 0.8;
	private double minSuperOffsetRatio = 0.2;
	private double maxSuperOffsetRatio = 0.8;
	
//	private Metrics metrics0;
//	private Metrics metrics1;
	private double yDelta;
private boolean hasSuscripts;
	
	
	public SuscriptEditor(TextChunk textChunk) {
		setPhraseListList(textChunk);
		setDefaults();
	}

	private void setDefaults() {
		minSubFontRatio = 0.4;
		maxSubFontRatio = 0.8;
		minSubOffsetRatio = 0.2;
		maxSubOffsetRatio = 0.8;
		minSuperFontRatio = 0.4;
		maxSuperFontRatio = 0.8;
		minSuperOffsetRatio = 0.2;
		maxSuperOffsetRatio = 0.8;
	}

	public PhraseChunk mergeSuscripts(SusType susType, PhraseChunk phraseChunk0, PhraseChunk phraseChunk1) {
		LOG.trace("Merging? "+phraseChunk0+" // "+phraseChunk1);
		Double y0 = phraseChunk0.getY();
		Double y1 = phraseChunk1.getY();
		if (y0 == null || y1 == null) {
			LOG.trace("unexpected null y0/y1");
			return null;
		}
		yDelta = y1 - y0; // always positive
		double fontRatio01 = phraseChunk0.getFontSize() / phraseChunk1.getFontSize();
		double fontRatio10 = 1.0 / fontRatio01;
		LOG.trace(yDelta + " || "+fontRatio01);
		PhraseChunk newPhraseList = null;
		LOG.trace("metrics ==="+susType+"====> "+yDelta+" / "+phraseChunk0.getFontSize()+" | "+fontRatio01+" ( "+minSuperFontRatio+ " - "+maxSuperFontRatio+")");
		if (SusType.SUPER.equals(susType)) {
			if (fontRatio01 < minSuperFontRatio || fontRatio01 > maxSuperFontRatio) {
				return null;
			}
			if (yDelta > phraseChunk1.getFontSize()) {
				return null;
			}
		}
		if (SusType.SUB.equals(susType)) {
			if (fontRatio10 < minSubFontRatio || fontRatio10 > maxSubFontRatio) {
				return null;
			}
			if (yDelta > phraseChunk0.getFontSize()) {
				return null;
			}
		}
		LOG.trace(susType+" \n"+phraseChunk0.getStringValue() + "\n"+phraseChunk1.getStringValue());
		newPhraseList = mergePhraseListsByIncreasingX(susType, phraseChunk0, phraseChunk1);
		if (hasSuscripts) {
			newPhraseList = joinPhraseComponents(newPhraseList);
		}
		LOG.trace(newPhraseList.toXML());
		LOG.trace(newPhraseList.getStringValue());
		return newPhraseList;
	}

	private PhraseChunk mergePhraseListsByIncreasingX(SusType susType, PhraseChunk phraseChunk0, PhraseChunk phraseChunk1) {
		PhraseChunk newPhraseChunk;
		int index0 = 0;
		int index1 = 0;
		newPhraseChunk = new PhraseChunk();
		hasSuscripts = false;
		while (true) {
			Phrase phrase0 = index0 >= phraseChunk0.size() ? null : phraseChunk0.get(index0);
			Double x0 = phrase0 == null ? null : phrase0.getX();
			Phrase phrase1 = index1 >= phraseChunk1.size() ? null : phraseChunk1.get(index1);
			Double x1 = phrase1 == null ? null : phrase1.getX();
			if (SusType.SUPER.equals(susType) && phrase0 != null) {
				phrase0.setSuscript(susType, true);
				hasSuscripts = true;
			} else if (SusType.SUB.equals(susType) && phrase1 != null) {
				phrase1.setSuscript(susType, true);
				hasSuscripts = true;
			}
			if (x0 == null) {
				if (x1 == null) {
					break;
				}
				while (index1 < phraseChunk1.size()) {
					newPhraseChunk.add(new Phrase(phraseChunk1.get(index1++)));
				}
			} else if (x1 == null) {
				while (index0 < phraseChunk0.size()) {
					phrase0 = phraseChunk0.get(index0++);
					newPhraseChunk.add(new Phrase(phrase0));
				}
			} else if (x0 < x1) {
				newPhraseChunk.add(new Phrase(phrase0));
				index0++;
			} else {
				newPhraseChunk.add(new Phrase(phrase1));
				index1++;
			}
		}
		return newPhraseChunk;
	}

	private PhraseChunk joinPhraseComponents(PhraseChunk phraseChunk) {
		if (phraseChunk == null || phraseChunk.size() < 2) {
			return phraseChunk;
		}
		Phrase lastPhrase = null;
		PhraseChunk newPhraseList = new PhraseChunk();
		for (int i = 0; i < phraseChunk.size(); i++) {
			Phrase phrase = phraseChunk.get(i);
			LOG.trace("PH "+phrase+"/"+phrase.hasSubscript());
			if (lastPhrase == null) {
				// 1st phrase
				lastPhrase = phrase;
			} else if (!lastPhrase.shouldAddSpaceBefore(phrase)) {
				lastPhrase.mergePhrase(phrase);
				LOG.trace("JOIN "+lastPhrase.toXML()+" => "+phrase);
			} else {
				newPhraseList.add(new Phrase(lastPhrase));
				lastPhrase = phrase;
			}
		}
		if (lastPhrase != null) {
			newPhraseList.add(new Phrase(lastPhrase));
		}
		LOG.trace("NEW "+newPhraseList);
		return newPhraseList;
	}



	public AbstractCMElement mergeAll() {
		int size = textChunk.size();
		for (int i = 0; i < size - 1;) {
			PhraseChunk phraseChunk0 = textChunk.get(i);
			PhraseChunk phraseChunk1 = textChunk.get(i + 1);
			LOG.trace("======================================================================\n"
					+"SUPER "+i+"/"+size+"\n"+phraseChunk0+"\n"+phraseChunk1);
			if (mergePhraseListsVertically(SusType.SUPER, i, i+1)) {
				LOG.trace("MERGED SUPER");
				size--;
			} else {
			};
			if (i < size - 1) {
				LOG.trace("======================================================================\n"
					+ "SUB "+i+"/"+size+"\n"+textChunk.get(i)+"\n"+textChunk.get(i + 1));
				if (mergePhraseListsVertically(SusType.SUB, i, i + 1)) {
					LOG.trace("MERGED SUB");
					size--;
				}
			}
			i++;
		}
		LOG.trace("condensed all "+textChunk);
		return textChunk;
	}

	private boolean mergePhraseListsVertically(SusType susType, int line0, int line1) {
		boolean merged = false;
		PhraseChunk phraseChunk0 = textChunk.get(line0);
		PhraseChunk phraseChunk1 = textChunk.get(line1);
		PhraseChunk newPhraseList = mergeSuscripts(susType, phraseChunk0, phraseChunk1);
		if (newPhraseList != null) {
			PhraseChunk mainPhraseList = (SusType.SUPER.equals(susType)) ? phraseChunk1 : phraseChunk0;
			PhraseChunk minorPhraseList = (SusType.SUPER.equals(susType)) ? phraseChunk0 : phraseChunk1;
			merged = textChunk.replace(mainPhraseList, newPhraseList);
			textChunk.remove(minorPhraseList);
		}
		return merged;
	}

	public AbstractCMElement getPhraseListList() {
		return textChunk;
	}

	public void setPhraseListList(TextChunk textChunk) {
		this.textChunk = textChunk;
	}

	public double getMinSubFontRatio() {
		return minSubFontRatio;
	}

	public void setMinSubFontRatio(double minSubFontRatio) {
		this.minSubFontRatio = minSubFontRatio;
	}

	public double getMaxSubFontRatio() {
		return maxSubFontRatio;
	}

	public void setMaxSubFontRatio(double maxSubFontRatio) {
		this.maxSubFontRatio = maxSubFontRatio;
	}

	public double getMinSubOffsetRatio() {
		return minSubOffsetRatio;
	}

	public void setMinSubOffsetRatio(double minSubOffsetRatio) {
		this.minSubOffsetRatio = minSubOffsetRatio;
	}

	public double getMaxSubOffsetRatio() {
		return maxSubOffsetRatio;
	}

	public void setMaxSubOffsetRatio(double maxSubOffsetRatio) {
		this.maxSubOffsetRatio = maxSubOffsetRatio;
	}

	public double getMinSuperFontRatio() {
		return minSuperFontRatio;
	}

	public void setMinSuperFontRatio(double minSuperFontRatio) {
		this.minSuperFontRatio = minSuperFontRatio;
	}

	public double getMaxSuperFontRatio() {
		return maxSuperFontRatio;
	}

	public void setMaxSuperFontRatio(double maxSuperFontRatio) {
		this.maxSuperFontRatio = maxSuperFontRatio;
	}

	public double getMinSuperOffsetRatio() {
		return minSuperOffsetRatio;
	}

	public void setMinSuperOffsetRatio(double minSuperOffsetRatio) {
		this.minSuperOffsetRatio = minSuperOffsetRatio;
	}

	public double getMaxSuperOffsetRatio() {
		return maxSuperOffsetRatio;
	}

	public void setMaxSuperOffsetRatio(double maxSuperOffsetRatio) {
		this.maxSuperOffsetRatio = maxSuperOffsetRatio;
	}
}
