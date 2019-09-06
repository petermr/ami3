package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGSVG;

public class PageColumnCache extends PageComponentCache {
	private static final Logger LOG = Logger.getLogger(PageColumnCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private static Double YPAGE_MAX = 800.;
	private static Double YMIN = YPAGE_MAX - 100.; // I think this is a good start
	private static String BLUE = "blue";
	
	private Double ymin = null;
	
	public PageColumnCache(PageCache pageCache) {
		ymin = YMIN;
		setPageCache(pageCache);
		processCache();
	}

	private void processCache() {
		findBottomWhitespace();
		getOrCreateConvertedSVGElement();
		SVGSVG.wrapAndWriteAsSVG(convertedSVGElement, new File("target/debug/pageFooter"+pageCache.getSerialNumber()+".svg"));
	}

	private void findBottomWhitespace() {
		List<SVGElement> elements = SVGElement.extractSelfAndDescendantElements(pageCache.getInputSVGElement());
		getOrCreateAllElementList();

		for (SVGElement element : elements) {
			Real2Range bbox = element.getBoundingBox();
			if (bbox != null && bbox.getYMin() > ymin) {
				this.allElementList.add(element);
			}
		}
		LOG.trace("BOTTOM "+allElementList.size());
	}
	
	public Double getYMin() {
		return ymin;
	}
	
	public void setYMin(Double ymin) {
		this.ymin = ymin;
		this.boundingBox = new Real2Range(new RealRange(0, PageCache.DEFAULT_XMAX), new RealRange(ymin, PageCache.DEFAULT_YMAX));
	}

	public String getFill() {
		return BLUE;
	}

}
