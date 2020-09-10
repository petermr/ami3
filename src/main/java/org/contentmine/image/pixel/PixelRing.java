package org.contentmine.image.pixel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.contentmine.eucl.euclid.Int2;
import org.contentmine.eucl.euclid.Int2Range;
import org.contentmine.eucl.euclid.IntArray;
import org.contentmine.eucl.euclid.IntSquareMatrix;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.eucl.euclid.Real2Array;
import org.contentmine.graphics.svg.SVGCircle;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGLine;
import org.contentmine.graphics.svg.SVGSVG;
import org.contentmine.graphics.svg.SVGText;
/** a ring of pixels around another ring or point.
 * 
 * @author pm286
 *
 */
public class PixelRing extends PixelList {
	private static final Logger LOG = LogManager.getLogger(PixelRing.class);
	public static List<String> COLORS8 = new ArrayList<>(
		    Arrays.asList("red", "orange", "green", "pink", "cyan", "blue", "brown", "magenta")
		);
	public static final int SIZE8 = COLORS8.size();
	public static final File PIXEL_TARGET = new File("target/pixels/");
	/** these keep us on the outside.
	 * take left right-angle before knights move
	 */
	List<Int2> DIRECTION_LIST = Arrays.asList(
		new Int2(-1, 0), // left
		new Int2( 0, 1),  // ahead
		new Int2(-1, 1), // ahead, left
		new Int2( 1, 0),  // right
		new Int2( 1, 1)   // ahead, right
		);


	public PixelRing() {
	}
	
	public PixelRing(PixelList pixelList) {
		super();
		super.list = pixelList.getList();
	}

	
	public PixelRing getPixelsTouching(PixelList pixelRing) {
		PixelList touchingPixels = null;
		if (pixelRing != null) {
			touchingPixels = super.getPixelsTouching(pixelRing);
		}
		return new PixelRing(touchingPixels);
	}

	/** grows a new ring "outside" this.
	 * currently developed for nested pixel rings
	 * experimental
	 * 
	 * In principle we could determine the outside by sectioning, but here we assume an onion
	 * ring structure with innerRingList representing the inside
	 * the ouside is simply "not innerRingList" - it need not be whitespace
	 * 
	 * @param innerRingList
	 * @return
	 */
	public PixelRing expandRingOutside(PixelList innerRing) {
		PixelIsland island = this.getIsland();
		PixelRing newRing = new PixelRing();
		for (Pixel node : this) {
			PixelList pixelList = node.getOrCreateNeighbours(island);
			for (Pixel pixel : pixelList) {
				if (this.contains(pixel)) {
					LOG.trace("skip this");
				} else if (innerRing.contains(pixel)) {
					LOG.trace("skip inner");
				} else {
					LOG.trace("adding "+pixel);
					newRing.add(pixel);
				}
			}
		}
		return newRing;
	}

	public IslandRingList getIslandRings() {
		IslandRingList islandRingList = new IslandRingList();
		PixelSet pixelSet = new PixelSet(this);
		PixelIsland pixelIsland = PixelIsland.createSeparateIslandWithClonedPixels(this, true);
		SVGSVG.wrapAndWriteAsSVG(pixelIsland.createSVG(), new File(PIXEL_TARGET,"island.svg"));
		while (pixelSet.size() > 0) {
			Pixel pixel = pixelSet.next();
			PixelRing island = createRing(pixel, pixelIsland);
			islandRingList.add(island);
			pixelSet.removeAll(island);
		}
		return islandRingList;
	}
	
	public void displayAndSortRing(int isl) {
		double mind = 1.5;
		Real2Array coords = getOrCreateReal2Array();
		int size = coords.size();
		SVGG gg = new SVGG();
		int col = 0;
		Real2 lineStartXY = null;
		SVGLine line = null;
		int start = -1;
		double fontSize = 0.5;
		for (int ii = 0; ii < size; ii++) {
			Real2 precedeXY = coords.get((ii-1 + size) % size);
			Real2 currentXY = coords.get(ii);
			if (lineStartXY == null) {
				lineStartXY = currentXY;
				start = ii;
				double strokeWidth = 0.4;
				String stroke = "gray";
				double rad = strokeWidth * 1.8;
				line = (SVGLine) new SVGLine(precedeXY, currentXY).setStroke(stroke).setStrokeWidth(strokeWidth).setOpacity(0.5);
				gg.appendChild(line);
				addAnnotatedCircle(gg, fontSize, currentXY, stroke, rad, ""+ii, 0.4);
			} else {
				double lastd = currentXY.getDistance(precedeXY);
				if (lastd > mind) {
					int nsteps = ii - start;
					LOG.trace("break " + lastd + "; " + nsteps+" "+start+"=>"+ii);
					double strokeWidth = 0.1 * (1 + Math.log(nsteps));
					String stroke = COLORS8.get(col);
					line = (SVGLine) new SVGLine(lineStartXY, currentXY).setStroke(stroke).setStrokeWidth(strokeWidth).setOpacity(0.5);
					gg.appendChild(line);
					SVGText.addCoordText(gg, fontSize, currentXY, "");
					double rad = lastd > mind ? 0.4 : 0.2;
					addAnnotatedCircle(gg, fontSize, currentXY, stroke, rad, ""+ii, 0.4);
					lineStartXY = null;
					col = (col + 1) % SIZE8;
				}
			}
			double rad = 0.1;
			SVGCircle c = (SVGCircle) new SVGCircle(currentXY, rad).setFill("gray");
			gg.appendChild(c);
		}
		SVGSVG.wrapAndWriteAsSVG(gg, new File("target/islands/distance"+"."+isl+".svg"));
	}
	
	
	// ============== private ===========

