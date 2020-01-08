package org.contentmine.graphics.svg.cache;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
/**
 * creates a series of nested graphical objects formed from horizontal ad graphical lines
 * may become a Cache later.
 * 
 * Initial motivation to detect axial boxes in a page
 * 
 * @author pm286
 *
 */
public class LineBoxTree extends AbstractCache {
	private static final Logger LOG = Logger.getLogger(LineBoxTree.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public LineBoxTree(SVGElement svgElement) {
//		this.svgElement = svgElement;
	}

	@Override
	public List<? extends SVGElement> getOrCreateElementList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearAll() {
		// TODO Auto-generated method stub
		
	}


}
