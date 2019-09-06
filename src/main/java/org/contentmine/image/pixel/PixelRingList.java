package org.contentmine.image.pixel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGG;

/** a list of (generally nested) PixelRings
 * generally created elsewhere (PixelIsland)
 * 
 * @author pm286
 *
 */
public class PixelRingList implements Iterable<PixelRing> {

	private final Logger LOG = Logger.getLogger(PixelRingList.class);
	
	public static final String[] DEFAULT_COLOURS = {"red", "cyan", "orange", "green", "magenta", "blue"};
	private List<PixelRing> ringList;
	private PixelIsland island; // not used at present, but could be useful in future
	private PixelRing outline;
	
	/** generally should not be used to create PixelRingList. Create a PixelIsland and then
	 * PixelIsland.createSeparateIslandWithClonedPixels()
	 */
	public PixelRingList() {
		init();
	}

	private void init() {
		ringList = new ArrayList<PixelRing>();
	}
	
	public PixelRingList(Collection<PixelRing> pixelCollection) {
		init();
		ringList.addAll(pixelCollection);
	}

	public Iterator<PixelRing> iterator() {
		return ringList.iterator();
	}
	
	public PixelRing get(int i) {
		return (ringList == null || i < 0 || i >= ringList.size()) ? null : ringList.get(i);
	}
	
	public void add(PixelRing pixelRing) {
		if (ringList == null) {
			init();
		}
		ringList.add(pixelRing);
	}
	
	public List<PixelRing> getList() {
		return ringList;
	}

	public int size() {
		return ringList == null ? 0 : ringList.size();
	}

	public void addAll(PixelRingList RingList) {
		this.ringList.addAll(RingList.getList());
	}

	public boolean contains(PixelList pixelList) {
		return ringList != null && ringList.contains(pixelList);
	}

	public boolean remove(PixelList pixelList) {
		if (ringList != null) {
			return ringList.remove(pixelList);
		}
		return false;
	}

	/** plots rings in different colours
	 * 
	 * cycles through the colours if not enough.
	 * 
	 * @param gg SVGG to which everything is drawn; if null create one
	 * @param fill list of colours; if null, defaults are used
	 */
	public SVGG plotPixels(SVGG gg, String[] fill) {
		if (gg == null) {
			gg = new SVGG();
		}
		if (fill == null) {
			fill = DEFAULT_COLOURS;
		}
		int i = 0;
		
		for (PixelRing pixelRing : this) {
			SVGG g = pixelRing.plotPixels(fill[i]);
			gg.appendChild(g);
			i = (i + 1) % fill.length;
		}
		return gg;
	}
	
	/** plot rings with defaults
	 * useful for debugging
	 */
	public SVGG plotPixels() {
		return plotPixels(null, null);
	}

	/** remove islands with only a few pixels (likely to be artefacts)
	 * 
	 * @param size max size of islands to remove
	 * 
	 */
	public void removeMinorIslands(int size) {
		for (int ring = 0; ring < ringList.size(); ring++) {
			PixelRing pixelRing = ringList.get(ring);
			// make copy of ring as island to isolate the ring
			PixelIsland newIsland = PixelIsland.createSeparateIslandWithClonedPixels(pixelRing, true);
			int oldSize = newIsland.size();
			newIsland.removeMinorIslands(size);
			int newSize = newIsland.size();
			// if it's changed swap the old pixels for the new
			if (newSize != oldSize) {
				ringList.set(ring, new PixelRing(newIsland.getPixelList()));
				LOG.trace("island size after "+newIsland.size());
			}
		}
	}

	public void setIsland(PixelIsland pixelIsland) {
		this.island = pixelIsland;
	}

	public void plotRings(SVGG g, String[] colours) {
		for (int i = 0; i < size(); i++) {
			get(i).plotPixels(g, colours[i % colours.length]);
		}
	}

