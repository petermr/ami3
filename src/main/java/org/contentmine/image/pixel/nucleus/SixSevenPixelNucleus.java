package org.contentmine.image.pixel.nucleus;

import org.contentmine.image.pixel.Pixel;
import org.contentmine.image.pixel.PixelIsland;
import org.contentmine.image.pixel.PixelList;
import org.contentmine.image.pixel.PixelNucleus;


/** two rightangle triangles joined by tips.
 * 
 * The centre pixels will have 3 neighbours *in the nucleus*
 * The others will have only two *in the nucleus*
 * 
 * +
 * ++
 *   ++
 *    +
 * or
 * +
 * ++
 *   +
 *   ++
 * or
 * +
 * ++++
 *    +
 * or
 * +  +
 * ++++
 *    
 * or maybe others
 *   
 *   centre pixel will be randomly one of the two central pixels
 *   
 *   maybe all 6-connected should be this.
 *   
 *   They all seem fourway crossings
 *   
 *   7-pixel	hopefully rare
 * 
 * Found one of form:
 * 
 *    +
 *    ++
 * +++
 *  +
 * 
 * this is a T joined to a triangle. It looks like a fourway
 * 
 * @return
 */


public class SixSevenPixelNucleus extends PixelNucleus {

	public SixSevenPixelNucleus(Pixel centrePixel, PixelList pixelList, PixelIsland island) {
		super(centrePixel, pixelList, island);
	}
	
}
