package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;

public class PageRightSidebarCache extends PageComponentCache {
	private static final Logger LOG = Logger.getLogger(PageRightSidebarCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static Double XPAGE_MAX = 600.;
	private static Double XMIN = XPAGE_MAX - 25.; // I think this is a good start
	private static String ORANGE = "orange";

	private Double xmin = null;
	
	public PageRightSidebarCache(PageCache pageCache) {
		xmin = XMIN;
		setPageCache(pageCache);
		processCache();
	}

	private void processCache() {
		findBottomWhitespace();
		getOrCreateConvertedSVGElement();
		SVGSVG.wrapAndWriteAsSVG(convertedSVGElement, new File("target/debug/pageRightSidebar"+pageCache.getSerialNumber()+".svg"));
	}

	private void findBottomWhitespace() {
		List<SVGElement> elements = SVGElement.extractSelfAndDescendantElements(pageCache.getInputSVGElement());
		getOrCreateAllElementList();

		for (SVGElement element : elements) {
			Real2Range bbox = element.getBoundingBox();
			if (bbox != null && bbox.getXMin() > xmin) {
				this.allElementList.add(element);
			}
		}
		LOG.trace("RIGHT "+allElementList.size());
	}
	
	public Double getXMin() {
		return xmin;
	}
	
	public void setXMin(Double xmin) {
		this.xmin = xmin;
		this.boundingBox = new Real2Range(new RealRange(this.xmin, PageCache.DEFAULT_XMAX), 
			new RealRange(pageCache.getOrCreateHeaderCache().getYMax(), pageCache.getOrCreateFooterCache().getYMin()));
	}

	public String getFill() {
		return ORANGE;
	}


}