	/** extracted next ring from pixelIsland and removes from island
	 * not yet tested
	 * 
	 * @param seedPixel
	 * @param pixelIsland
	 * @return
	 */
	private PixelRing createRing(Pixel seedPixel, PixelIsland pixelIsland) {
		PixelSet usedPixelSet = new PixelSet();
		PixelRing pixelRing = new PixelRing();
		Stack<Pixel> stack = new Stack<Pixel>();
		stack.add(seedPixel);
		while (!stack.empty()) {
			PixelSet pixelSet = new PixelSet(pixelIsland.pixelList);
			Pixel pixel = stack.pop();
			pixelRing.add(pixel);
			usedPixelSet.add(pixel);
			PixelList neighbourList = pixel.getOrCreateNeighboursIn(pixelIsland, pixelSet);
			for (Pixel neighbour : neighbourList) {
				if (!usedPixelSet.contains(neighbour)) {
					stack.add(neighbour);
				}
			}
		}
		return pixelRing;
	}

	/**
	 * 
	 * @param g object containing circle and annotation, Created if null
	 * @param fontSize
	 * @param currentXY
	 * @param stroke
	 * @param rad
	 * @param msg
	 * @param opacity
	 * @return SVGG 
	 */
	public static SVGG addAnnotatedCircle(
			SVGG g, double fontSize, Real2 currentXY, String stroke, double rad, String msg, double opacity) {
		if (g == null) {
			g = new SVGG();
		}
		SVGCircle c = (SVGCircle) new SVGCircle(currentXY, rad).setFill(stroke).setOpacity(opacity);
		g.appendChild(c);
		SVGText.addCoordText(g, fontSize, currentXY, msg);
		return g;
	}

//	@Deprecated
//	public PixelList createSortedRing() {
//
//		int size = this.size();
//		Set<Pixel> unusedSet = new HashSet<>();
//		unusedSet.addAll(list);
//		List<PixelList> newListList = new ArrayList<>();
//		PixelList newList = null;
//		for (int i = 0; i < size; i++) {
//			if (newList == null) {
//				newList = new PixelList();
//				newListList.add(newList);
//			}
//			Pixel current = this.get(i);
//			newList.add(current);
//			unusedSet.remove(current);
//			PixelList neighbours = current.getOrCreateNeighbours(island);
//			int neighbourCount = getUnusedNeighbours(unusedSet, neighbours);
//			if (neighbourCount < 1) {
//				System.out.println("broke at " + current);
//				newList = null;
//			} else {
//			}
//		}
//		return null; 
//	}
		
