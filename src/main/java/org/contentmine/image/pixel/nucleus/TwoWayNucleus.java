package org.contentmine.image.pixel.nucleus;

import org.contentmine.image.pixel.Pixel;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelList;
import org.contentmine.image.pixel.PixelNucleus;

/** probably shouldn't exist.
 * 
 * shouldn't be created and should be flattenable.
 * 
 * @author pm286
 *
 */
public class TwoWayNucleus extends PixelNucleus {

	public TwoWayNucleus(Pixel centrePixel, PixelList pixelList, PixelIsland island) {
		super(centrePixel, pixelList, island);
	}
	
}
