package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;

public class PageLeftSidebarCache extends PageComponentCache {
	private static final Logger LOG = Logger.getLogger(PageLeftSidebarCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static Double XMAX = 25.; // I think this is a good start
	private static String ORANGE = "orange";
	
	private Double xmax = null;
	
	public PageLeftSidebarCache(PageCache pageCache) {
		xmax = XMAX;
		setPageCache(pageCache);
		processCache();
	}

	private void processCache() {
		findLeftWhitespace();
		getOrCreateConvertedSVGElement();
		SVGSVG.wrapAndWriteAsSVG(convertedSVGElement, new File("target/debug/pageLeftSidebar"+pageCache.getSerialNumber()+".svg"));
	}

	private void findLeftWhitespace() {
		List<SVGElement> elements = SVGElement.extractSelfAndDescendantElements(pageCache.getInputSVGElement());
		getOrCreateAllElementList();

		for (SVGElement element : elements) {
			Real2Range bbox = element.getBoundingBox();
			if (bbox != null && bbox.getXMax() < xmax) {
//				String s = "";
//				if (element instanceof SVGText) {
//					s = ((SVGText) element).getText();
//				}
				this.allElementList.add(element);
			}
		}
		LOG.trace("LEFT "+allElementList.size());
	}
	
	public Double getXMax() {
		return xmax;
	}
	
	public void setXMax(Double xmax) {
		this.xmax = xmax;
		this.boundingBox = new Real2Range(new RealRange(0, this.xmax), 
				new RealRange(pageCache.getOrCreateHeaderCache().getYMax(), pageCache.getOrCreateFooterCache().getYMin()));
	}

	public String getFill() {
		return ORANGE;
	}

}
