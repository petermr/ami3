package org.contentmine.image.pixel;

import org.apache.log4j.Logger;

/** coordination shell radiating from pixel or pixelList
 * 
 * @author pm286
 *
 */
public class PixelShell {

	private final static Logger LOG = Logger.getLogger(PixelShell.class);
	private PixelSet expandedShell;
	private PixelIsland island;
	private PixelSet seedSet = null;

	private PixelShell(PixelIsland island) {
		this.island = island;
		expandedShell = new PixelSet();
	}

	public PixelShell(Pixel pixel, PixelIsland island) {
		this(island);
		this.seedSet = new PixelSet(pixel);
		expandedShell.add(pixel);
	}

	public PixelShell(PixelList pixelList, PixelIsland island) {
		this(island);
		this.seedSet = new PixelSet(pixelList);
		expandedShell.addAll(pixelList);
	}

	public PixelShell(PixelList pixelList) {
		this(pixelList.getIsland());
		this.seedSet = new PixelSet(pixelList);
		expandedShell.addAll(pixelList);
	}

	public void expandOnePixelFromCurrent() {
		PixelList shellPixelList = new PixelList(expandedShell);
		for (Pixel pixel : shellPixelList) {
			PixelList neighbourList = pixel.getOrCreateNeighbours(island);
			for (Pixel neighbour  : neighbourList) {
				expandedShell.add(neighbour);
			}
			LOG.trace("expanded to "+expandedShell.size());
		}
	}

	public PixelSet getExpandedSetWithoutSeed() {
		PixelSet newSet = new PixelSet(expandedShell);
		newSet.removeAll(seedSet);
		return newSet;
	}

	public PixelSet getExpandedSet() {
		return expandedShell;
	}
	
	public String toString() {
		return expandedShell == null ? "" : expandedShell.toString();
	}
}