package org.contentmine.image.slice;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.Real2;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;

/** ordered ranges of contigous pixes at given X value of image.
 * 
 * @author pm286
 *
 */
public class XSlice {

	private List<IntRange> ranges;
	private int x;
	private int current0;
	private int current1;
	private XSlice lastSlice;
	private static int yminTotal;
	private static int ymaxTotal;

	public XSlice(int x) {
		ranges = new ArrayList<IntRange>();
		this.x = x;
	}
	public void addRange(IntRange range) {
		ranges.add(range);
	}
	
	public SVGG getSVGG() {
		SVGG g = null;
		if (size() > 0) {
			g = new SVGG();
			for (IntRange range : ranges) {
				SVGRect rect = new SVGRect(new Real2(x, range.getMin()), new Real2(x+1, range.getMax()));
				g.appendChild(rect);
			}
		}
		return g;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (IntRange range : ranges) {
			sb.append(range.getMin()+"("+range.getRange()+") ");
		}
		return sb.toString();
	}
	public int size() {
		return ranges.size();
	}
	
	public List<Integer> getOverlapList(XSlice lastSlice) {
		this.lastSlice = lastSlice;
		List<Integer> overlapList = new ArrayList<Integer>();
		current0 = 0;
		current1 = 0;
		while (current0 < this.ranges.size() && current1 < lastSlice.ranges.size()) {
			Integer overlap = catchupAndOverlap(false, lastSlice);
			if (overlap == null) {
				overlap = catchupAndOverlap(true, lastSlice);
			}
			if (overlap != null) {
				overlapList.add(overlap);
			}
		}
		return overlapList;
	}
	
	private Integer catchupAndOverlap(boolean flip, XSlice lastSlice) {
		List<IntRange> ranges1 = this.ranges;
		int pointer1 = current1;
		List<IntRange> ranges0 = lastSlice.ranges;
		int pointer0 = current0;
		Integer overlap = null;
		if (flip) {
			ranges0 = this.ranges;
			pointer0 = current1;
			ranges1 = lastSlice.ranges;
			pointer1 = current0;
		}
		while (pointer0 < ranges0.size() && pointer1 < ranges1.size()) {
			if (ranges0.get(pointer0).getMax() < ranges1.get(pointer1).getMin()) {
				pointer0++;
			} else {
				overlap = null;
				if (ranges0.get(pointer0).getMin() > ranges1.get(pointer1).getMax()) {
					
				} else {
					overlap = (ranges0.get(pointer0).getMin() + ranges1.get(pointer1).getMax()) / 2; 
				}
				break;
			}
		}
		return overlap;

	}
	public static int getYminTotal() {
		return yminTotal;
	}
	public static int getYmaxTotal() {
		return ymaxTotal;
	}
	public IntRange get(int j) {
		return (j < 0 || j >= ranges.size()) ? null : ranges.get(j); 
	}
	
	public static XSlice getBinarySlice(BufferedImage image, int x) {
		int current = 0;
		XSlice slice = new XSlice(x);
		int rangeYMin = -1;
		int y = 0;
		yminTotal = -1;
		ymaxTotal = -1;
		for (; y < image.getHeight(); y++) {
			int rgb = image.getRGB(x, y);
			// black
			if (rgb ==  0xff000000) {
				if (current == 0) {
					rangeYMin = y;
					if (yminTotal == -1) {
						yminTotal = rangeYMin;
					}
				}
				current = 1;
				ymaxTotal = y;
			// white
			} else if (rgb == 0xffffffff) {
				slice.addClosedRange(current, rangeYMin, y);
				current = 0;
			} else {
				System.out.print(Integer.toHexString(rgb)+" "+x+" "+y+" "+rgb);
			}
		}
		slice.addClosedRange(current, rangeYMin, y - 1);
		return slice;
	}

	private void addClosedRange(int current, int ymin, int y) {
		if (current == 1) {
			this.addRange(new IntRange(ymin, y));
		}
	}


}
