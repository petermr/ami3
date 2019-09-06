package org.contentmine.graphics.svg.cache;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.AbstractCMElement;
import org.contentmine.graphics.html.HtmlElement;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.plot.AbstractPlotBox;

/** superclass for caches.
 * 
 * @author pm286
 *
 */
public abstract class AbstractCache {
	
	private static final Logger LOG = Logger.getLogger(AbstractCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public static final double MARGIN = 1.0;
	
	protected Double axialEps = 0.1;
	protected Real2Range boundingBox;
	protected ComponentCache ownerComponentCache;
	protected Real2Range ownerComponentCacheBoundingBox;
	private AbstractPlotBox svgMediaBox;

	protected ShapeCache siblingShapeCache;
	protected TextCache siblingTextCache;

	protected SVGElement inputSVGElement;
	protected SVGElement convertedSVGElement;
	protected HtmlElement convertedHtmlElement;

	protected List<Class<?>> ignoreClassList;

	protected AbstractCache() {
		setDefaults();
	}
	
	private void setDefaults() {
		ignoreClassList = new ArrayList<Class<?>>();
		ignoreClassList.add(TextChunkCache.class); // probably not yet working
	}

	public AbstractCache(ComponentCache ownerComponentCache) {
		this();
		this.ownerComponentCache = ownerComponentCache;
		this.siblingShapeCache = ownerComponentCache == null ? null : ownerComponentCache.shapeCache;
		this.siblingTextCache = ownerComponentCache == null ? null : ownerComponentCache.textCache;
		getOrCreateElementList();
	}

	public AbstractCache(AbstractPlotBox svgMediaBox) {
		this();
		this.svgMediaBox = svgMediaBox;
	}

	protected void drawBox(AbstractCMElement g, String col, double width) {
		Real2Range box = this.getBoundingBox();
		if (box != null) {
			SVGRect boxRect = SVGRect.createFromReal2Range(box);
			boxRect.setStrokeWidth(width);
			boxRect.setStroke(col);
			boxRect.setOpacity(0.3);
			g.appendChild(boxRect);
		}
	}

	protected void writeDebug(String type, String outFilename, SVGG g) {
		File outFile = new File(outFilename);
		SVGSVG.wrapAndWriteAsSVG(g, outFile);
	}
	
	/** the bounding box of the cache
	 * 
	 * @return the bounding box of the containing svgCache (or null if none)
	 */
	public Real2Range getOrCreateComponentCacheBoundingBox() {
		if (ownerComponentCacheBoundingBox == null) {
			ownerComponentCacheBoundingBox = ownerComponentCache == null ? null : ownerComponentCache.getBoundingBox();
		}
		return ownerComponentCacheBoundingBox;
	}

	protected Real2Range getOrCreateBoundingBox(List<? extends SVGElement> elementList) {
		if (boundingBox == null) {
			boundingBox = (elementList == null || elementList.size() == 0) ? null :
			SVGElement.createBoundingBox(elementList);
		}
		return boundingBox;
	}

	/** the bounding box of the actual components
	 * The extent of the context (e.g. svgCache) may be larger
	 * @return the bounding box of the contained components
	 */
	public Real2Range getBoundingBox() {
		return getOrCreateBoundingBox(getOrCreateElementList());
	}
	
	public abstract List<? extends SVGElement> getOrCreateElementList();

	/** initial SVGElement.
	 * normally read in from other sources, including files or extraction
	 * from other elements. 
	 * @return
	 */
	public SVGElement getInputSVGElement() {
		return inputSVGElement;
	}

	/** SVGG containing (copies of) all elements after processing.
	 * 
	 * @return
	 */
	public SVGElement getOrCreateConvertedSVGElement() {
		if (convertedSVGElement == null) {
			convertedSVGElement = new SVGG();
			List<? extends SVGElement> elementList = getOrCreateElementList();
			if (elementList.size() == 0) {
				LOG.trace("Empty elementList");
			}
			for (AbstractCMElement component : elementList) {
				convertedSVGElement.appendChild(component.copy());
			}
		}
		return convertedSVGElement;
	}

	public ComponentCache getOwnerComponentCache() {
		return ownerComponentCache;
	}

	public boolean remove(AbstractCMElement element) {
		List<? extends SVGElement> elementList = this.getOrCreateElementList();
		boolean remove = elementList.remove(element);
		if (remove) {
			this.clearBoundingBoxToNull();
			if (ownerComponentCache != null) {
				ownerComponentCache.clearBoundingBoxToNull();
			}
		}
		return remove;
	}
	
	public boolean remove(List<? extends AbstractCMElement> elementList) {
		boolean remove = false;
		for (AbstractCMElement element : elementList) {
			remove |= remove(element);
		}
		return remove;
	}
	
	/** clears bounding box to null.
	 * required after changes to contentCaches
	 */
	public void clearBoundingBoxToNull() {
		this.boundingBox = null;
	}

	public void superClearAll() {
		boundingBox = null;
		ownerComponentCacheBoundingBox = null;
	}
	
	public abstract void clearAll();

	public void setSiblingShapeCache(ShapeCache shapeCache) {
		this.siblingShapeCache = shapeCache;
	}
}
