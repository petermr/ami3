package org.contentmine.norma.image.ocr;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.IntRange;
import org.contentmine.eucl.euclid.util.MultisetUtil;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

/** manages character heights and widths
 * */

public class FontGeometry {
	private static final Logger LOG = Logger.getLogger(FontGeometry.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private TextLine textLine;

	private Multiset<Integer> upperLimits;
	private Multiset<Integer> lowerLimits;
	private List<Entry<Integer>> commonestUpperLimits;
	private List<Entry<Integer>> commonestLowerLimits;
	private List<Entry<Integer>> sortedUpperLimits;
	private List<Entry<Integer>> sortedLowerLimits;
	private Entry<Integer> commonestUpperLimit;
	private Entry<Integer> commonestLowerLimit;
	private Multimap<Integer, Entry<IntRange>> yRangeMultisetEntryByLowerValue;
	private Multimap<Integer, Entry<IntRange>> yRangeMultisetEntryByUpperValue;
	private Multiset<Integer> baseYSet;
	private Multiset<Integer> capsYSet;
	private Multiset<Integer> descenderSet;
	private Multiset<Integer> ascenderSet;
	private Multiset<Integer> medianSet;
	private Integer baseY;
	private Integer capsY;
	private Integer descY;
	private Integer ascY;
	private Integer medianY; 
	private double descenderFraction = 0.13; 
	private double ascenderFraction = 0.11; 
	private double medianFraction = 0.13;

	private FontGeometry() {
		
	}
	public FontGeometry(TextLine textLine) {
		this.textLine = textLine;
	}

	void createUpperLowerLimitMultisets() {
		this.getOrCreateYRangesByLowerUpperValues();
		this.createAndPopulateLowerLimits();
		this.createAndPopulateUpperLimits();
		
		return;
	}
	public Integer getBaseY() {
		if (this.commonestUpperLimits == null || this.commonestUpperLimits.size() == 0) {
			LOG.trace("no upper limits");
			return null;
		}
		this.commonestUpperLimit = this.commonestUpperLimits.get(0);
		this.baseYSet = HashMultiset.create();
		this.baseY = this.commonestUpperLimit.getElement();
//		LOG.debug("baseY "+this.baseY);
		return this.baseY;
	}

	public Integer getCapsY() {
		if (this.commonestLowerLimits == null || this.commonestLowerLimits.size() == 0) {
			LOG.trace("no lower limits");
			return null;
		}
		this.commonestLowerLimit = this.commonestLowerLimits.get(0);
		this.capsYSet = HashMultiset.create();
		this.capsY = this.commonestLowerLimit.getElement();
//		LOG.debug("caps: "+capsY);
		return this.capsY;
	}
	
	private void getDescender() {
		this.descenderSet = HashMultiset.create();
		Set<Integer> upperValues = this.yRangeMultisetEntryByUpperValue.keySet();
		for (Integer upperValue : upperValues) {
			Collection<Entry<IntRange>> ranges = this.yRangeMultisetEntryByUpperValue.get(upperValue);
			double tol = this.descenderFraction  * textLine.getYRange().getRange();
			int delta = upperValue - this.baseY;
//			LOG.debug(delta+" "+tol);
			if (delta > tol) {
				this.descenderSet.add(upperValue, ranges.size());
			}
			
		}
		descY = (descenderSet.size() == 0) ? null:(Integer) MultisetUtil.getCommonestValue(descenderSet);
//		LOG.debug("desc: "+descY);
		
	}

	private void getAscender() {
		this.ascenderSet = HashMultiset.create();
		Set<Integer> lowerValues = this.yRangeMultisetEntryByLowerValue.keySet();
		double tol = this.ascenderFraction  * textLine.getYRange().getRange();
		for (Integer lowerValue : lowerValues) {
			Collection<Entry<IntRange>> ranges = this.yRangeMultisetEntryByLowerValue.get(lowerValue);
			
			int delta = capsY - lowerValue;
			if (delta > tol) {
				this.ascenderSet.add(lowerValue, ranges.size());
			}
			
		}
		this.ascY = (this.ascenderSet.size() == 0) ? null:(Integer) MultisetUtil.getCommonestValue(this.ascenderSet);
//		LOG.debug("asc: "+this.ascenderSet);
		
	}

	private void getMedian() {
		this.medianSet = HashMultiset.create();
		Set<Integer> upperValues = this.yRangeMultisetEntryByUpperValue.keySet();
		for (Integer upperValue : upperValues) {
			Collection<Entry<IntRange>> ranges = this.yRangeMultisetEntryByLowerValue.get(upperValue);
			double tol = this.medianFraction  * textLine.getYRange().getRange();
//			LOG.debug("upperVal "+upperValue);
			Integer deltaBase = this.baseY == null ? null : this.baseY - upperValue;
			Integer deltaCaps = this.capsY == null ? null : upperValue - capsY;
//			LOG.debug(deltaCaps+"/"+deltaBase+"/"+tol);
			if (deltaBase > tol && deltaCaps > tol) {
				this.medianSet.add(upperValue, ranges.size());
			}
			
		}
		this.medianY = (this.medianSet.size() == 0) ? null:(Integer) MultisetUtil.getCommonestValue(this.medianSet);
//		LOG.debug("median: "+this.medianSet);
	}



	public void createMedianAscDescenders() {
		this.getOrCreateYRangesByLowerUpperValues();
//		LOG.debug("upper "+this.baseY+this.yRangeMultisetEntryByUpperValue);
//		LOG.debug("low "+this.capsY+" "+this.yRangeMultisetEntryByLowerValue);
//		LOG.debug("yRange "+textLine.getYRange()+textLine.getPhrases());
		this.getBaseY();
		this.getCapsY();
		this.getMedian();
		this.getDescender();
		this.getAscender();
//		LOG.debug(ascY+"/"+capsY+"/"+medianY+"/"+baseY+"/"+descY);

	}

	void getOrCreateYRangesByLowerUpperValues() {
		if (this.yRangeMultisetEntryByLowerValue == null || this.yRangeMultisetEntryByUpperValue == null) {
			this.yRangeMultisetEntryByLowerValue = ArrayListMultimap.create();
			this.yRangeMultisetEntryByUpperValue = ArrayListMultimap.create();
			List<Entry<IntRange>> yRangeMultisetEntryList = textLine.getYRangeMultisetEntryList();
			if (yRangeMultisetEntryList == null) {
				LOG.trace("null yRangeMultisetEntryList");
				return;
			}
			for (Entry<IntRange> yRangeMultisetEntry : yRangeMultisetEntryList) {
				IntRange yRange = yRangeMultisetEntry.getElement();
				this.yRangeMultisetEntryByLowerValue.put(yRange.getMin(), yRangeMultisetEntry);
				this.yRangeMultisetEntryByUpperValue.put(yRange.getMax(), yRangeMultisetEntry);
			}
			this.upperLimits = createMultiset(yRangeMultisetEntryByUpperValue);
			this.lowerLimits = createMultiset(yRangeMultisetEntryByLowerValue);
			this.commonestUpperLimits = MultisetUtil.createListSortedByCount(upperLimits);
			this.commonestLowerLimits = MultisetUtil.createListSortedByCount(lowerLimits);
			this.sortedUpperLimits = MultisetUtil.createListSortedByValue(this.upperLimits);
			this.sortedLowerLimits = MultisetUtil.createListSortedByValue(this.lowerLimits);
		}
	}

	private Multiset<Integer> createMultiset(Multimap<Integer, Entry<IntRange>> yRangeMultisetEntryByValue) {
		Multiset<Integer> multiset = HashMultiset.create();
		for (Integer lowerUpper : yRangeMultisetEntryByValue.keySet()) {
			Collection<Entry<IntRange>> entries = yRangeMultisetEntryByValue.get(lowerUpper);
			for (Entry<IntRange> entry : entries) {
				multiset.add(lowerUpper, entry.getCount());
			}
		}
		return multiset;
	}

	void createAndPopulateUpperLimits() {
		upperLimits = HashMultiset.create();
		for (Integer upperValue : yRangeMultisetEntryByUpperValue.keySet()) {
			Collection<Entry<IntRange>> entries = yRangeMultisetEntryByUpperValue.get(upperValue);
			for (Entry<IntRange> entry : entries) {
				int count = entry.getCount();
				int upper = entry.getElement().getMax();
				upperLimits.add(upper, count);
			}
		}
	}

	void createAndPopulateLowerLimits() {
		lowerLimits = HashMultiset.create();
		for (Integer lowerValue : yRangeMultisetEntryByLowerValue.keySet()) {
			Collection<Entry<IntRange>> entries = yRangeMultisetEntryByLowerValue.get(lowerValue);
			for (Entry<IntRange> entry : entries) {
				int count = entry.getCount();
				int lower = entry.getElement().getMin();
				lowerLimits.add(lower, count);
			}
		}
	}


}