	/** assumes this is a ring. 
	 * starts at arbitrary point. 
	 * 
	 * Will have problems if there are gaps, etc.
	 * 
	 * @return a geometrically sorted rings
	 */
	public PixelRing createSortedRing() {
		int size = this.size();
		Set<Pixel> unusedSet = new HashSet<>();
		unusedSet.addAll(list);
		PixelRing sortedRing = new PixelRing();
		Set<Pixel> multipleSet = getMultiplyConnectedPixels();
		if (multipleSet.size() %2 != 0) {
			LOG.warn("Odd number of multiple pixels: ");
		}
		Set<Pixel> chainBreakSet = getChainBreakingPixels();
		if (chainBreakSet.size() > 0) {
			throw new RuntimeException("some 1-connected pixels: "+chainBreakSet);
		}
		
		Pixel start = getFirst2ConnectedPixel(multipleSet);
		unusedSet.remove(start);
		addToRingAndRemoveFromUnused(unusedSet, sortedRing, start);
		Pixel current = start;
		Pixel last = null;
		while (!unusedSet.isEmpty()) {
			PixelList neighbours = current.getNeighboursInSet(unusedSet);
			if (neighbours.size() == 0) {
				if (unusedSet.isEmpty()) {
					LOG.debug("no unused left, FINISH!");
					break; // from while
				} else {
					throw new RuntimeException("No neighbours for " + current + "; unused "+unusedSet.size());
				}
			} else if (neighbours.size() == 1) {
				// normal chain propagation
				Pixel next = neighbours.get(0);
				addToRingAndRemoveFromUnused(unusedSet, sortedRing, next);
				current = next;
				System.out.print(".");
			} else if (last == null && neighbours.size() == 2) { 
				// first pixel
				Pixel next = neighbours.get(0);
				addToRingAndRemoveFromUnused(unusedSet, sortedRing, next);
				current = next;
			} else if (neighbours.size() > 2) {
				throw new RuntimeException("Cannot process more than single branch "+current);
			} else {
				// branch
				if (!multipleSet.contains(current)) {
					throw new RuntimeException(" should be multiple "+current);
				}
				current = addTriangle(unusedSet, sortedRing, multipleSet, current, neighbours);
				if (current == null) {
					throw new RuntimeException("cannot add triangle ");
				}
			}
			last = current;
		}
		LOG.debug("finished loop");
		return sortedRing;
	}

	/** assumes this is a ring. 
	 * uses a "force-based" method
	 * we are going clockwise round outside of ring. Always try to "bend to the left" where possible.
	 * 
	 * 
	 * tracks the outer pixels by always tending "outwards"
	 * 
	 * @return a geometrically sorted rings
	 */
	public PixelRing createSortedRing3() {
		int size = this.size();
		Set<Pixel> unusedSet = new HashSet<>();
		unusedSet.addAll(list);
		PixelRing sortedRing = new PixelRing();
		// start outside and approach until we hit a pixel.
		List<Pixel> pixel2 = find2Pixel();
		Pixel current = pixel2.get(1);
		// approximate a tangent as if we had arrived from the ring
		Pixel last = new Pixel(current.getInt2().getX(), current.getInt2().getY() + 1);
		Set<Pixel> ringSet = new HashSet<>(this.list);
		while (true) {
			Pixel next = nextPixel(current, last, ringSet);
			if (next == null) break;
			Int2 vector = next.getInt2().subtract(current.getInt2());
			if (!vector.isAxial()) {
				vector = rotpi4(vector);
			}
		}
		
		
//		
//		Pixel start = getFirst2ConnectedPixel(multipleSet);
//		unusedSet.remove(start);
//		addToRingAndRemoveFromUnused(unusedSet, sortedRing, start);
//		Pixel current = start;
//		Pixel last = null;
//		while (!unusedSet.isEmpty()) {
//			PixelList neighbours = current.getNeighboursInSet(unusedSet);
//			if (neighbours.size() == 0) {
//				if (unusedSet.isEmpty()) {
//					LOG.debug("no unused left, FINISH!");
//					break; // from while
//				} else {
//					throw new RuntimeException("No neighbours for " + current + "; unused "+unusedSet.size());
//				}
//			} else if (neighbours.size() == 1) {
//				// normal chain propagation
//				Pixel next = neighbours.get(0);
//				addToRingAndRemoveFromUnused(unusedSet, sortedRing, next);
//				current = next;
//				System.out.print(".");
//			} else if (last == null && neighbours.size() == 2) { 
//				// first pixel
//				Pixel next = neighbours.get(0);
//				addToRingAndRemoveFromUnused(unusedSet, sortedRing, next);
//				current = next;
//			} else if (neighbours.size() > 2) {
//				throw new RuntimeException("Cannot process more than single branch "+current);
//			} else {
//				// branch
//				if (!multipleSet.contains(current)) {
//					throw new RuntimeException(" should be multiple "+current);
//				}
//				current = addTriangle(unusedSet, sortedRing, multipleSet, current, neighbours);
//				if (current == null) {
//					throw new RuntimeException("cannot add triangle ");
//				}
//			}
//			last = current;
//		}
		LOG.debug("finished loop");
		return sortedRing;
	}

	
	
			

	/** transforms an axial vector by rotating pi/4 and shrinking by 1/sqrt(2)
	 * e.g (1, -1) => (0, -1)
	 * 
	 * @param vector
	 * @return
	 */
	private Int2 rotpi4(Int2 vector) {
		Int2 transformed = null;
		IntSquareMatrix rotmat = new IntSquareMatrix(new int[][] {
			new int[] {1, -1},
			new int[] {1,  1}
		});
		if (vector != null && vector.isAxial()) {
			IntArray array = rotmat.multiply(vector.toIntArray());
			transformed = new Int2(array.elementAt(0), array.elementAt(1));
		}
		return transformed;
	}

