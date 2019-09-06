package org.contentmine.svg2xml.page;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.text.structure.AbstractContainer;
import org.contentmine.svg2xml.pdf.ChunkId;

import nu.xom.Nodes;

/** 
 * Superclass of raw components of PDFPage SVG.
 * <p>
 * Components are:
 * <p>
 * FigureAnalyzer, ImageAnalyzer, ShapeAnalyzer, MixedAnalyzer, TextAnalyzer.
 * <p>
 * Each component can access the PageAnalyzer, and through that the PDFAnalyzer
 * for the document. Most analyzers have an AbstractContainer which processes the raw
 * SVG.
 * 
 * @author pm286
 */
public abstract class ChunkAnalyzer {
	
	private static final PrintStream SYSOUT = System.out;

	private final static Logger LOG = Logger.getLogger(ChunkAnalyzer.class);

	protected ChunkId chunkId;
	protected SVGElement svgChunk;
	protected PageAnalyzer pageAnalyzer;
	protected List<AbstractContainer> abstractContainerList;
	
	protected ChunkAnalyzer(PageAnalyzer pageAnalyzer) {
		this.pageAnalyzer = pageAnalyzer;
		throw new RuntimeException("CHECK NEVER USED");
	}
	
	public PageAnalyzer getPageAnalyzer() {
		return pageAnalyzer;
	}

	protected void setSVGChunk(SVGElement svgChunk) {
		this.svgChunk = svgChunk;
		getChunkId();
	}
	
	protected SVGElement getSVGChunk() {
		return svgChunk;
	}

	public List<AbstractContainer> createContainers() {
		throw new RuntimeException("Override for: "+getClass());
	}

	protected void ensureAbstractContainerList() {
		if (abstractContainerList == null) {
			abstractContainerList = new ArrayList<AbstractContainer>();
		}
	}

	public SVGG createChunkFromList(List<? extends SVGElement> svgElements) {
		SVGG g = new SVGG();
		for (int i = 0; i < svgElements.size(); i++) {
			AbstractCMElement element = svgElements.get(i);
			g.appendChild(element.copy());
		}
		String title = this.getClass().getName()+svgElements.size();
		g.setTitle(title);
		ChunkId chunkId = this.getChunkId();
		if (chunkId != null) {
			g.setId(this.getChunkId().toString());
		} else {
			LOG.trace("NULL chunkId");
		}
		return g;
	}

	public ChunkId getChunkId() {
		if (chunkId == null) {
			//String id = (svgg == null) ? null : svgg.getId();
			//String id = (svgElement == null) ? null : svgElement.getId();
			//if (id == null) {
			String id = null;
			if (svgChunk != null) {
				id = svgChunk.getId();
				if (id == null) {
					Nodes idNodes = svgChunk.query("ancestor::*/@id");
					id = (idNodes.size() == 0) ? null : idNodes.get(0).getValue();
					//SYSOUT.println(svgElement.toXML());
				}
			}
			//}
			chunkId = (id == null) ? null : new ChunkId(id);
		}
		return chunkId;
	}
	
	public void setChunkId(ChunkId chunkId) {
		this.chunkId = chunkId;
	}
	
	public void setChunkId(ChunkId chunkId, int subChunk) {
		if (chunkId != null) {
			ChunkId newChunkId = new ChunkId(chunkId);
			newChunkId.setSubChunkNumber(subChunk);
			this.setChunkId(newChunkId);
		}
	}
	

}
