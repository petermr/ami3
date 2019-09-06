package org.contentmine.image.pixel;

import java.io.File;
import java.util.Stack;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.graphics.svg.SVGSVG;
/** a ring of pixels around another ring or point.
 * 
 * @author pm286
 *
 */
public class PixelRing extends PixelList {
	private static final Logger LOG = Logger.getLogger(PixelRing.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public PixelRing() {
	}
	
	public PixelRing(PixelList pixelList) {
		super();
		super.list = pixelList.getList();
	}

	
	public PixelRing getPixelsTouching(PixelRing pixelRing) {
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
	public PixelRing expandRingOutside(PixelRing innerRing) {
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
		SVGSVG.wrapAndWriteAsSVG(pixelIsland.createSVG(), new File("target/pixels/island.svg"));
		while (pixelSet.size() > 0) {
			Pixel pixel = pixelSet.next();
			PixelRing island = createRing(pixel, pixelIsland);
			islandRingList.add(island);
			pixelSet.removeAll(island);
		}
		return islandRingList;
	}

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

}