	private Pixel nextPixel(Pixel current, Pixel last, Set<Pixel> ringSet) {
		PixelList neighbours = current.getNeighboursInSet(ringSet);
		
		return null;
	}

	/** find 2 pixels, pixel0 just outside the ring and pixel1 in it
	 * 
	 * @return
	 */
	private List<Pixel> find2Pixel() {
		Int2Range bbox = this.getIntBoundingBox();
		int x0 = bbox.getXMin() - 5;
		int xmax = bbox.getXMax() + 5;
		int y0 = bbox.getYRange().getMidPoint();
		List<Pixel> pixel2 = new ArrayList<>();
		Pixel last = null;
		Pixel current = null;
		for (int x = x0; x < xmax; x++) {
			Int2 xy = new Int2(x, y0);
			current = new Pixel(xy);
			if (this.contains(current)) {
				System.out.println("In Ring "+current);
				break;
			}
			last = current;
		}
		if (last == null) {
			throw new RuntimeException("could not find pixel ring");
		}
		pixel2.add(last);
		pixel2.add(current);
		return pixel2;
	}
	

	/** when ring goes round corners we get a triangle with a diagonal
	 * 
	 * @param unusedSet
	 * @param sortedRing
	 * @param multipleSet
	 * @param current
	 * @param neighbours
	 * @return
	 */
	private Pixel addTriangle(Set<Pixel> unusedSet, PixelRing sortedRing, 
			Set<Pixel> multipleSet, Pixel current, PixelList neighbours) {
		LOG.trace("triangle current , neighbours "+current+" "+neighbours);
		// does it have a single branching neighbours?
		Pixel multipleNeighbour = null;
		Pixel singleNeighbour = null;
		LOG.trace("current " + multipleSet.contains(current));
		
		for (Pixel neighbour : neighbours) {
			LOG.trace("triangle neighbour "+neighbour+" "+neighbour.getOrCreateNeighbours(island));
			if (multipleSet.contains(neighbour)) {
				multipleNeighbour = neighbour;
			} else {
				if (singleNeighbour != null) {
					throw new RuntimeException("No branched neighbours " +current);
				}
				singleNeighbour = neighbour;
			}
		}
		if (singleNeighbour == null) {
			int diagonalIndex = current.isDiagonalNeighbour(neighbours.get(0)) ? 1 : 0;
			addToRingAndRemoveFromUnused(unusedSet, sortedRing, neighbours.get(diagonalIndex));
			addToRingAndRemoveFromUnused(unusedSet, sortedRing, neighbours.get(1 - diagonalIndex));
		} else if (multipleNeighbour != null) {
			addToRingAndRemoveFromUnused(unusedSet, sortedRing, singleNeighbour);
			addToRingAndRemoveFromUnused(unusedSet, sortedRing, multipleNeighbour);
		}
		
		return multipleNeighbour;
	}

	private Set<Pixel> getMultiplyConnectedPixels() {
		Set<Pixel> multipleSet = new HashSet<>();
		for (Pixel pixel : this) {
			PixelList allNeighbours = pixel.getOrCreateNeighbours(island);
			Set<Pixel> neighboursInRing = new HashSet<>();
			for (Pixel neighbour : allNeighbours) {
				if (this.contains(neighbour)) {
					neighboursInRing.add(neighbour);
				}
			}
			if (neighboursInRing.size() > 2) {
				multipleSet.add(pixel);
			}
			
		}
		SVGG g = new SVGG();
		for (Pixel pixel : multipleSet) {
			g.appendChild(pixel.getSVGRect());
		}
		SVGSVG.wrapAndWriteAsSVG(g, new File(PIXEL_TARGET, "multiple.svg"));
		return multipleSet;
	}

	/** get pixels with 0 or 1 connections
	 * 
	 * @return
	 */
	private Set<Pixel> getChainBreakingPixels() {
		Set<Pixel> unringSet = new HashSet<>();
		for (Pixel pixel : this) {
			if (pixel.getOrCreateNeighbours(island).size() < 2) {
				unringSet.add(pixel);
			}
		}
		return unringSet;
	}

	private void addToRingAndRemoveFromUnused(Set<Pixel> unusedSet, PixelRing sortedRing, Pixel next) {
		unusedSet.remove(next);
		sortedRing.add(next);
	}

	

}
