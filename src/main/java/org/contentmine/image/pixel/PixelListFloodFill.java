package org.contentmine.image.pixel;

import java.awt.Point;

import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.Int2Range;

/** used for filling a PixelList exterior and interior.
 * 
 * @author pm286
 *
 */
public class PixelListFloodFill extends FloodFill {

	private final static Logger LOG = Logger.getLogger(PixelListFloodFill.class);
	
	private PixelList pixelList;
	private int xMin;
	private int yMin;
	private boolean inverted;

	public PixelListFloodFill(PixelList pixelList) {
		super(0, 0);
		this.pixelList = pixelList;
		setUp();
	}
	
	/** writes a white boundary round each edge to help floodfill.
	 * 
	 * @param pixelList TODO
	 * @return
	 */
	private void setUp() {
		Int2Range int2bBox = pixelList.getIntBoundingBox();
		try {
			xMin = int2bBox.getXRange().getMin() - 1;
			yMin = int2bBox.getYRange().getMin() - 1;
			// the 1 is the fencepost; 2 is for the new borders
			width = int2bBox.getXRange().getRange() + 1 + 2;
			height = int2bBox.getYRange().getRange() + 1 + 2;
		} catch (NullPointerException e) {
			
		}
	}
	
	protected boolean isBlack(int posX, int posY) {
		boolean inList = pixelList.contains(new Pixel(posX + xMin, posY + yMin));
		return (inverted ? !inList : inList);
	}

	public PixelList createInteriorPixelList() {
		inverted = true;
		boolean oldDiagonal = diagonal;
		diagonal = false;
		painted = new boolean[height][width];
		addNextUnpaintedBlack(0, 0);
		PixelList filledList = new PixelList();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				Pixel pixel = new Pixel(i + xMin, j + yMin);
				if (!painted[j][i] && !pixelList.contains(pixel)) {
					filledList.add(pixel);
				}
			}
		}
		diagonal = oldDiagonal;
		return filledList;
	}
	
	@Override
	protected Pixel getPixelFromPoint(Point p) {
		return new Pixel(p.x + xMin, p.y + yMin);
	}

}