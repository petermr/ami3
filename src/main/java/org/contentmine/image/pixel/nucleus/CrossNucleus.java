package org.contentmine.image.pixel.nucleus;

import org.contentmine.image.pixel.Pixel;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelList;
import org.contentmine.image.pixel.PixelNucleus;

public class CrossNucleus extends PixelNucleus {

	public CrossNucleus(Pixel centrePixel, PixelList pixelList, PixelIsland island) {
		super(centrePixel, pixelList, island);
	}

	public static int getCrossCentre(Pixel centrePixel, PixelList pixelList, PixelIsland island) {
		centrePixel = null;
		int pixelNumber = -1;
		for (int i = 0; i < 5; i++) {
			Pixel pixel = pixelList.get(i);
			if (pixel.getOrthogonalNeighbours(island).size() == 4) {
				if (centrePixel != null) {
					throw new RuntimeException("Bad cross: " + pixelList);
				}
				centrePixel = pixel;
				pixelNumber = i;
			}
		}
		return pixelNumber;
	}


}
