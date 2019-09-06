package org.contentmine.image.pixel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Axis.Axis2;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.eucl.euclid.RealArithmeticProgression;
import org.contentmine.eucl.euclid.RealArray;

/** a container for PixelRingLists
 * 
 * @author pm286
 *
 */
public class LocalSummitList implements Iterable<PixelRingList> {
	private static final Logger LOG = Logger.getLogger(LocalSummitList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private List<PixelRingList> prlList;
	private PixelRingList localSummitList;
	private Real2Array centreArray;
	private RealArithmeticProgression xArithProg;
	private RealArithmeticProgression yArithProg;

	public LocalSummitList(List<PixelRingList> pixelRingListList) {
		this.prlList = pixelRingListList;
	}
	
	public LocalSummitList() {
	}

	public int size() {
		return prlList ==  null ? 0 : prlList.size();
	}

	@Override
	public Iterator<PixelRingList> iterator() {
		return prlList == null ? null : prlList.iterator();
	}

	public PixelRingList get(int i) {
		return prlList == null ? null : prlList.get(i);
	}

	public PixelRingList extractLocalSummits(int minNestedRings) {
		centreArray = new Real2Array();
		localSummitList = new PixelRingList();
		for (int isl = 0; isl < size(); isl++) {
			PixelRingList pixelRingListIsland = get(isl);
			int nestedRings = pixelRingListIsland.size();
			if (nestedRings > minNestedRings) {
				int ring = minNestedRings - 1;
				PixelRing pixelRing = pixelRingListIsland.get(ring);
				localSummitList.add(pixelRing);
				// lower nesting likely to contain isolated points
				IslandRingList islandRingList = IslandRingList.createFromPixelRing(pixelRing, null);
				for (PixelRing islandRing : islandRingList) {
					Real2 centreCoordinate = islandRing.getCentreCoordinate();
					centreArray.addElement(centreCoordinate);
				}
			}
		}
		centreArray.format(2);
//		RealArithmeticProgression yArithProg = getArithmeticProgression(Axis2.Y, 1.5);
//		IntArray intArray = yArithProg == null ? null : yArithProg.getIntArray();
//		LOG.debug("RealAP y: " + yArithProg + " " + intArray);
		
		/**
		totalSVG.appendChild(ringSVG.copy());
		SVGSVG.wrapAndWriteAsSVG(ringSVG, 
		new File(parentFile, baseName+"."+"ring." + isl + "." + ring + "." + CTree.SVG));
		SVGG totalSVG = new SVGG();
		SVGCircle circle = (SVGCircle) new SVGCircle(centreCoordinate, radius)
				.setStrokeWidth(0.7).setOpacity(0.4).setStroke("green");
		ringSVG.appendChild(circle);
		totalSVG.appendChild(circle.copy());
		File file = new File(parentFile, baseName+"."+"total" + "." + CTree.SVG);
		LOG.debug("total "+file+"\n"+centreArray+"\n");
		SVGSVG.wrapAndWriteAsSVG(totalSVG, 
			file);
	*/
		return localSummitList;
	}

	private RealArithmeticProgression getArithmeticProgression(Axis2 axis, double delta) {
		RealArithmeticProgression arithProg = null;
		RealArray array = axis == Axis2.X ? centreArray.getXArray() : centreArray.getYArray();
		if (array != null) {
			array.sortAscending();
			arithProg = RealArithmeticProgression.createAP(array, delta);
		}
		if (Axis2.X.equals(axis)) {
			xArithProg = arithProg;
		}
		if (Axis2.Y.equals(axis)) {
			yArithProg = arithProg;
		}
		return arithProg;
	}

	public void add(PixelRingList pixelRingList) {
		if (pixelRingList != null) {
			getOrCreatePixelRingListList();
			prlList.add(pixelRingList);
		}
	}

	public List<PixelRingList> getOrCreatePixelRingListList() {
		if (prlList == null) {
			prlList = new ArrayList<PixelRingList>();
		}
		return prlList;
	}

	public PixelRingList getLocalSummitList() {
		return localSummitList;
	}

	public void setLocalSummitList(PixelRingList localSummitList) {
		this.localSummitList = localSummitList;
	}

	/** may need to run extractLocalSummits(minNestedRings) */
	public Real2Array getCentreArray() {
		return centreArray;
	}

	public RealArithmeticProgression getxArithProg() {
		return xArithProg;
	}

	public RealArithmeticProgression getyArithProg() {
		return yArithProg;
	}

}
