package org.contentmine.eucl.euclid.euclid;

import java.util.Set;
import java.util.TreeSet;

import org.contentmine.eucl.euclid.Real2Range;
import org.contentmine.eucl.euclid.Real2RangeComparator;
import org.contentmine.eucl.euclid.RealComparator;
import org.contentmine.eucl.euclid.RealRange;
import org.contentmine.eucl.euclid.RealRangeComparator;
import org.junit.Assert;
import org.junit.Test;

public class Real2RangeComparatorTest {

	public final static Double ZERO = 0.0;
	public final static Double EPS = 0.01;
	public final static Double ONE = 1.0;
	public final static Double TWO = 2.0;
	public final static Double THREE = 3.0;
	public final static RealRange ONE_TWO = new RealRange(ONE, TWO);
	public final static RealRange ZERO_ONE = new RealRange(ZERO, ONE);
	public final static Real2Range ONE_TWO_ONE_TWO = new Real2Range(ONE_TWO, ONE_TWO);
	public final static Real2Range ZERO_ONE_ONE_TWO = new Real2Range(ZERO_ONE, ONE_TWO);
	public final static Real2Range ONE_TWO_ZERO_ONE = new Real2Range(ONE_TWO, ZERO_ONE);
	public final static Double SQR3 = Math.sqrt(THREE);
	
	public final static RealComparator RZERO = new RealComparator(0.0);
	public final static RealComparator REPS = new RealComparator(EPS);
	public final static RealRangeComparator RRZERO = new RealRangeComparator(RZERO);
	public final static RealRangeComparator RREPS = new RealRangeComparator(REPS);
	
	@Test
	public void testDummy() {
		
	}
	@Test
	public void comparatorTest() {
		Real2RangeComparator comparator = new Real2RangeComparator(RRZERO);
		Assert.assertEquals(0, comparator.compare(ONE_TWO_ONE_TWO, ONE_TWO_ONE_TWO));
		Assert.assertEquals(-1, comparator.compare(ZERO_ONE_ONE_TWO, ONE_TWO_ONE_TWO));
		Assert.assertEquals(-1, comparator.compare(ONE_TWO_ONE_TWO, ZERO_ONE_ONE_TWO));
		Assert.assertEquals(-1, comparator.compare(ONE_TWO_ONE_TWO, ONE_TWO_ZERO_ONE));
	}
	
	@Test
	public void comparatorTest1() {
		Real2RangeComparator comparator = new Real2RangeComparator(RREPS);
		Assert.assertEquals(0, comparator.compare(ONE_TWO_ONE_TWO, new Real2Range(ONE_TWO, new RealRange(ONE-EPS/2., TWO+EPS/2.))));
		// both min and max in first range are larger
		Assert.assertEquals(-1, comparator.compare(ONE_TWO_ONE_TWO, new Real2Range(new RealRange(ONE-EPS*2., TWO-EPS*2.), ONE_TWO)));
		// both min and max in first range are smaller
		Assert.assertEquals(-1, comparator.compare(ONE_TWO_ONE_TWO, new Real2Range(new RealRange(ONE-EPS*2., TWO-EPS*2.), ONE_TWO)));
		// this gives -1 because there is a disagreement
		Assert.assertEquals(-1, comparator.compare(ONE_TWO_ONE_TWO, new Real2Range(new RealRange(ONE-EPS*2., TWO+EPS*2.), ONE_TWO)));
	}
	
	/** TreeSet works
	 * 
	 */
	@Test
	public void testTreeSet(){
		Real2RangeComparator comparator = new Real2RangeComparator(RRZERO);
		Set<Real2Range> set = new TreeSet<Real2Range>(comparator);
		set.add(ONE_TWO_ONE_TWO);
		set.add(ZERO_ONE_ONE_TWO);
		set.add(ONE_TWO_ONE_TWO);
		set.add(new Real2Range(ONE_TWO, new RealRange(ONE, TWO-0.001)));
		Assert.assertEquals(3, set.size());
	}
	
	/** TreeSet works
	 * 
	 */
	@Test
	public void testTreeSet1(){
		Real2RangeComparator comparator = new Real2RangeComparator(RREPS);
		Set<Real2Range> set = new TreeSet<Real2Range>(comparator);
		set.add(ONE_TWO_ONE_TWO);
		set.add(ZERO_ONE_ONE_TWO);
		set.add(ONE_TWO_ONE_TWO);
		set.add(new Real2Range(ONE_TWO, new RealRange(ONE, TWO-0.001)));
		set.add(new Real2Range(new RealRange(ONE, TWO-0.001), ONE_TWO));
		Assert.assertEquals(2, set.size());
	}
	
}
