package org.contentmine.image.pixel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.contentmine.eucl.euclid.Int2;

public class PixelTriangle {

	private Pixel[] pixel = new Pixel[3];
	private PixelSet set = new PixelSet();
	private PixelIsland island;
	private List<Pixel> diagonal;

	PixelTriangle (Pixel pixel0, Pixel pixel1, Pixel pixel2, PixelIsland island) {
		this.island = island;
		this.pixel[0] = pixel0;
		this.pixel[1] = pixel1;
		this.pixel[2] = pixel2;
		set.addAll(Arrays.asList(pixel));
	}

	/** returns a Triangle if points for right-angled isosceles.
	 * 
	 * @param pixel0
	 * @param pixel1
	 * @param pixel2
	 * @param island
	 * @return null if not RH isosceles
	 */
	public static PixelTriangle createTriangle(Pixel pixel0, Pixel pixel1, Pixel pixel2, PixelIsland island) {
		PixelTriangle triangle = new PixelTriangle(pixel0, pixel1, pixel2, island);
		return triangle.createSet() ? triangle : null;
	}

	private boolean createSet() {
		boolean created = false;
		// we must set up neighbours
		PixelList neighbours0 = pixel[0].getOrCreateNeighbours(island);
		PixelList neighbours1 = pixel[1].getOrCreateNeighbours(island);
		PixelList neighbours2 = pixel[2].getOrCreateNeighbours(island);
		boolean n01 = neighbours0.contains(pixel[1]);
		boolean n02 = neighbours0.contains(pixel[2]);
		boolean n12 = neighbours1.contains(pixel[2]);
		if (n01 && n02 && n12) {
			set.addAll(Arrays.asList(pixel));
			created = true;
		}
		return created;
	}
	
	@Override 
	public boolean equals(Object o) {
		boolean equals = false;
		if (o != null && o instanceof PixelTriangle) {
			PixelTriangle triangle = (PixelTriangle) o;
			equals = set.equals(triangle.set);
		}
		return equals;
	}

	@Override
	public int hashCode() {
		return set.hashCode();
	}
	
	public PixelSet addAll(PixelTriangle triangle) {
		PixelSet union = new PixelSet(set);
		union.addAll(triangle.set);
		return union;
	}
	
	public PixelSet retainAll(PixelTriangle triangle) {
		PixelSet retained = new PixelSet(set);
		retained.retainAll(triangle.set);
		return retained;

	}
	
	/** looks for pixel whose neighbours are in x and y axial direction.
	 * 
	 * @return
	 */
	public Pixel findRightAnglePixel() {
		for (int i = 0; i < 3; i++) {
			Pixel pixeli = pixel[i];
			Pixel pixelj = pixel[(i+1)%3];
			Pixel pixelk = pixel[(i+2)%3];
			if (isTriangle(pixel[i], pixelj, pixelk)) {
				return pixel[i];
			}
			if (isTriangle(pixel[i], pixelk, pixelj)) {
				return pixel[i];
			}
		}
		return null;
	}
	
	private boolean isTriangle(Pixel pixel0, Pixel pixel1, Pixel pixel2) {
		Int2 i0 = pixel0.getInt2();
		Int2 i1 = pixel1.getInt2();
		Int2 i2 = pixel2.getInt2();
		Int2 d01 = i0.subtract(i1);
		Int2 d02 = i0.subtract(i2);
		int dot = d01.dotProduct(d02);
		return dot == 0;
	}

	public List<Pixel> getDiagonal() {
		diagonal = null;
		Pixel rightAngle = findRightAnglePixel();
		if (rightAngle != null) {
			diagonal = new ArrayList<Pixel>(Arrays.asList(set.toArray(new Pixel[0])));
			diagonal.remove(rightAngle);
		}
		return diagonal;
	}

	public void removeDiagonalNeighbours() {
		getDiagonal();
		Pixel pixel0 = diagonal.get(0);
		Pixel pixel1 = diagonal.get(1);
		pixel0.getOrCreateNeighbours(island).remove(pixel1);
		pixel1.getOrCreateNeighbours(island).remove(pixel0);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(pixel[0]);
		sb.append(" "+pixel[1]);
		sb.append(" "+pixel[2]);
		sb.append("; diag "+diagonal);
		return sb.toString();
	}
}
