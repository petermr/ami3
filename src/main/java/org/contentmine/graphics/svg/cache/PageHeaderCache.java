package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;

public class PageHeaderCache extends PageComponentCache {
	private static final Logger LOG = Logger.getLogger(PageHeaderCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static Double YMAX = 100.; // I think this is a good start
	private static String RED = "red";
	
	private Double ymax = null;
	
	public PageHeaderCache(PageCache pageCache) {
		ymax = YMAX;
		setPageCache(pageCache);
		processCache();
	}

	private void processCache() {
		findTopWhitespace();
		getOrCreateConvertedSVGElement();
		SVGSVG.wrapAndWriteAsSVG(convertedSVGElement, new File("target/debug/pageHeader"+pageCache.getSerialNumber()+".svg"));
	}

	private void findTopWhitespace() {
		List<SVGElement> elements = SVGElement.extractSelfAndDescendantElements(pageCache.getInputSVGElement());
		getOrCreateAllElementList();

		for (SVGElement element : elements) {
			Real2Range bbox = element.getBoundingBox();
			if (bbox != null && bbox.getYMax() < ymax) {
				this.allElementList.add(element);
			}
		}
		LOG.trace("TOP "+allElementList.size());
	}
	
	public Double getYMax() {
		return ymax;
	}
	
	public void setYMax(Double ymax) {
		this.ymax = ymax;
		this.boundingBox = new Real2Range(new RealRange(0, PageCache.DEFAULT_XMAX), new RealRange(0, ymax));
	}

	public String getFill() {
		return RED;
	}

}
