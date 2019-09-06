package org.contentmine.eucl.euclid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.svg.SVGG;
import org.contentmine.graphics.svg.SVGRect;
import org.contentmine.graphics.svg.SVGText;
import org.contentmine.graphics.svg.linestuff.BoundingBoxManager;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class Real2RangeList extends BoundingBoxManager implements Iterable<Real2Range>, Collection<Real2Range> {
	private static final Logger LOG = Logger.getLogger(Real2RangeList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private Multiset<Integer> heightSet;
	private Multiset<Integer> widthSet;
	
	private List<String> strokeList;
	private double strokeWidth;
	private boolean addNumbers;
	
	public Real2RangeList() {
		super();
		setDefaults();
	}

	private void setDefaults() {
		strokeList = new ArrayList<String>();
		strokeList.add("red");
		strokeWidth = 1.5;
		addNumbers = false;
	}

	@Override
	public boolean isEmpty() {
		return getOrCreateBBoxList().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return getOrCreateBBoxList().contains(o);
	}

	@Override
	public Object[] toArray() {
		return getOrCreateBBoxList().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return getOrCreateBBoxList().toArray(a);
	}

	@Override
	public boolean remove(Object o) {
		return getOrCreateBBoxList().remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return getOrCreateBBoxList().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Real2Range> c) {
		return getOrCreateBBoxList().addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return getOrCreateBBoxList().removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return getOrCreateBBoxList().retainAll(c);
	}

	@Override
	public void clear() {
		getOrCreateBBoxList().clear();
	}

	@Override
	public Iterator<Real2Range> iterator() {
		return getOrCreateBBoxList().iterator();
	}

	public SVGG createSVG() {
		getOrCreateBBoxList();
		SVGG g = new SVGG();
		for (int i = 0; i < bboxList.size(); i++) {
			Real2Range bbox = bboxList.get(i);
			SVGRect rect = SVGRect.createFromReal2Range(bbox);
			rect.setFill("none");
			rect.setStrokeWidth(strokeWidth);
			rect.setStroke(strokeList.get(i % strokeList.size()));
			if (addNumbers) {
				SVGText text = SVGText.createDefaultText(bbox.getLLURCorners()[1], ""+i, 8, "blue");
				g.appendChild(text);
			}
			g.appendChild(rect);
		}
		return g;
	}

	public List<String> getStrokeList() {
		return strokeList;
	}

	public void setStrokeList(List<String> strokeList) {
		this.strokeList = strokeList;
	}

	public double getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public boolean isAddNumbers() {
		return addNumbers;
	}

	public void setAddNumbers(boolean addNumbers) {
		this.addNumbers = addNumbers;
	}

	public Integer getCommonestIntegerHeight() {
		getOrCreateIntegerHeightSet();
		return (Integer) MultisetUtil.getCommonestValue(heightSet);
	}

	private Multiset<Integer> getOrCreateIntegerHeightSet() {
		if (heightSet == null) {
			heightSet = HashMultiset.create();
			for (Real2Range bbox : this) {
				Integer height = (int) Util.format(bbox.getHeight(), 0);
				heightSet.add(height);
			}
		}
		return heightSet;
	}

	public Integer getCommonestWidth() {
		getOrCreateIntegerWidthSet();
		return (Integer) MultisetUtil.getCommonestValue(widthSet);
	}

	private Multiset<Integer> getOrCreateIntegerWidthSet() {
		if (widthSet == null) {
			widthSet = HashMultiset.create();
			for (Real2Range bbox : this) {
				Integer width = (int) Util.format(bbox.getWidth(), 0);
				heightSet.add(width);
			}
		}
		return widthSet;
	}
}
