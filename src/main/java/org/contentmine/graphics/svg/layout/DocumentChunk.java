package org.contentmine.graphics.svg.layout;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGPath;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;

public class DocumentChunk extends SVGG {
	private static final Logger LOG = Logger.getLogger(DocumentChunk.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	public final static String LEVEL = "level";

	private Integer level;
//	private SVGText text;

	public DocumentChunk() {
		super();
		this.setSVGClassName("documentChunk");
		getLevel();
	}
	public DocumentChunk(SVGPath path) {
		this();
		SVGPath newPath = (SVGPath) path.copy();
		this.appendChild(newPath);
//		LOG.debug("=>>"+this.toXML());
		SVGSVG.wrapAndWriteAsSVG(this, new File("target/pubstyle/documentChunk/path.svg"));
	}
	
	public DocumentChunk(SVGText text) {
		this();
		SVGText newText = (SVGText) text.copy();
		this.appendChild(newText);
	}
	
	public Integer getLevel() {
		String ll = this.getAttributeValue(LEVEL);
		Integer level = null;
		if (ll != null) {
			try {
				level = new Integer(Integer.parseInt(ll));
			} catch (Exception nfe) {
				throw new RuntimeException("failed to parse level as integer: "+ll);
			}
		}
		return level;
	}
	
	
}
