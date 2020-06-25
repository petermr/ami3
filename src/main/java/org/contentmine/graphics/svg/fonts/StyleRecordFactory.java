package org.contentmine.graphics.svg.fonts;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
	private static final Logger LOG = LogManager.getLogger(StyleRecordFactory.class);
private StyledBoxRecordSet styleRecordSet;
	private boolean normalizeFontNames;
	private List<SVGText> inputTexts;
	
	public StyleRecordFactory() {
		
	}
	
	public StyledBoxRecordSet createStyleRecordSet(List<SVGText> texts) {
		styleRecordSet = null;
		this.inputTexts = texts;
		if (inputTexts != null) {
			styleRecordSet = new StyledBoxRecordSet();
			styleRecordSet.setNormalizeFontNames(normalizeFontNames);
			for (SVGText text : inputTexts) {
				styleRecordSet.getOrCreateStyleRecord(text);
			}
			if (normalizeFontNames) {
				styleRecordSet.normalizeFontNamesByStyleAndWeight();
				LOG.debug("normalize");
			}
		}
		return styleRecordSet;
	}

	public StyledBoxRecordSet createStyleRecordSet(File svgFile) {
		AbstractCMElement svgElement = SVGElement.readAndCreateSVG(svgFile);
		return createStyleRecordSet(svgElement);
	}

	public StyledBoxRecordSet createStyleRecordSet(AbstractCMElement svgElement) {
		List<SVGText> texts = SVGText.extractSelfAndDescendantTexts(svgElement);
		StyledBoxRecordSet styleRecordSet = this.createStyleRecordSet(texts);
		return styleRecordSet;
	}

	public boolean isNormalizeFontNames() {
		return normalizeFontNames;
	}

	public void setNormalizeFontNames(boolean normalizeFontNames) {
		this.normalizeFontNames = normalizeFontNames;
	}

}