	public SVGG getGraphEdges(PixelIsland island, String[] fill) {
		SVGG g = new SVGG();
		PixelList outline;
		if (size() > 1) {
			outline = get(0).getPixelsWithOrthogonalContactsTo(get(1), island);
			outline.plotPixels(g, fill[0]);
			PixelIsland outlineIsland = PixelIsland.createSeparateIslandWithClonedPixels(outline, true);
			PixelGraph graph = PixelGraph.createGraph(outlineIsland);
//			PixelNodeList nodeList = graph.getNodeList();
			PixelEdgeList edgeList = graph.getOrCreateEdgeList();
			for (PixelEdge edge : edgeList) {
				PixelSegmentList segmentList = edge.getOrCreateSegmentList(2);
				g.appendChild(segmentList.getOrCreateSVG());
			}
		}
		return g;
	}

	/** gets the pixels touching between ring0 and ring1.
	 * NOT fully described or tested.
	 * used in plotOutline() and PixelIsland.plotIsland()
	 * 
	 * @return
	 */
	@Deprecated //"not fully described"
	public PixelRing getOrCreateOutline() {
		if (outline == null) {
			if (size() > 1) {
				outline = get(1).getPixelsTouching(get(0));
			}
		}
		return outline;
	}
	
	@Deprecated //"not fully described"
	public SVGG plotOutline(String colour) {
		SVGG g = null;
		outline = getOrCreateOutline();
		outline.plotPixels(g, colour);
		return g;
	}

	public PixelRing getOuterPixelRing() {
		return get(0);
	}
	
	public PixelRing getRing(int i) {
		PixelRing ring1 = get(i);
		PixelRing ring0 = get(i-1);
		PixelRing ring = ring0 == null ? ring1 : ring1.getPixelsTouching(ring0);
		return ring;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("rings: "+ringList.size()+"\n");
		for (PixelRing ring : this) {
			sb.append(" "+ring.size()+"\n");
		}
		return sb.toString();
	}

	public PixelRing getInnermostRing() {
		return ringList.get(ringList.size() - 1);
	}

	/**
	 * 
	 * @return mean of coordinates of innermost ring.
	 * 
	 */
	public Real2 getInnermostCentreCoordinate() {
		PixelRing innermostRing = getInnermostRing();
		return innermostRing == null ? null : innermostRing.getCentreCoordinate();
	}
	
	/** create local summits from pixelRings
	 * The normal pixelRing extraction is essetially the whole "shoreline" of the
	 * contour. This method finds the centroids of the new islands at this contour level,
	 * 
	 * @param pixelRingListList list of PixelRingLists from different islands
	 * @param minNestedRings lower limit for creating local summits
	 * @return
	 */

	public Real2Array extractLocalSummits1(LocalSummitList pixelRingListList, int minNestedRings) {
		Real2Array localSummitCoordinates = new Real2Array();
		for (PixelRingList pixelRingListIsland : pixelRingListList) {
			int nestedRings = pixelRingListIsland.size();
			if (nestedRings > minNestedRings) {
				Real2Array coordinateArray = null;
				for (int iring = 0; iring < nestedRings; iring++) {
					PixelRing pixelRing = pixelRingListIsland.get(iring);
					// lower nesting likely to contain isolated points
					if (iring == minNestedRings - 1) {
						coordinateArray = processLowestSignificantRing( pixelRing);
						localSummitCoordinates.addAll(coordinateArray);
					}
				}
			}
		}
		return localSummitCoordinates;
	}

	private static Real2Array processLowestSignificantRing(PixelRing pixelRing) {
		Real2Array coordinateArray = new Real2Array();
		IslandRingList islandRingList = IslandRingList.createFromPixelRing(pixelRing, null);
		for (PixelRing islandRing : islandRingList) {
			Real2 centreCoordinate = islandRing.getCentreCoordinate();
			coordinateArray.addElement(centreCoordinate);
		}
		return coordinateArray;
	}
	

}
