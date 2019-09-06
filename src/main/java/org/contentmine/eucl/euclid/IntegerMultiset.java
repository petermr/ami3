package org.contentmine.eucl.euclid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.log.Log;

import com.google.common.collect.BoundType;
import com.google.common.collect.Multiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

/** represents a single bin with multiple integers
 * 
 * has a range (IntRange) which must initially be set but can be readjusted later.
 * 
 * Typical example is pixels projected onto an axis
 * 
 * @author pm286
 *
 */
public class IntegerMultiset implements Comparable<IntegerMultiset> {


	private static final Logger LOG = Logger.getLogger(IntegerMultiset.class);
	static {
		LOG.setLevel(Level.DEBUG);
	}

	private TreeMultiset<Integer> multiset;
	private IntRange intRange;
	
	private IntegerMultiset() {
		multiset = TreeMultiset.create();
	}

	public IntegerMultiset(IntRange intRange) {
		this();
		this.setIntRange(intRange);
	}

	public void add(Multiset<Integer> values) {
		for (Integer value : values) {
			multiset.add(value);
			intRange.add(value);
		}
	}
	
	/** removes values but does not reset intRange
	 * 
	 * @param values
	 */
	public void removeAll(Multiset<Integer> values) {
		multiset.removeAll(values);
	}
	
	public void setIntRange(IntRange intRange) {
		this.intRange = new IntRange(intRange);
	}
	
	public String toString() {
		return multiset.toString();
	}

	/** get the highest values within a tolerance of the high limit
	 * 
	 * @param delta
	 * @return
	 */
	public SortedMultiset<Integer> getHighValues(int delta) {
		if (intRange == null) {
			throw new RuntimeException("null intRange");
		}
		SortedMultiset<Integer> values = multiset.tailMultiset(intRange.getMax() - delta, BoundType.CLOSED);
		return values;
	}
	
	/** get the lowest values within a tolerance of the low limit
	 * 
	 * @param delta
	 * @return
	 */
	public SortedMultiset<Integer> getLowValues(int delta) {
		if (intRange == null) {
			throw new RuntimeException("null intRange");
		}
		SortedMultiset<Integer> values = multiset.headMultiset(intRange.getMin() + delta, BoundType.CLOSED);
		return values;
	}

	public int size() {
		return multiset == null ? 0 : multiset.size();
	}

	public void resetIntRange(IntRange intRange2) {
		this.intRange = this.intRange.not(intRange2);
	}

	public void add(Integer ii) {
		multiset.add(ii);
	}

	public Multiset<Integer> getMultiset() {
		return multiset;
	}

	/** compares IntegerMultizset based on minimum of intRanges
	 * 
	 * @param im
	 * @return
	 */
	@Override
	public int compareTo(IntegerMultiset im) {
		if (im == null) return 0;
		return this.intRange.minval - im.intRange.minval;
	}
	
	public Set<Integer> elementSet() {
		return multiset.elementSet();
	}
	
	public List<Integer> getSortedValues() {
		return new ArrayList<Integer>(multiset.elementSet());
	}

	public List<IntegerMultiset> splitAtGaps(int gap) {
		List<IntegerMultiset> splitSets = new ArrayList<>();
		List<Integer> gapStartList = findGaps(gap);
		if (gapStartList.size() > 0) {
			LOG.debug("splitAtGaps NYI fully): "+gapStartList);
		}
		return splitSets;
	}

	/** finds gaps within the values >= gap
	 * 
	 * @param gapWidth inclusive
	 * @return lists of values at start of gaps
	 */
	private List<Integer> findGaps(int gapWidth) {
		Integer last = null;
		List<Integer> gapStartList = new ArrayList<Integer>();
		Iterator<Integer> iterator = multiset.iterator();
		while (iterator.hasNext()) {
			Integer value = iterator.next();
			if (last != null) {
				if (value - last >= gapWidth) {
					gapStartList.add(last);
				}
			}
			last = value;
		}
		return gapStartList;
	}

}
