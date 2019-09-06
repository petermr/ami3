package org.contentmine.graphics.svg.layout;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.StyleBundle;

/** selects an SVGElement based on its attributes:
 * style (fontName, fontSize, fill, stroke, etc.)
 * xy position (bounding box)
 * relative position (after/before in reading order, etc.)
 * 
 * @author pm286
 *
 */
public class ElementSelector {
	private static final Logger LOG = Logger.getLogger(ElementSelector.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public final static String LEVEL = "level";
	
	private StyleBundle selectorStyleBundle;
	private Real2Range bbox;
	private double fontSizeToleranceFactor;
	private double strokeWidthToleranceFactor;
	private SVGPath templatePath;
	private SVGText templateText;

	private boolean enforceStrokeWidth;
	private boolean enforceFontSize;
	private boolean enforceStyle;
	private boolean enforceWeight;

	public ElementSelector() {
		setDefaults();
	}
	
	private void setDefaults() {
		strokeWidthToleranceFactor = 0.98;
		fontSizeToleranceFactor = 0.98;
		enforceFontSize = true;
		enforceStyle = false;
		enforceWeight = true;
	}
	
	public ElementSelector(SVGText templateText) {
		this();
		this.templateText = templateText;
		selectorStyleBundle = templateText.getStyleBundle();
		if (selectorStyleBundle == null) {
			LOG.debug("Null selectorStyleBundle: "+templateText.toXML());			
		}
	}

	public ElementSelector(SVGPath templatePath) {
		this();
		this.templatePath = templatePath;
		selectorStyleBundle = templatePath.getStyleBundle();
		if (selectorStyleBundle == null) {
			LOG.debug("Null selectorStyleBundle: "+templatePath.toXML());			
		}
	}

	public boolean matches(SVGText extractedText) {
		StyleBundle extractedBundle = extractedText.getStyleBundle();
		// hardcoded at present
		if (selectorStyleBundle == null) {
			LOG.debug("null selector bundle");
			return false;
		}
		if (enforceWeight && !selectorStyleBundle.matchesFontWeight(extractedBundle)) {
			LOG.trace("fails weight");
			return false;
		}
		if (enforceStyle && !selectorStyleBundle.matchesFontStyle(extractedBundle)) {
			LOG.trace("fails style");
			return false;
		}
		if (enforceFontSize && !selectorStyleBundle.matchesFontSize(extractedBundle, fontSizeToleranceFactor)) {
			LOG.trace("fails size");
			return false;
		}
		return true;
	}

	public boolean matches(SVGPath extractedPath) {
		StyleBundle extractedBundle = extractedPath.getStyleBundle();
		// hardcoded at present
		if (selectorStyleBundle == null) {
			LOG.debug("null selector bundle");
			return false;
		}
		// add more here
		if (enforceStrokeWidth && !selectorStyleBundle.matchesStrokeWidth(extractedBundle, strokeWidthToleranceFactor)) {
			LOG.trace("fails width");
			return false;
		}
		if (!matches(templatePath, extractedPath)) {
			return false;
		}
		return true;
	}

	private boolean matches(SVGPath templatePath, SVGPath extractedPath) {
		boolean matches = false;
		Real2Range templateBox = templatePath.getBoundingBox().format(1);
		Real2Range extractedBox = extractedPath.getBoundingBox().format(1);
//		LOG.debug("BOXES: "+templateBox+" ?= "+extractedBox);
		matches = templateBox.includes(extractedBox);
		if (matches) {
			// FIXME need stuff here
//			LOG.debug("BOXES: "+templateBox+" ?= "+extractedBox);
		}
		return matches;
	}

	public DocumentChunk createSectionHead(SVGText extractedText) {
		return extractedText == null ? null : new DocumentChunk(extractedText);
	}

	DocumentChunk createAnnotatedSectionHead(SVGText templateText, SVGText extractedText) {
		DocumentChunk sectionHead = null;
		if (matches(extractedText)) {
			String svgClassName = templateText.getSVGClassNameString();
			if (svgClassName == null) {
				throw new RuntimeException("missing classname: "+templateText.toXML());
			}
			extractedText.setSVGClassName(svgClassName);
			String fill = templateText.getAttributeValue(AbstractPubstyle.NEW_FILL);
			if (fill != null) {
				extractedText.setFill(fill);
			}
			sectionHead = createSectionHead(extractedText);
		}
		return sectionHead;
	}
	
	public DocumentChunk createSectionHead(SVGPath extractedPath) {
		return extractedPath == null ? null : new DocumentChunk(extractedPath);
	}

	DocumentChunk createDocumentChunk(SVGPath templatePath, SVGPath extractedPath) {
		DocumentChunk sectionHead = null;
		if (matches(extractedPath)) {
			String svgClassName = templatePath.getSVGClassNameString();
			if (svgClassName == null) {
				throw new RuntimeException("missing classname: "+templatePath.toXML());
			}
			extractedPath.setSVGClassName(svgClassName);
			String fill = templatePath.getAttributeValue(AbstractPubstyle.NEW_FILL);
			if (fill != null) {
				extractedPath.setFill(fill);
			}
			sectionHead = createSectionHead(extractedPath);
		}
		return sectionHead;
	}
	

}
