package org.contentmine.image.pixel;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;

/**
 * connected Nodes.
 * 
 * @author pm286
 * 
 */
public class PixelNucleus implements Comparable<PixelNucleus> {

	private static final String END_STRING = "}";
	private static final String START_STRING = "{";
	private static Logger LOG = Logger.getLogger(PixelNucleus.class);

	public enum PixelJunctionType {
		CROSS,
		DOT,
		DOUBLEY,
		FILLEDT,
		NICKEDT,
		FIVEPIXEL,
		SIXPIXEL,
		SEVENPIXEL,
		EIGHTORMOREPIXEL,
		TERMINAL,
		Y, CYCLIC,
	}
	protected PixelIsland island;
	private Real2 centre;
	protected Pixel centrePixel;
	protected PixelList pixelList;
	private int rightAngleCorner;
	private PixelJunctionType junctionType;
	private PixelNode pixelNode;  // the node that coresponds to centrePixel
	// list of pixels protruding from nucleus // normally in edges
	// pixels can be removed from this list (but not from the nucleus)
	private PixelList spikePixelList;

	public PixelNucleus(PixelIsland island) {
		this.island = island;
		setDefaults();
	}

	/** for making subclassed Nucleus
	 * 
	 * @param centrePixel
	 * @param pixelList
	 * @param island
	 */
	protected PixelNucleus(Pixel centrePixel, PixelList pixelList, PixelIsland island) {
		this(island);
		this.centrePixel = centrePixel;
		this.pixelList = pixelList;
	}

	private void setDefaults() {
		this.rightAngleCorner = -1;
	}

	public boolean contains(PixelNode node) {
		return (this.pixelNode != null && pixelNode.equals(node));
	}

	/** size in pixels.
	 * 
	 * @return
	 */
	public int size() {
		return pixelList.size();
	}

	public static void drawNucleusSet(Set<PixelNucleus> nucleusSet, SVGG g,
			double rad) {
		for (PixelNucleus nucleus : nucleusSet) {
			nucleus.drawCentre(g, rad);
		}
	}

	private void drawCentre(SVGG g, double rad) {
		Real2 centre = this.getCentre();
		SVGCircle circle = new SVGCircle(centre.plus(new Real2(0.5, 0.5)), rad);
		circle.setOpacity(0.4);
		circle.setFill("magenta");
		g.appendChild(circle);
	}

	/** get coordinates of centre of nucleus.
	 * 
	 * @return
	 */
	public Real2 getCentre() {
		centre = null;
		if (centrePixel == null) {
			Real2Array real2Array = pixelList.getOrCreateReal2Array();
			centre = real2Array.getMean();
		} else {
			centre = new Real2(centrePixel.getInt2());
		}
		return centre;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(START_STRING);
		if (pixelNode != null) {
			sb.append(pixelNode);
		}
		if (centre != null) {
//			sb.append(centre + ";"); //  too complex at present
		}
		ensurePixelList();
		sb.append(pixelList.toString());
		sb.append(END_STRING);
		return sb.toString();
	}

//	public JunctionSet getJunctionSet() {
//		return junctionSet;
//	}

	private Pixel computeCentrePixel() {
		PixelList pixelList = island.getPixelList();
		if (pixelList.size() > 0) {
			double dist0 = Integer.MAX_VALUE;
			getCentre();
			for (Pixel pixel : pixelList) {
				double dist = centre.getDistance(new Real2(pixel.getInt2()));
				if (dist < dist0) {
					dist0 = dist;
					centrePixel = pixel;
				}
			}
		}
		return centrePixel;
	}
	
	public Pixel getCentrePixel() {
		if (centrePixel == null) {
			computeCentrePixel();
		}
		return centrePixel;
	}

	public void add(Pixel pixel) {
		ensurePixelList();
		if (!pixelList.contains(pixel)) {
			pixelList.add(pixel);
		}
	}

	private void ensurePixelList() {
		if (pixelList == null) {
			pixelList = new PixelList();
		}
	}

