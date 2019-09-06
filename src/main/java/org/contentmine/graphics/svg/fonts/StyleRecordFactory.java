package org.contentmine.graphics.svg.fonts;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGText;

/** creates StyleRecords and StyleRecordSets.
 * 
 * allows for control of fontNames, etc.
 * 
 * @author pm286
 *
 */
public class StyleRecordFactory {
	private static final Logger LOG = Logger.getLogger(StyleRecordFactory.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private StyleRecordSet styleRecordSet;
	private boolean normalizeFontNames;
	public StyleRecordFactory() {
		
	}
	
	public StyleRecordSet createStyleRecordSet(List<SVGText> texts) {
		styleRecordSet = null;
		if (texts != null) {
			styleRecordSet = new StyleRecordSet();
			styleRecordSet.setNormalizeFontNames(normalizeFontNames);
			for (SVGText text : texts) {
				styleRecordSet.getOrCreateStyleRecord(text);
			}
			if (normalizeFontNames) {
				styleRecordSet.normalizeFontNamesByStyleAndWeight();
				LOG.debug("normalize");
			}
		}
		return styleRecordSet;
	}

	public StyleRecordSet createStyleRecordSet(File svgFile) {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(svgFile);
		return createStyleRecordSet(svgElement);
	}

	public StyleRecordSet createStyleRecordSet(AbstractCMElement svgElement) {
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(svgElement);
		StyleRecordSet styleRecordSet = this.createStyleRecordSet(texts);
		return styleRecordSet;
	}

	public boolean isNormalizeFontNames() {
		return normalizeFontNames;
	}

	public void setNormalizeFontNames(boolean normalizeFontNames) {
		this.normalizeFontNames = normalizeFontNames;
	}

}
