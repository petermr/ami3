package org.contentmine.graphics.svg.cache;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.graphics.svg.SVGElement;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.objects.ContentBoxGrid;
import org.contentmine.graphics.svg.objects.SVGContentBox;
import org.contentmine.graphics.svg.text.build.TextChunk;
import org.contentmine.graphics.svg.text.build.TextChunkList;

public class ContentBoxCache extends AbstractCache {

	static final Logger LOG = Logger.getLogger(ContentBoxCache.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private RectCache rectCache;
	private TextChunkCache textChunkCache;
	private List<SVGContentBox> contentBoxList;
	private ContentBoxGrid contentBoxGrid;

	public ContentBoxCache() {
	}
	
	public ContentBoxCache(ComponentCache containingComponentCache) {
		super(containingComponentCache);
		this.rectCache = containingComponentCache.getOrCreateRectCache();
		// FIXME
//		LOG.error("FIXME TextChunkCache NYI in ContentBoxCache");
//		throw new RuntimeException("TextChunkCache NYI");
//		this.textChunkCache = containingComponentCache.getOrCreateTextChunkCache();
	}

	public List<SVGContentBox> getOrCreateContentBoxList() {
		if (contentBoxList == null) {
			contentBoxList = new ArrayList<SVGContentBox>();
		}
		return contentBoxList;
	}

	public List<? extends SVGElement> getOrCreateElementList() {
		return getOrCreateContentBoxList();
	}

	/** nxm operation - slow can be be optimised by using sorted y coords.
	 * 
	 * @param rectCache rectCache
	 * @param textChunkCache textChunkCache
	 * @return
	 */
	public static ContentBoxCache createCache(RectCache rectCache, TextChunkCache textChunkCache) {
		ContentBoxCache contentBoxCache = null;
		if (rectCache != null && textChunkCache != null) {
			contentBoxCache = new ContentBoxCache(rectCache.getOwnerComponentCache());
			SVGElement.setBoundingBoxCached(rectCache.getOrCreateRectList(), true);
			textChunkCache.setBoundingBoxCached(true);
			contentBoxCache.createContentBoxList(rectCache, textChunkCache);
		}
		return contentBoxCache;
	}

	/** crude at first - does not check that rects do not overlap.
	 * 
	 * @param rectCache
	 * @param textChunkCache
	 * @return
	 */
	private List<SVGContentBox> createContentBoxList(RectCache rectCache, TextChunkCache textChunkCache) {
		List<SVGRect> rectList = rectCache.getOrCreateRectList();
		TextChunkList textChunkList = textChunkCache.getOrCreateTextChunkList();
		Real2Range ownerBBox = getOwnerComponentCache().getBoundingBox();
		LOG.trace("own "+ownerBBox);
		contentBoxList = new ArrayList<SVGContentBox>();
		// does not detach used phrases so a possibility of duplicates
		for (int irect = 0; irect < rectList.size(); irect++) {
			SVGRect rect = rectList.get(irect);
			Real2Range rectBox = rect.getBoundingBox();
			if (rectBox.isEqualTo(ownerBBox, AbstractCache.MARGIN)) {
				LOG.info("Omitted box surrounding ownerCache area");
			} else {
				LOG.trace("RECTBOX "+irect+"; "+rectBox);
				SVGContentBox contentBox = new SVGContentBox(rect);
				// there might be several text chunks in a box
				for (TextChunk textChunk : textChunkList) {
					contentBox.addContainedElements(textChunk);
				}
				if (contentBox.size() > 0) {
					LOG.trace("CB "+contentBox.toString());
					contentBoxList.add(contentBox);
				}
			}
		}
		return contentBoxList;
	}

	public ContentBoxGrid getOrCreateContentBoxGrid() {
		if (contentBoxGrid == null) {
			List<SVGContentBox> contentBoxList = getOrCreateContentBoxList();
			List<SVGRect> rectList = new ArrayList<SVGRect>();
			for (SVGContentBox contentBox : contentBoxList) {
				rectList.add(contentBox.getRect());
			}
			contentBoxGrid = new ContentBoxGrid();
			contentBoxGrid.add(rectList);
		}
		return contentBoxGrid;
	}

	public String toString() {
		String s = ""
				+ "rect: "+rectCache+"\n"
				+ "text: "+textChunkCache+"\n"
				+ "contentBoxList: "+contentBoxList.size()+"; "+contentBoxList+"\n"
				+ "contentBoxGrid: "+contentBoxGrid+"\n";
		return s;
	}

	@Override
	public void clearAll() {
		superClearAll();
		contentBoxList = null;
		contentBoxGrid = null;
	}

}
