package org.contentmine.eucl.euclid;

import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Comparator;
//import java.util.Iterator;
//import java.util.List;
//import java.util.ListIterator;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.contentmine.eucl.euclid.util.MultisetUtil;
import org.contentmine.graphics.svg.cache.GenericAbstractList;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import georegression.struct.curve.ParabolaGeneral_F32;

/** an array of multisets representing bins
 * supports an array of Multiset<Integer> created
 * from a single Multiset<Integer>, i.e.
 * the total set is chopped into bins
 * 
 * @author pm286
 *
 */
public class IntegerMultisetList extends GenericAbstractList<IntegerMultiset> {
	private static final Logger LOG = Logger.getLogger(IntegerMultisetList.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	public int binsize;
	public int minval;
	public int maxval;
	public int binCount;
	private int startValue;
	private Multiset<Integer> rawSet;
	
	public IntegerMultisetList()  {
	}
	
	
	/** create a set of bins, each of which is a multiset.
	 * because these are integers, the bins do not lose info
	 * 
	 * @param array list of integers
	 * @param binsize size of bins
	 * @return list of bins (each is a multiset)
	 */
	public IntegerMultisetList createMultisets(IntArray array, int binsize) {
		this.binsize = binsize;
		createLimits(array, binsize);
		ensureGenericList();
//		genericList = new ArrayList<Multiset<Integer>>();
		for (int i = 0; i < binCount; i++) {
			IntRange intRange = new IntRange(minval + i * binsize, minval + (i+1) * binsize);
			IntegerMultiset bin = new IntegerMultiset(intRange);
			genericList.add(bin);
		}
		
		List<Integer> rawList = new ArrayList<Integer>(rawSet);
		for (int i = 0; i < rawList.size(); i++) {
			Integer ii = rawList.get(i);
//			int ibin = (ii - minval + 1) / this.binsize;
			int ibin = (ii - minval) / this.binsize;
			genericList.get(ibin).add(ii);
		}
		return this;

	}

	private void createLimits(IntArray array, int binsize) {
		rawSet = HashMultiset.create();
		rawSet.addAll(array.getIntegerList());
		List<Multiset.Entry<Integer>> entriesSortedByValue = MultisetUtil.createEntryList(
			MultisetUtil.getEntriesSortedByValue(rawSet));
		if (entriesSortedByValue.size() == 0) {
//			LOG.debug("empty sets");
			return;
		}
		minval = entriesSortedByValue.get(0).getElement();
		maxval = entriesSortedByValue.get(entriesSortedByValue.size() - 1).getElement();
		startValue = getStartValue();
		calculateBinCount();  
	}

	private int calculateBinCount() {
		binCount = ((maxval - startValue ) / binsize) + 1;
		return binCount;
	}

	private int getStartValue() {
		startValue = (minval / binsize ) * binsize; // use delta granularity
		return startValue;
	}

	public int getBinsize() {
		return binsize;
	}

	public void setBinsize(int binsize) {
		this.binsize = binsize;
	}

	public int getMinval() {
		return minval;
	}

	public int getMaxval() {
		return maxval;
	}

	public int getBinCount() {
		return binCount;
	}

	public void setBinCount(int binCount) {
		this.binCount = binCount;
	}

	/** iterates through bins to see if any neighbours can be transferred.
	 * 
	 * if bin[i] contains values within bounds of bin[i+1] and count(bin[i] < count(bin[i+1])
	 * transfers those from bin[i] to bin[i+1]
	 * if bin[i+1] contains values within bounds of bin[i] and count(bin[i+1] < count(bin[i])
	 * transfers those from bin[i+1] to bin[i]
	 * 
	 * This is empirical and could be unstable. delta should be "much less" than
	 * bin width
	 * 
	 * @param delta max value inclusive for transferring
	 * 
	 */
	public void mergeNeighbouringMultisets(int delta) {
		for (int i = 0; i < genericList.size() - 1; i++) {
			IntegerMultiset bini = genericList.get(i);
			IntegerMultiset binii = genericList.get(i + 1);
			Multiset<Integer> highValsi = bini.getHighValues(delta);
			Multiset<Integer> lowValsii = binii.getLowValues(delta);
			if (highValsi.size() > 0 && lowValsii.size() > 0) {
				if (highValsi.size() < lowValsii.size()) {
//					LOG.debug("move high " + highValsi + " from " + bini + " up to " + binii);
					moveValsFromTo(highValsi, bini, binii);
				} else {
//					LOG.debug("move low " + lowValsii + "from " + binii + " down to " + bini);
					moveValsFromTo(lowValsii, binii, bini);
				}
			}
		}
	}

	private void moveValsFromTo(Multiset<Integer> values, IntegerMultiset fromSet, IntegerMultiset toSet) {
		toSet.add(values);
		fromSet.removeAll(values);
		IntRange intRange = new IntRange();
		for (Integer value : values) {
			intRange.add(value);
		}
		fromSet.resetIntRange(intRange);
	}


	public List<IntegerMultiset> getList() {
		return genericList;
	}

	public void removeEmptyMultisets() {
		for (int i = genericList.size() - 1; i >= 0; i--) {
			IntegerMultiset bin = genericList.get(i);
			if (bin.size() == 0) {
				genericList.remove(i);
			}
		}
	}

	/** split multisets into 2 if a gap in middle */
	
	public void splitMultisets(int gap) {
		for (int i = genericList.size() - 1; i >= 0; i--) {
			IntegerMultiset bin = genericList.get(i);
			List<IntegerMultiset> splitBins = bin.splitAtGaps(gap);
			if (splitBins.size() > 1) {
				genericList.remove(i);
				// insert new bins
				for (int j = splitBins.size() - 1; j >= 0; j--) {
					genericList.add(i, splitBins.get(j));
				}
			}
		}
	}
		


}
