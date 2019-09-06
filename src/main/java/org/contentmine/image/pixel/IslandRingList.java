package org.contentmine.image.pixel;

import java.util.List;
import java.util.Stack;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/** manages island subrings within a single PixelRing
 * 
 * @author pm286
 *
 */
public class IslandRingList extends PixelRingList {
	private static final Logger LOG = Logger.getLogger(IslandRingList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}
	
	public IslandRingList() {
		super();
	}

	public static IslandRingList createFromPixelRing(PixelRing originalPixelRing, PixelIsland island) {
		IslandRingList islandRingList = null;
		if (originalPixelRing != null) {
			islandRingList = new IslandRingList();
			PixelSet pixelSet = new PixelSet(originalPixelRing);
			while (!pixelSet.isEmpty()) {
				Stack<Pixel> pixelStack = new Stack<Pixel>();
				Pixel seedPixel = pixelSet.iterator().next();
				pixelStack.push(seedPixel);
				PixelRing pixelRing = new PixelRing();
				islandRingList.add(pixelRing);
				while (!pixelStack.isEmpty()) {
					Pixel pixel = pixelStack.pop();
					pixelSet.remove(pixel);
					pixelRing.add(pixel);
					PixelList neighbours = pixel.getOrCreateNeighboursIn(island, pixelSet);
					for (Pixel neighbour : neighbours) {
						pixelStack.add(neighbour);
					}
				}
			}
		}
		return islandRingList;
	}

}
