package org.contentmine.image.pixel;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class PixelSet implements Set<Pixel> {

	private Set<Pixel> pixelSet;
	
	public PixelSet() {
		ensurePixelSet();
	}

	/** deep copy of set.
	 * 
	 * @param set
	 */
	public PixelSet(PixelSet set) {
		this.pixelSet = new LinkedHashSet<Pixel>(set.pixelSet);
	}
	
	/** create set with single pixel.
	 * 
	 * @param pixel
	 */
	public PixelSet(Pixel pixel) {
		this.pixelSet = new LinkedHashSet<Pixel>();
		pixelSet.add(pixel);
	}

	/** create set with list of pixels (not necessarily unique).
	 * 
	 * @param pixelList
	 */
	public PixelSet(PixelList pixelList) {
		this.pixelSet = new LinkedHashSet<Pixel>();
		for (Pixel pixel : pixelList) {
			add(pixel);
		}
	}

	private void ensurePixelSet() {
		if (pixelSet == null) {
			pixelSet = new LinkedHashSet<Pixel>(); 
		}
	}

	public boolean add(Pixel pixel) {
		ensurePixelSet();
		return pixelSet.add(pixel);
	}

	public boolean contains(Pixel pixel) {
		ensurePixelSet();
		return pixelSet.contains(pixel);
	}

	public int size() {
		ensurePixelSet();
		return pixelSet.size();
	}

	public boolean isEmpty() {
		ensurePixelSet();
		return pixelSet.isEmpty();
	}

	public Iterator<Pixel> iterator() {
		ensurePixelSet();
		return pixelSet.iterator();
	}

	public void remove(Pixel pixel) {
		ensurePixelSet();
		pixelSet.remove(pixel);
	}

	public void addAll(PixelList list) {
		ensurePixelSet();
		pixelSet.addAll(list.getList());
	}

	public boolean contains(Object o) {
		ensurePixelSet();
		return pixelSet.contains(o);
	}

	public Object[] toArray() {
		ensurePixelSet();
		return pixelSet.toArray();
	}

	public <T> T[] toArray(T[] a) {
		ensurePixelSet();
		return pixelSet.toArray(a);
	}

	public boolean remove(Object o) {
		ensurePixelSet();
		return pixelSet.remove(o);
	}

	public boolean containsAll(Collection<?> c) {
		ensurePixelSet();
		return pixelSet.containsAll(c);
	}

	public boolean addAll(Collection<? extends Pixel> c) {
		ensurePixelSet();
		return pixelSet.addAll(c);
	}

	public boolean removeAll(Collection<?> c) {
		ensurePixelSet();
		return pixelSet.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		ensurePixelSet();
		return pixelSet.retainAll(c);
	}

	public void clear() {
		ensurePixelSet();
		pixelSet.clear();
	}

	/** get next pixel.
	 * 
	 * creates an iterator for non-empty set and returns the next pixel
	 * 
	 * @return next pixel (order will not be reproducible)
	 */
	public Pixel next() {
		return (isEmpty()) ? null : iterator().next();
	}
	
	public String toString() {
		return pixelSet.toString();
	}

	public void removeAll(PixelList pixelList) {
		for (Pixel pixel : pixelList) {
			this.remove(pixel);
		}
	}
}