	public boolean canTouch(Pixel pix) {
		for (Pixel pixel : this.pixelList) {
			if (pixel.isDiagonalNeighbour(pix)
					|| pixel.isOrthogonalNeighbour(pix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * list of pixels which can be removed as part of superthinning of nuclei.
	 * 
	 * @return
	 */
	public PixelList getSuperthinRemovablePixelList() {
		PixelList removableList = new PixelList();
		for (Pixel pixel : pixelList) {
			if (pixel.isTjunctionCentre(island)) {
				removableList.add(pixel);
			} else {

			}
		}
		return removableList;
	}

	@Deprecated
	void doTJunctionThinning(PixelIsland island) {
		PixelList removables = getSuperthinRemovablePixelList();
		island.removePixels(removables);
	}

//	public boolean isDotJunction() {
//		if (getJunctionType() == null) {
//			if (pixelList.size() == 1) {
//				centrePixel = pixelList.get(0);
//				if (centrePixel.getOrthogonalNeighbours(island).size() 
//						+ centrePixel.getDiagonalNeighbours(island).size() == 0) {
//					setJunctionType(PixelJunctionType.DOT);
//				}
//			}
//		}
//		return PixelJunctionType.DOT.equals(getJunctionType());
//	}
//
//	public boolean isTerminalJunction() {
//		if (getJunctionType() == null) {
//			if (pixelList.size() == 1) {
//				centrePixel = pixelList.get(0);
//				if (centrePixel.getOrthogonalNeighbours(island).size() 
//						+ centrePixel.getDiagonalNeighbours(island).size() == 1) {
//					setJunctionType(PixelJunctionType.TERMINAL);
//					LOG.trace("Terminal centre: "+centrePixel+" ; "+centrePixel.getOrCreateNeighbours(island));
//				}
//			}
//		}
//		return PixelJunctionType.TERMINAL.equals(getJunctionType());
//	}
//
//	public boolean isCrossJunction() {
//		if (getJunctionType() == null) {
//			if (pixelList.size() == 5) {
//				int corner = getCrossCentre();
//				if (corner != -1) {
//					setJunctionType(PixelJunctionType.CROSS);
//				}
//			}
//		}
//		return PixelJunctionType.CROSS.equals(getJunctionType());
//	}
//
//	private int getCrossCentre() {
//		centrePixel = null;
//		int pixelNumber = -1;
//		for (int i = 0; i < 5; i++) {
//			Pixel pixel = pixelList.get(i);
//			if (pixel.getOrthogonalNeighbours(island).size() == 4) {
//				if (centrePixel != null) {
//					throw new RuntimeException("Bad cross: " + this);
//				}
//				centrePixel = pixel;
//				pixelNumber = i;
//			}
//		}
//		return pixelNumber;
//	}
//
//	public boolean isTJunction() {
//		if (getJunctionType() == null) {
//			if (pixelList.size() == 1) {
//				if(isNickedT()) {
//					setJunctionType(PixelJunctionType.NICKEDT); 
//				}
//			} else if (pixelList.size() == 4) {
//				if (isFilledT()) {
//					setJunctionType(PixelJunctionType.FILLEDT); 
//				}
//			}
//		}
//		return PixelJunctionType.NICKEDT.equals(getJunctionType()) ||
//				PixelJunctionType.FILLEDT.equals(getJunctionType());
//	}
//
//	/** symmetric about vertical stem.
//	 * 
//	 * @return
//	 */
//	private boolean isNickedT() {
//		boolean isT = false;
//		if (centrePixel == null) {
//			if (pixelList.size() == 1) {
//				centrePixel = pixelList.get(0);
//				LOG.trace("nicked T "+centrePixel);
//			}
//		}
//		if (centrePixel != null) {
//			PixelList diagonalNeighbours = centrePixel.getDiagonalNeighbours(island);
//			PixelList orthogonalNeighbours = centrePixel.getOrthogonalNeighbours(island);
//			if (diagonalNeighbours.size() == 2 && orthogonalNeighbours.size() == 1) {
//				isT = true;
//			}
//		}
//		return isT;
//	}
//
//	private boolean isFilledT() {
//		boolean isT = false;
//		centrePixel = null;
//		for (Pixel pixel : pixelList) {
//			if (pixel.getOrthogonalNeighbours(island).size() == 3) {
//				if (centrePixel != null) {
//					throw new RuntimeException("Not a filled TJunction "+this);
//				}
//				centrePixel = pixel;
//			}
//		}
//		if (centrePixel != null) {
//			isT = true;
//			LOG.debug("Filled T: "+centrePixel);
//		}
//		return isT;
//	}
//
//	/** 3 connected pixels in rightangle.
//	 * 
//	 * The first cross is the corner returned by getRightAngleCorner();
//	 * 
//	 * + +
//	 * +
//	 * 
//	 * @return
//	 */
//	public boolean isYJunction() {
//		if (getJunctionType() == null) {
//			if (pixelList.size() == 3) {
//				int corner = getRightAngleCorner();
//				if (corner != -1) {
//					centrePixel = pixelList.get(corner);
//					setJunctionType(PixelJunctionType.Y);
//				}
//			}
//		}
//		return PixelJunctionType.Y.equals(getJunctionType());
//	}
//
//	/** not expected.
//	 * 
//	 * Found one of form:
//	 * 
//	 *  + +
//	 * +++
//	 * 
//	 * don't understand it
//	 * 
//	 * @return
//	 */
//	public boolean isFivePixelJunction() {
//		if (getJunctionType() == null) {
//			if (pixelList.size() == 5) {
//				centrePixel = pixelList.getCentralPixel();
//				setJunctionType(PixelJunctionType.FIVEPIXEL);
//			}
//		}
//		return PixelJunctionType.FIVEPIXEL.equals(getJunctionType());
//	}
//
//
//	/** two rightangle triangles joined by tips.
//	 * 
//	 * The centre pixels will have 3 neighbours *in the nucleus*
//	 * The others will have only two *in the nucleus*
//	 * 
//	 * +
//	 * ++
//	 *   ++
//	 *    +
//	 * or
//	 * +
//	 * ++
//	 *   +
//	 *   ++
//	 * or
//	 * +
//	 * ++++
//	 *    +
//	 * or
//	 * +  +
//	 * ++++
//	 *    
//	 * or maybe others
//	 *   
//	 *   centre pixel will be randomly one of the two central pixels
//	 *   
//	 *   maybe all 6-connected should be this.
//	 *   
//	 *   They all seem fourway crossings
//	 *   
//	 * @return
//	 */
//	public boolean isSixPixelJunction() {
//		if (getJunctionType() == null) {
//			if (pixelList.size() == 6) {
//				centrePixel = pixelList.getCentralPixel();
//				setJunctionType(PixelJunctionType.SIXPIXEL);
//			}
//		}
//		return PixelJunctionType.SIXPIXEL.equals(getJunctionType());
//	}
//
//	/** hopefully rare
//	 * 
//	 * Found one of form:
//	 * 
//	 *    +
//	 *    ++
//	 * +++
//	 *  +
//	 * 
//	 * this is a T joined to a triangle. It looks like a fourway
//	 * 
//	 * @return
//	 */
//	public boolean isSevenPixelJunction() {
//		if (getJunctionType() == null) {
//			if (pixelList.size() == 7) {
//				centrePixel = pixelList.getCentralPixel();
//				setJunctionType(PixelJunctionType.SEVENPIXEL);
//			}
//		}
//		return PixelJunctionType.SEVENPIXEL.equals(getJunctionType());
//	}
//
//	/** not found yet
//	 * 
//	 * @return
//	 */
//	public boolean isEightOrMorePixelJunction() {
//		if (getJunctionType() == null) {
//			if (pixelList.size() >= 7) {
//				centrePixel = pixelList.getCentralPixel();
//				setJunctionType(PixelJunctionType.EIGHTORMOREPIXEL);
//			}
//		}
//		return PixelJunctionType.EIGHTORMOREPIXEL.equals(getJunctionType());
//	}
//
//
//	/** two diagonal Y's joined by stems.
//	 * 
//	 *  +
//	 * ++
//	 *   ++
//	 *   +
//	 *   
//	 *   centre pixel will be randomly one of the two central pixels
//	 * @return
//	 */
//	public boolean isDoubleYJunction() {
//		if (getJunctionType() == null) {
//			if (pixelList.size() == 6) {
//				PixelList centres = new PixelList();
//				for (Pixel pixel : pixelList) {
//					PixelList orthNeighbours = pixel.getOrthogonalNeighbours(island);
//					if (orthNeighbours.size() == 2 && 
//							orthNeighbours.get(0).isDiagonalNeighbour(orthNeighbours.get(1)))  {
//						centres.add(pixel);
//					}
//				}
//				if (centres.size() == 2 && centres.get(0).isDiagonalNeighbour(centres.get(1))) {
//					centrePixel = centres.get(0); // arbitrary but?
//					setJunctionType(PixelJunctionType.DOUBLEY);
//				}
//			}
//		}
//		return PixelJunctionType.DOUBLEY.equals(getJunctionType());
//	}

	@Deprecated
	public boolean rearrangeYJunction(PixelIsland island) {
		Pixel rightAnglePixel = pixelList.get(rightAngleCorner);
		int p0 = (rightAngleCorner + 1) % 3;
		Pixel startPixel0 = pixelList.get(p0);
		int p1 = (p0 + 1) % 3;
		Pixel startPixel1 = pixelList.get(p1);
		LOG.trace("pixel0 " + startPixel0 + " neighbours "
				+ startPixel0.getOrCreateNeighbours(island));
		LOG.trace("pixel1 " + startPixel1 + " neighbours "
				+ startPixel1.getOrCreateNeighbours(island));
		LOG.trace("right angle " + rightAnglePixel + " neighbours "
				+ rightAnglePixel.getOrCreateNeighbours(island));
		if (movePixel(startPixel0, startPixel1, rightAnglePixel, island)) {
			return true;
		}
		if (movePixel(startPixel1, startPixel0, rightAnglePixel, island)) {
			return true;
		}
		return false;
	}

	private boolean movePixel(Pixel pixel0, Pixel pixel1,
			Pixel rightAnglePixel, PixelIsland island) {
		Int2 right = rightAnglePixel.getInt2();
		Int2 p0 = pixel0.getInt2();
		Int2 p1 = pixel1.getInt2();
		Int2 vector = right.subtract(p0);
		Int2 new2 = p1.plus(vector);
		Pixel newPixel = new Pixel(new2.getX(), new2.getY());
		PixelList neighbours = newPixel.getOrCreateNeighbours(island);
		LOG.trace("new " + newPixel + "neighbours: " + neighbours);
		if (neighbours.size() != 3)
			return false; // we still have to remove old pixel

		PixelList oldNeighbours = pixel1.getOrCreateNeighbours(island); // before
																		// removal
		island.remove(pixel1);
		newPixel.clearNeighbours();
		island.addPixelAndComputeNeighbourNeighbours(newPixel);
		PixelList newPixelNeighbours = newPixel.getOrCreateNeighbours(island);
		LOG.trace("new " + newPixel + "neighbours: " + newPixelNeighbours);
		for (Pixel oldNeighbour : oldNeighbours) {
			oldNeighbour.clearNeighbours();
			PixelList oldNeighbourNeighbours = oldNeighbour
					.getOrCreateNeighbours(island);
			LOG.trace("old " + oldNeighbour + "neighbours: "
					+ oldNeighbourNeighbours);
		}
		return true;
	}

	public PixelList getPixelList() {
		ensurePixelList();
		return pixelList;
	}

	public PixelJunctionType getJunctionType() {
		return junctionType;
	}

	public void set(PixelNode node) {
		if (this.pixelNode != null) {
			throw new RuntimeException();
		}
		this.pixelNode = node;
	}

	public PixelNode getNode() {
		if (pixelNode == null) {
			getCentrePixel();
			if (centrePixel == null) {
				LOG.error("Null centre pixel ; pixelList "+pixelList);
				for (Pixel pixel : pixelList) {
					LOG.trace(pixel+"; "+pixel.getOrCreateNeighbours(island));
				}
			} else {
				pixelNode = new PixelNode(centrePixel, island);
			}
		}
		return pixelNode;
	}

	/** all non-nucleus pixels connected to nucleus pixels are added as neighbours.
	 * 
	 * This effectively creates a shell of linkable pixels suitable for edges.
	 * 
	 * @return
	 */
	public PixelList createSpikePixelList() {
		spikePixelList = new PixelList();
		for (Pixel pixel : pixelList) {
			PixelList neighbours = pixel.createNeighbourList(island);
			for (Pixel neighbour : neighbours) {
				if (!pixelList.contains(neighbour)) {
					spikePixelList.add(neighbour);
				}
			}
		}
		return spikePixelList;
	}

	/** get bounding box for nucleus.
	 * 
	 * @return bounding box (null if no pixels)
	 */
	public Int2Range getBoundingBox() {
		Int2Range bbox = null;
		PixelList pixelList = getPixelList();
		if (pixelList.size() > 0) {
			bbox = pixelList.getIntBoundingBox();
		}
		return bbox;
	}

	/** NYI
	 * 
	 * @param nucleusj
	 * @return
	 */
	PixelNucleus merge(PixelNucleus nucleus) {
		throw new RuntimeException("NYI");
	}

	/** adds all pixels.
	 * 
	 * pixelSet is not altered
	 * 
	 * @param pixelSet
	 */
	public void addAll(PixelSet pixelSet) {
		Iterator<Pixel> iterator = pixelSet.iterator();
		while (iterator.hasNext()) {
			this.add(iterator.next());
		}
	}

	public void setJunctionType(PixelJunctionType junctionType) {
		this.junctionType = junctionType;
	}

	public int compareTo(PixelNucleus nucleus) {
		if (centrePixel != null && nucleus.centrePixel != null) {
			return centrePixel.compareTo(nucleus.centrePixel);
		}
		return 0;
	}

	public void setPixelList(PixelList pixelList) {
		this.pixelList = pixelList;
	}


}
