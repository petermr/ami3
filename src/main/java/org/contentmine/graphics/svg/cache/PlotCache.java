package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGLine.LineDirection;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.plot.AbstractPlotBox;
/**
 * creates a series of nested graphical objects formed from horizontal ad graphical lines
 * may become a Cache later.
 * 
 * Initial motivation to detect axial boxes in a page
 * 
 * @author pm286
 *
 */
public class PlotCache extends ComponentCache {
	private static final Logger LOG = LogManager.getLogger(PlotCache.class);
private List<AbstractPlotBox> plotBoxList;
	private List<SVGElement> elementList;
	
	public PlotCache() {
		super();
		init();
	}
	
	private void init() {
		plotBoxList = new ArrayList<>();
	}

	@Override
	public List<? extends SVGElement> getOrCreateElementList() {
		if (elementList == null) {
			elementList = new ArrayList<SVGElement>();
			LOG.debug("getOrCreateElementList NYI");
//			elementList.addAll(plotBoxList);
			
		}
		return elementList;
	}

	@Override
	public void clearAll() {
		throw new RuntimeException("NYI");
	}


	/* cached boundingBox.
	 * The bbox may be reset 
	 * 
	 */
	public Real2Range getBoundingBox() {
		LOG.debug("NYI");
		// there's a bug here
		return boundingBox;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String s = sb.toString();
		return s;
		
	}

	public List<? extends AbstractPlotBox> getOrCreatePlotBoxList() {
		if (plotBoxList == null) {
			plotBoxList = new ArrayList<>();
		}
		return plotBoxList;
	}
	
}
